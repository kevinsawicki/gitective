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
import org.gitective.core.filter.commit.AndCommitFilter;
import org.gitective.core.filter.commit.CommitCountFilter;
import org.gitective.core.filter.commit.CommitListFilter;
import org.gitective.core.filter.commit.ParentCountFilter;
import org.gitective.core.service.CommitFinder;

/**
 * Unit tests of {@link ParentCountFilter}
 */
public class ParentTest extends GitTestCase {

	/**
	 * Test match
	 * 
	 * @throws Exception
	 */
	public void testMatch() throws Exception {
		add("file.txt", "abc");
		add("file.txt", "abcd");

		CommitCountFilter count = new CommitCountFilter();
		CommitFinder service = new CommitFinder(testRepo);
		service.setFilter(new AndCommitFilter(new ParentCountFilter(1), count));
		service.find();
		assertEquals(1, count.getCount());
	}

	/**
	 * Test non-match
	 * 
	 * @throws Exception
	 */
	public void testNonMatch() throws Exception {
		add("file.txt", "abc");

		CommitCountFilter count = new CommitCountFilter();
		CommitFinder service = new CommitFinder(testRepo);
		service.setFilter(new AndCommitFilter(new ParentCountFilter(), count));
		service.find();
		assertEquals(0, count.getCount());
	}

	/**
	 * Test of {@link ParentCountFilter#clone()}
	 * 
	 * @throws Exception
	 */
	public void testClone() throws Exception {
		add("file.txt", "abc");
		RevCommit commit2 = add("file.txt", "abcd");

		CommitListFilter commits = new CommitListFilter();
		CommitFinder service = new CommitFinder(testRepo);
		ParentCountFilter parents = new ParentCountFilter(1);
		service.setFilter(new AndCommitFilter(parents, commits));
		service.find();
		assertEquals(commit2, commits.getCommits().get(0));

		RevFilter clone = parents.clone();
		assertNotNull(clone);
		assertNotSame(parents, clone);

		commits = new CommitListFilter();
		service.setFilter(new AndCommitFilter(clone, commits));
		service.find();
		assertEquals(commit2, commits.getCommits().get(0));

	}
}
