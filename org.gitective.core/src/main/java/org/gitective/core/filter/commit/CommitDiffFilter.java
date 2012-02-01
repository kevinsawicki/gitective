/*
 * Copyright (c) 2011 Kevin Sawicki <kevinsawicki@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to
 * deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 */
package org.gitective.core.filter.commit;

import static org.eclipse.jgit.lib.FileMode.TYPE_FILE;
import static org.eclipse.jgit.lib.FileMode.TYPE_MASK;
import static org.eclipse.jgit.lib.NullProgressMonitor.INSTANCE;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffEntry.ChangeType;
import org.eclipse.jgit.diff.RenameDetector;
import org.eclipse.jgit.lib.AbbreviatedObjectId;
import org.eclipse.jgit.lib.FileMode;
import org.eclipse.jgit.lib.MutableObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.gitective.core.TreeUtils;

/**
 * Commit diff filter that computes the differences introduced by each commit
 * visited and calls {@link #include(RevWalk, RevCommit, Collection)}.
 *
 * @see #include(RevWalk, RevCommit, Collection)
 */
public class CommitDiffFilter extends CommitFilter {

	private static class LocalDiffEntry extends DiffEntry {

		public LocalDiffEntry(final String path) {
			oldPath = path;
			newPath = path;
		}

		private LocalDiffEntry setOldMode(final FileMode mode) {
			oldMode = mode;
			return this;
		}

		private LocalDiffEntry setNewMode(final FileMode mode) {
			newMode = mode;
			return this;
		}

		private LocalDiffEntry setChangeType(final ChangeType type) {
			changeType = type;
			return this;
		}

		private LocalDiffEntry setNewId(final AbbreviatedObjectId id) {
			newId = id;
			return this;
		}
	}

	/**
	 * True to detect renames, false otherwise
	 */
	protected final boolean detectRenames;

	/**
	 * Rename detector
	 */
	protected RenameDetector renameDetector;

	/**
	 * Create commit diff filter
	 */
	public CommitDiffFilter() {
		this(false);
	}

	/**
	 * Create commit diff filter
	 *
	 * @param detectRenames
	 *            true to detect renames, false otherwise
	 */
	public CommitDiffFilter(final boolean detectRenames) {
		this.detectRenames = detectRenames;
	}

	@Override
	public CommitFilter setRepository(Repository repository) {
		if (detectRenames)
			renameDetector = new RenameDetector(repository);
		return super.setRepository(repository);
	}

	/**
	 * Create tree walk to compute differences for the given commits
	 * <p>
	 * Sub-classes may override this method to create a custom tree walk with
	 * different filtering options set.
	 * <p>
	 * The last tree in the tree walk is assumed to be the tree of the current
	 * commit
	 *
	 * @param walker
	 * @param commit
	 * @return tree walk
	 */
	protected TreeWalk createTreeWalk(final RevWalk walker,
			final RevCommit commit) {
		final TreeWalk walk = TreeUtils.diffWithParents(walker, commit);
		walk.setRecursive(true);
		return walk;
	}

	@Override
	public boolean include(final RevWalk walker, final RevCommit commit)
			throws IOException {
		final TreeWalk walk = createTreeWalk(walker, commit);
		final List<DiffEntry> diffs;
		final int treeCount = walk.getTreeCount();
		switch (treeCount) {
		case 0:
		case 1:
			diffs = Collections.emptyList();
			break;
		case 2:
			diffs = DiffEntry.scan(walk);
			break;
		default:
			diffs = new ArrayList<DiffEntry>();
			final MutableObjectId currentId = new MutableObjectId();
			final int currentTree = treeCount - 1;
			while (walk.next()) {
				final int currentMode = walk.getRawMode(currentTree);
				int parentMode = 0;
				boolean same = false;
				for (int i = 0; i < currentTree; i++) {
					final int mode = walk.getRawMode(i);
					same = mode == currentMode && walk.idEqual(currentTree, i);
					if (same)
						break;
					parentMode |= mode;
				}
				if (same)
					continue;

				final LocalDiffEntry diff = new LocalDiffEntry(
						walk.getPathString());
				diff.setOldMode(FileMode.fromBits(parentMode));
				diff.setNewMode(FileMode.fromBits(currentMode));
				walk.getObjectId(currentId, currentTree);
				diff.setNewId(AbbreviatedObjectId.fromObjectId(currentId));
				if (parentMode == 0 && currentMode != 0)
					diff.setChangeType(ChangeType.ADD);
				else if (parentMode != 0 && currentMode == 0)
					diff.setChangeType(ChangeType.DELETE);
				else
					diff.setChangeType(ChangeType.MODIFY);
				diffs.add(diff);
			}
		}
		if (detectRenames) {
			renameDetector.reset();
			renameDetector.addAll(diffs);
			return include(walker, commit,
					renameDetector.compute(walker.getObjectReader(), INSTANCE)) ? true
					: include(false);
		} else
			return include(walker, commit, diffs) ? true : include(false);
	}

	/**
	 * Is the given diff entry a file?
	 *
	 * @param diff
	 * @return true if a regular or executable file, false otherwise
	 */
	protected boolean isFileDiff(DiffEntry diff) {
		switch (diff.getChangeType()) {
		case DELETE:
			return TYPE_FILE == (diff.getOldMode().getBits() & TYPE_MASK);
		case ADD:
			return TYPE_FILE == (diff.getNewMode().getBits() & TYPE_MASK);
		default:
			return TYPE_FILE == (diff.getOldMode().getBits() & TYPE_MASK)
					&& TYPE_FILE == (diff.getNewMode().getBits() & TYPE_MASK);
		}
	}

	/**
	 * Handle the differences introduced by given commit.
	 * <p>
	 * Sub-classes should override this method. The default implementation calls
	 * {@link #include(RevCommit, Collection)}.
	 *
	 * @param walker
	 *            non-null
	 * @param commit
	 *            non-null
	 * @param diffs
	 *            non-null
	 * @return true to continue, false to abort
	 */
	public boolean include(final RevWalk walker, final RevCommit commit,
			final Collection<DiffEntry> diffs) {
		return include(commit, diffs);
	}

	/**
	 * Handle the differences introduced by given commit.
	 * <p>
	 * Sub-classes should override this method. The default implementation
	 * returns true in all cases.
	 *
	 * @param commit
	 *            non-null
	 * @param diffs
	 *            non-null
	 * @return true to continue, false to abort
	 */
	public boolean include(final RevCommit commit,
			final Collection<DiffEntry> diffs) {
		return true;
	}
}
