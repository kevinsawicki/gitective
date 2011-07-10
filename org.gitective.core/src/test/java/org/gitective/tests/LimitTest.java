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

import org.eclipse.jgit.revwalk.filter.RevFilter;
import org.gitective.core.filter.commit.AndCommitFilter;
import org.gitective.core.filter.commit.CommitCountFilter;
import org.gitective.core.filter.commit.CommitLimitFilter;
import org.gitective.core.service.CommitFinder;
import org.junit.Test;

/**
 * Unit tests of {@link CommitLimitFilter}
 */
public class LimitTest extends GitTestCase {

	/**
	 * Test of {@link CommitLimitFilter#clone()}
	 * 
	 * @throws Exception
	 */
	@Test
	public void cloneFilter() throws Exception {
		add("file1.txt", "a");
		add("file1.txt", "b");
		add("file1.txt", "c");
		CommitFinder service = new CommitFinder(testRepo);
		CommitCountFilter count = new CommitCountFilter();
		CommitLimitFilter limit = new CommitLimitFilter(2);
		service.setFilter(new AndCommitFilter(limit, count)).find();
		assertEquals(2, count.getCount());
		count.reset();
		RevFilter clone = limit.clone();
		assertNotNull(clone);
		assertNotSame(limit, clone);
		service.setFilter(new AndCommitFilter(clone, count)).find();
		assertEquals(2, count.getCount());
	}

	/**
	 * Test of {@link CommitLimitFilter#reset()}
	 * 
	 * @throws Exception
	 */
	@Test
	public void reset() throws Exception {
		add("file1.txt", "a");
		CommitFinder service = new CommitFinder(testRepo);
		CommitCountFilter count = new CommitCountFilter();
		CommitLimitFilter limit = new CommitLimitFilter(2);
		service.setFilter(new AndCommitFilter(limit, count));
		service.find();
		assertEquals(1, count.getCount());
		service.find();
		assertEquals(2, count.getCount());
		service.find();
		assertEquals(2, count.getCount());
		limit.reset();
		service.find();
		assertEquals(3, count.getCount());
	}

	/**
	 * Test limiting number of commits in walk
	 * 
	 * @throws Exception
	 */
	@Test
	public void limit() throws Exception {
		add("file1.txt", "a");
		add("file2.txt", "b");
		add("file3.txt", "c");
		CommitFinder service = new CommitFinder(testRepo);
		CommitCountFilter count = new CommitCountFilter();
		service.setFilter(new AndCommitFilter(new CommitLimitFilter(1), count));
		service.find();
		assertEquals(1, count.getCount());
		count.reset();
		service.setFilter(new AndCommitFilter(new CommitLimitFilter(3), count));
		service.find();
		assertEquals(3, count.getCount());
	}
}
