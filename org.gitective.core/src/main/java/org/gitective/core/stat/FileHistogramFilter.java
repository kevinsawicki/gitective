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
package org.gitective.core.stat;

import java.util.Collection;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.filter.RevFilter;
import org.gitective.core.filter.commit.CommitDiffFilter;
import org.gitective.core.filter.commit.CommitFilter;

/**
 * Filter that generates a histogram of the modified files introduced by each
 * commit visited.
 */
public class FileHistogramFilter extends CommitDiffFilter {

	private FileHistogram histogram = new FileHistogram();

	/**
	 * Create file histogram filter
	 */
	public FileHistogramFilter() {
		super();
	}

	/**
	 * Create file histogram filter
	 *
	 * @param detectRenames
	 */
	public FileHistogramFilter(final boolean detectRenames) {
		super(detectRenames);
	}

	/**
	 * Get file histogram
	 *
	 * @return histogram
	 */
	public FileHistogram getHistogram() {
		return histogram;
	}

	@Override
	public boolean include(final RevCommit commit,
			final Collection<DiffEntry> diffs) {
		for (DiffEntry diff : diffs)
			histogram.include(commit, diff);
		return true;
	}

	@Override
	public CommitFilter reset() {
		histogram = new FileHistogram();
		return super.reset();
	}

	@Override
	public RevFilter clone() {
		return new FileHistogramFilter(detectRenames);
	}
}
