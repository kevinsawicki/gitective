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
import org.gitective.core.filter.commit.CommitMessageFindFilter;
import org.gitective.core.filter.commit.PatternFindCommitFilter;
import org.gitective.core.service.CommitFinder;
import org.junit.Test;

/**
 * Unit tests of {@link PatternFindCommitFilter}
 */
public class FindTest extends GitTestCase {

	/**
	 * Test match
	 * 
	 * @throws Exception
	 */
	@Test
	public void matc() throws Exception {
		add("file.txt", "content", "matchmiddlehere");
		CommitMessageFindFilter find = new CommitMessageFindFilter("middle");
		CommitCountFilter count = new CommitCountFilter();
		CommitFinder service = new CommitFinder(testRepo);
		service.setFilter(new AndCommitFilter(find, count)).find();
		assertEquals(1, count.getCount());
	}

	/**
	 * Test non-match
	 * 
	 * @throws Exception
	 */
	@Test
	public void nonMatch() throws Exception {
		add("file.txt", "content", "matchmiddlehere");
		CommitMessageFindFilter find = new CommitMessageFindFilter("nomatch");
		CommitCountFilter count = new CommitCountFilter();
		CommitFinder service = new CommitFinder(testRepo);
		service.setFilter(new AndCommitFilter(find, count)).find();
		assertEquals(0, count.getCount());
	}

	/**
	 * Test {@link CommitMessageFindFilter#clone()}
	 * 
	 * @throws Exception
	 */
	@Test
	public void cloneFitler() throws Exception {
		CommitMessageFindFilter find = new CommitMessageFindFilter("content");
		RevFilter clone = find.clone();
		assertNotNull(clone);
		assertNotSame(find, clone);
		assertTrue(clone instanceof CommitMessageFindFilter);
	}
}
