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

import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.revwalk.filter.RevFilter;

/**
 * Composite filter that only includes commits that are included by at least one
 * child filter that has been added to this filter. This filter stops matching
 * against child filters when the first child filter matches the current commit.
 */
public class OrCommitFilter extends CompositeCommitFilter {

	/**
	 * Create an empty or commit filter
	 */
	public OrCommitFilter() {
		super();
	}

	/**
	 * Create an or commit filter with given child filters
	 * 
	 * @param filters
	 */
	public OrCommitFilter(final RevFilter... filters) {
		super(filters);
	}

	@Override
	public boolean include(final RevWalk walker, final RevCommit commit)
			throws IOException {
		final int length = filters.length;
		for (int i = 0; i < length; i++)
			if (filters[i].include(walker, commit))
				return true;
		return include(false);
	}

	@Override
	public RevFilter clone() {
		return new OrCommitFilter(cloneFilters());
	}
}
