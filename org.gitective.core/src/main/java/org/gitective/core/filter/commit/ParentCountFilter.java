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

import static java.lang.Integer.MAX_VALUE;

import java.io.IOException;

import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.revwalk.filter.RevFilter;

/**
 * Commit filter that includes commits that match the range of number of
 * parents.
 */
public class ParentCountFilter extends CommitFilter {

	private final int min;

	private final int max;

	/**
	 * Create a parent count filter that includes commits that have at least 2
	 * parents.
	 */
	public ParentCountFilter() {
		this(2);
	}

	/**
	 * Create a filter that includes commits that have a parent commit count of
	 * at least the number specified.
	 *
	 * @param min
	 *            minimum number of parent commits (inclusive)
	 */
	public ParentCountFilter(final int min) {
		this(min, MAX_VALUE);
	}

	/**
	 * Create a filter that includes commits that have a parent commit count
	 * that falls inclusively in the specified range.
	 *
	 * @param min
	 *            minimum number of parent commits (inclusive)
	 * @param max
	 *            maximum number of parent commits (inclusive)
	 */
	public ParentCountFilter(final int min, final int max) {
		this.min = min;
		this.max = max;
	}

	@Override
	public boolean include(final RevWalk walker, final RevCommit commit)
			throws IOException {
		final int parents = commit.getParentCount();
		if (parents < min || parents > max)
			return include(false);
		return true;
	}

	@Override
	public RevFilter clone() {
		return new ParentCountFilter(min, max);
	}
}
