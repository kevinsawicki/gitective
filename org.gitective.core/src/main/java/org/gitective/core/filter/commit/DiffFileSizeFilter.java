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

import java.util.Collection;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.filter.RevFilter;

/**
 * Filter for including commits that introduced a configurable number of file
 * differences
 */
public class DiffFileSizeFilter extends CommitDiffFilter {

	private final int total;

	private int count;

	/**
	 * Create a filter that includes commits that introduced a minimum number of
	 * file differences
	 *
	 * @param detectRenames
	 * @param diffTotal
	 */
	public DiffFileSizeFilter(final boolean detectRenames, final int diffTotal) {
		super(detectRenames);
		total = diffTotal;
	}

	/**
	 * Create a filter that includes commits that introduced a minimum number of
	 * file differences
	 *
	 * @param diffTotal
	 */
	public DiffFileSizeFilter(final int diffTotal) {
		this(false, diffTotal);
	}

	/**
	 * Get the configured difference total
	 *
	 * @return total
	 */
	public int getTotal() {
		return total;
	}

	@Override
	public boolean include(final RevCommit commit,
			final Collection<DiffEntry> diffs) {
		for (DiffEntry diff : diffs)
			switch (diff.getChangeType()) {
			case ADD:
			case MODIFY:
			case DELETE:
				count++;
				if (count >= total)
					return true;
				break;
			}
		return false;
	}

	@Override
	public RevFilter clone() {
		return new DiffFileSizeFilter(detectRenames, total);
	}
}
