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

import java.util.Collection;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.revwalk.RevCommit;
import org.gitective.core.BlobUtils;

/**
 * Commit diff filter that computes the edits introduced by each commit visited
 * and calls {@link #include(org.eclipse.jgit.revwalk.RevWalk, RevCommit)}.
 */
public class CommitDiffEditFilter extends CommitDiffFilter {

	/**
	 * Create commit diff edit filter
	 */
	public CommitDiffEditFilter() {
		super();
	}

	/**
	 * Create commit diff edit filter
	 *
	 * @param detectRenames
	 */
	public CommitDiffEditFilter(final boolean detectRenames) {
		super(detectRenames);
	}

	/**
	 * Mark the start of a commit being processed
	 * <p>
	 * Sub-classes should override this method instead of overriding
	 * {@link #include(org.eclipse.jgit.revwalk.RevWalk, RevCommit)} and calling
	 * super.
	 *
	 * @param commit
	 * @return this filter
	 */
	protected CommitDiffEditFilter markStart(RevCommit commit) {
		return this;
	}

	/**
	 * Mark the end of a commit being processed
	 *
	 * <p>
	 * Sub-classes should override this method instead of overriding
	 * {@link #include(org.eclipse.jgit.revwalk.RevWalk, RevCommit)} and calling
	 * super.
	 *
	 * @param commit
	 * @return this filter
	 */
	protected CommitDiffEditFilter markEnd(RevCommit commit) {
		return this;
	}

	@Override
	public boolean include(final RevCommit commit,
			final Collection<DiffEntry> diffs) {
		markStart(commit);
		for (DiffEntry diff : diffs) {
			if (!isFileDiff(diff))
				continue;
			if (!include(commit, diff, BlobUtils.diff(repository, diff
					.getOldId().toObjectId(), diff.getNewId().toObjectId())))
				return markEnd(commit).include(false);
		}
		markEnd(commit);
		return true;
	}

	/**
	 * Handle the edits introduced by given commit.
	 * <p>
	 * Sub-classes should override this method. The default implementation call
	 * {@link #include(RevCommit, DiffEntry, Edit)} for each edit.
	 *
	 * @param commit
	 *            non-null
	 * @param diff
	 *            non-null
	 * @param edits
	 *            non-null
	 * @return true to continue, false to abort
	 */
	protected boolean include(final RevCommit commit, final DiffEntry diff,
			final Collection<Edit> edits) {
		for (Edit edit : edits)
			if (!include(commit, diff, edit))
				return false;
		return true;
	}

	/**
	 * Handle the edit introduced by given commit.
	 * <p>
	 * Sub-classes should override this method. The default implementation
	 * returns true in all cases.
	 *
	 * @param commit
	 *            non-null
	 * @param diff
	 *            non-null
	 * @param edit
	 *            non-null
	 * @return true to continue, false to abort
	 */
	protected boolean include(final RevCommit commit, final DiffEntry diff,
			final Edit edit) {
		return true;
	}
}
