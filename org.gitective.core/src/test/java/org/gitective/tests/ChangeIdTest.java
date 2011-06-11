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

import org.gitective.core.filter.commit.AndCommitFilter;
import org.gitective.core.filter.commit.ChangeIdFilter;
import org.gitective.core.filter.commit.CommitCountFilter;
import org.gitective.core.service.CommitFinder;

/**
 * Unit tests of {@link ChangeIdFilter}
 */
public class ChangeIdTest extends GitTestCase {

	/**
	 * Test match
	 * 
	 * @throws Exception
	 */
	public void testMatch() throws Exception {
		add("file.txt", "patch",
				"fixes a bug\nChange-Id: I12345abcde12345abcde12345abcde12345abcde");

		CommitFinder service = new CommitFinder(testRepo);
		CommitCountFilter count = new CommitCountFilter();
		service.setFilter(new AndCommitFilter(new ChangeIdFilter(), count));
		service.find();
		assertEquals(1, count.getCount());

		service.setFilter(new AndCommitFilter(new ChangeIdFilter().clone(),
				count));
		service.find();
		assertEquals(2, count.getCount());
	}

	/**
	 * Test non-match
	 * 
	 * @throws Exception
	 */
	public void testNonMatch() throws Exception {
		add("file.txt", "patch",
				"fixes a bug\nChange-Id: I12345abcde12345abxyz12345abcde12345abcde");

		CommitFinder service = new CommitFinder(testRepo);
		CommitCountFilter count = new CommitCountFilter();
		service.setFilter(new AndCommitFilter(new ChangeIdFilter(), count));
		service.find();
		assertEquals(0, count.getCount());
	}

}
