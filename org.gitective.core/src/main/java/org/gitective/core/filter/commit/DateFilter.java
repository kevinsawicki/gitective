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
import java.util.Date;

import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.gitective.core.Assert;

/**
 * Date commit filter that includes commit until one if found that is older than
 * the configured date.
 */
public abstract class DateFilter extends CommitFilter {

	/**
	 * Time
	 */
	protected final long time;

	/**
	 * Create date filter
	 * 
	 * @param time
	 */
	public DateFilter(final long time) {
		this.time = time;
	}

	/**
	 * Create date filter from date
	 * 
	 * @param date
	 *            must be non-null
	 */
	public DateFilter(final Date date) {
		Assert.notNull("Date cannot be null", date);
		time = date.getTime();
	}

	@Override
	public boolean include(final RevWalk walker, final RevCommit commit)
			throws IOException {
		final Date date = getDate(commit);
		return include(date != null && time <= date.getTime());
	}

	/**
	 * Get date from commit to compare against
	 * 
	 * @param commit
	 * @return date
	 */
	protected abstract Date getDate(RevCommit commit);

}
