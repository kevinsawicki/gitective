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
package org.gitective.tests;

import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.filter.RevFilter;
import org.gitective.core.CommitFinder;
import org.gitective.core.filter.commit.AndCommitFilter;
import org.gitective.core.filter.commit.CommitLimitFilter;
import org.gitective.core.filter.commit.LastCommitFilter;
import org.junit.Test;

/**
 * Unit tests of {@link LastCommitFilter}
 */
public class LastTest extends GitTestCase {

	/**
	 * Test storing last commit visited
	 * 
	 * @throws Exception
	 */
	@Test
	public void last() throws Exception {
		add("a.txt", "a");
		RevCommit commit2 = add("a.txt", "b");
		add("a.txt", "c");

		LastCommitFilter last = new LastCommitFilter();
		assertNull(last.getLast());
		CommitLimitFilter limit = new CommitLimitFilter(2);
		AndCommitFilter filter = new AndCommitFilter(limit, last);
		CommitFinder finder = new CommitFinder(testRepo);
		finder.setFilter(filter);
		finder.find();
		assertEquals(commit2, last.getLast());
		finder.find();
		assertEquals(commit2, last.getLast());
		last.reset();
		assertNull(last.getLast());
		limit.reset();
		finder.find();
		assertEquals(commit2, last.getLast());
	}

	/**
	 * Test of {@link LastCommitFilter#clone()}
	 */
	@Test
	public void cloneFilter() {
		LastCommitFilter filter = new LastCommitFilter();
		assertNull(filter.getLast());
		RevFilter clone = filter.clone();
		assertNotNull(clone);
		assertNotSame(filter, clone);
		assertTrue(clone instanceof LastCommitFilter);
		assertNull(((LastCommitFilter) clone).getLast());
	}
}
