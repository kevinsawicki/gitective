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
import org.gitective.core.filter.commit.CommitListFilter;
import org.junit.Test;

/**
 * Unit tests of {@link CommitListFilter}
 */
public class CommitListTest extends GitTestCase {

	/**
	 * Unit test of {@link CommitListFilter#clone()}
	 */
	@Test
	public void cloneFilter() {
		CommitListFilter filter = new CommitListFilter();
		assertTrue(filter.getCommits().isEmpty());
		RevFilter clone = filter.clone();
		assertNotNull(clone);
		assertNotSame(filter, clone);
		assertTrue(clone instanceof CommitListFilter);
		assertTrue(((CommitListFilter) clone).getCommits().isEmpty());
	}

	/**
	 * Unit test of {@link CommitListFilter#reset()}
	 * 
	 * @throws Exception
	 */
	@Test
	public void reset() throws Exception {
		RevCommit commit = add("file.txt", "content");

		CommitListFilter filter = new CommitListFilter();
		CommitFinder service = new CommitFinder(testRepo);
		service.setFilter(filter);
		service.find();

		assertFalse(filter.getCommits().isEmpty());
		assertEquals(1, filter.getCommits().size());
		assertEquals(commit, filter.getCommits().get(0));

		filter.reset();

		assertTrue(filter.getCommits().isEmpty());

		service.find();

		assertFalse(filter.getCommits().isEmpty());
		assertEquals(1, filter.getCommits().size());
		assertEquals(commit, filter.getCommits().get(0));
	}
}
