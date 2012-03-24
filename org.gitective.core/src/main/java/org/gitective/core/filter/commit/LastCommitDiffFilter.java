/*
 * Copyright (c) 2012 Kevin Sawicki <kevinsawicki@gmail.com>
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

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.revwalk.filter.RevFilter;
import org.eclipse.jgit.treewalk.TreeWalk;

/**
 * Filter that includes commits until all the tree paths in the first commit
 * visited have been attributed to the last commit that edited each path in the
 * initial tree.
 * <p>
 * Using this filter starting at the HEAD commit will give you the commit that
 * last modified each file current in the HEAD tree.
 */
public class LastCommitDiffFilter extends CommitDiffFilter {

	private final Map<String, RevCommit> commits = new HashMap<String, RevCommit>();

	private int remaining;

	private RevTree tree;

	/**
	 * Create filter
	 */
	public LastCommitDiffFilter() {
		super();
	}

	/**
	 * Create filter
	 *
	 * @param detectRenames
	 */
	public LastCommitDiffFilter(final boolean detectRenames) {
		super(detectRenames);
	}

	@Override
	public boolean include(final RevWalk walker, final RevCommit commit,
			final Collection<DiffEntry> diffs) throws IOException {
		if (tree == null) {
			tree = commit.getTree();
			final TreeWalk treeWalk = new TreeWalk(walker.getObjectReader());
			treeWalk.setRecursive(true);
			treeWalk.addTree(tree);
			while (treeWalk.next())
				commits.put(treeWalk.getPathString(), null);
			remaining = commits.size();
		}

		for (DiffEntry diff : diffs) {
			String path;
			switch (diff.getChangeType()) {
			case DELETE:
				path = diff.getOldPath();
				break;
			default:
				path = diff.getNewPath();
				break;
			}

			if (!commits.containsKey(path))
				continue;
			RevCommit blobCommit = commits.get(path);
			if (blobCommit != null)
				continue;

			commits.put(path, commit);
			remaining--;
			if (remaining == 0)
				return include(false);
		}

		return true;
	}

	/**
	 * Get tree being indexed
	 *
	 * @return tree
	 */
	public RevTree getTree() {
		return tree;
	}

	/**
	 * Get all paths in the tree returned from {@link #getTree()} mapped to the
	 * last commit that modified the path
	 *
	 * @return commits
	 */
	public Map<String, RevCommit> getCommits() {
		return commits;
	}

	@Override
	public CommitFilter reset() {
		commits.clear();
		tree = null;
		remaining = 0;
		return super.reset();
	}

	@Override
	public RevFilter clone() {
		return new LastCommitDiffFilter(detectRenames);
	}
}
