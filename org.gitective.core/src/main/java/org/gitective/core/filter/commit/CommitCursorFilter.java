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

import java.io.IOException;

import org.eclipse.jgit.errors.StopWalkException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.revwalk.filter.RevFilter;
import org.gitective.core.Assert;

/**
 * Cursor filter that retains the latest commit if not included by the wrapped
 * filter. This can be useful for retaining the commit to start subsequent walks
 * when walking commits in blocks.
 */
public class CommitCursorFilter extends CommitFilter {

	private RevCommit last;

	private RevFilter filter;

	/**
	 * Create cursor filter that retains last commit when not included by the
	 * given filter.
	 * 
	 * @param filter
	 */
	public CommitCursorFilter(RevFilter filter) {
		Assert.notNull("Filter cannot be null", filter);
		this.filter = filter;
	}

	@Override
	public boolean include(RevWalk walker, RevCommit commit) throws IOException {
		try {
			boolean include = filter.include(walker, commit);
			last = include ? null : commit;
			return include;
		} catch (StopWalkException e) {
			last = commit;
			throw e;
		}
	}

	/**
	 * Get last commit visited.
	 * 
	 * @return commit
	 */
	public RevCommit getLast() {
		return last;
	}

	@Override
	public CommitFilter reset() {
		last = null;
		if (filter instanceof CommitFilter)
			((CommitFilter) filter).reset();
		return super.reset();
	}

	@Override
	public RevFilter clone() {
		return new CommitCursorFilter(filter);
	}

}
