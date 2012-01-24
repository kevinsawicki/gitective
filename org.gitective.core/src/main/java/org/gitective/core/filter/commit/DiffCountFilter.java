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
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.revwalk.filter.RevFilter;

/**
 * Filter that include commits based on number of differences introduced in each
 * commit visited.
 */
public class DiffCountFilter extends CommitDiffEditFilter {

	private int count;

	/**
	 * Create diff count filter
	 */
	public DiffCountFilter() {
		super();
	}

	/**
	 * Create diff count filter
	 *
	 * @param detectRenames
	 */
	public DiffCountFilter(final boolean detectRenames) {
		super(detectRenames);
	}

	@Override
	protected CommitDiffEditFilter markStart(final RevCommit commit) {
		count = 0;
		return super.markStart(commit);
	}

	@Override
	public boolean include(final RevWalk walker, final RevCommit commit,
			final Collection<DiffEntry> diffs) {
		super.include(walker, commit, diffs);
		return include(commit, diffs, count) ? true : include(false);
	}

	protected boolean include(RevCommit commit, DiffEntry diff,
			Collection<Edit> edits) {
		for (Edit edit : edits)
			switch (edit.getType()) {
			case DELETE:
				count += edit.getLengthA();
				break;
			case INSERT:
			case REPLACE:
				count += edit.getLengthB();
				break;
			default:
				break;
			}
		return true;
	}

	/**
	 * Should the commit introducing the given number of differences be
	 * included?
	 * <p>
	 * Sub-classes should override this method. The default implementation
	 * returns true in all cases.
	 *
	 * @param commit
	 * @param diffs
	 * @param diffCount
	 * @return true to continue, false to about
	 */
	protected boolean include(final RevCommit commit,
			final Collection<DiffEntry> diffs, final int diffCount) {
		return true;
	}

	@Override
	public RevFilter clone() {
		return new DiffCountFilter(detectRenames);
	}
}
