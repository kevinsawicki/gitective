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
import org.gitective.core.filter.commit.CommitListFilter;
import org.gitective.core.service.CommitFinder;
import org.junit.Test;

/**
 * Unit tests of working with tags
 */
public class TagTest extends GitTestCase {

	/**
	 * Test finding commits in all tags
	 * 
	 * @throws Exception
	 */
	@Test
	public void tags() throws Exception {
		RevCommit commit1 = add("f1.txt", "content0");
		tag("t1");
		RevCommit commit2 = add("f2.txt", "content2");

		CommitFinder finder = new CommitFinder(testRepo);
		CommitListFilter commits = new CommitListFilter();
		finder.setFilter(commits).findInTags();
		assertTrue(commits.getCommits().contains(commit1));
		assertFalse(commits.getCommits().contains(commit2));
	}

	/**
	 * Test searching for commits in repository with no tags
	 * 
	 * @throws Exception
	 */
	@Test
	public void noTags() throws Exception {
		RevCommit commit1 = add("f1.txt", "content0");
		CommitFinder finder = new CommitFinder(testRepo);
		CommitListFilter commits = new CommitListFilter();
		finder.setFilter(commits).findInTags();
		assertFalse(commits.getCommits().contains(commit1));
	}
}
