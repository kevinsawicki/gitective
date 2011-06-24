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
import org.gitective.core.filter.commit.OrCommitFilter;
import org.gitective.core.service.CommitFinder;

/**
 * Unit tests of {@link OrCommitFilter}
 */
public class OrTest extends GitTestCase {

	/**
	 * Test of {@link OrCommitFilter#clone()}
	 */
	public void testClone() {
		CommitCountFilter count = new CommitCountFilter();
		OrCommitFilter or = new OrCommitFilter(count);
		RevFilter clone = or.clone();
		assertNotNull(clone);
		assertNotSame(or, clone);
		assertTrue(clone instanceof OrCommitFilter);
	}

	/**
	 * Test second filter in a {@link OrCommitFilter} not being called when the
	 * first filter matches.
	 * 
	 * @throws Exception
	 */
	public void testSecondNotCalled() throws Exception {
		add("file.txt", "test");
		add("file.txt", "testa");
		CommitLimitFilter limit = new CommitLimitFilter(1);
		CommitCountFilter count = new CommitCountFilter();
		CommitFinder service = new CommitFinder(testRepo);
		service.setFilter(new OrCommitFilter(limit, count)).find();
		assertEquals(1, count.getCount());
	}

	/**
	 * Test no matches of the filters in an {@link OrCommitFilter}
	 * 
	 * @throws Exception
	 */
	public void testNoMatches() throws Exception {
		add("file.txt", "test");
		add("file.txt", "testa");
		add("file.txt", "testb");

		CommitLimitFilter limit = new CommitLimitFilter(2);
		CommitCountFilter count = new CommitCountFilter();
		CommitFinder service = new CommitFinder(testRepo);
		service.setFilter(new AndCommitFilter(new OrCommitFilter().add(limit),
				count));
		service.find();
		assertEquals(2, count.getCount());
	}
}
