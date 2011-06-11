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

import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.filter.RevFilter;
import org.gitective.core.filter.commit.AndCommitFilter;
import org.gitective.core.filter.commit.CommitListFilter;
import org.gitective.core.filter.commit.CommitterFilter;
import org.gitective.core.filter.commit.CommitterSetFilter;
import org.gitective.core.service.CommitFinder;

/**
 * Unit tests of committer filters
 */
public class CommitterTest extends GitTestCase {

	/**
	 * Test clone of {@link CommitterFilter}
	 */
	public void testCloneCommitterFilter() {
		CommitterFilter filter = new CommitterFilter(committer);
		RevFilter cloned = filter.clone();
		assertNotNull(cloned);
		assertNotSame(filter, cloned);
		assertTrue(filter instanceof CommitterFilter);
	}

	/**
	 * Test clone of {@link CommitterSetFilter}
	 */
	public void testCloneCommitterSetFilter() {
		CommitterSetFilter filter = new CommitterSetFilter();
		RevFilter cloned = filter.clone();
		assertNotNull(cloned);
		assertNotSame(filter, cloned);
		assertTrue(filter instanceof CommitterSetFilter);
	}

	/**
	 * Test of {@link CommitterFilter}
	 * 
	 * @throws Exception
	 */
	public void testCommitterFilter() throws Exception {
		add("file.txt", "a");
		PersonIdent findUser = new PersonIdent("find user", "find@user.com");
		committer = findUser;
		RevCommit commit1 = add("file.txt", "b");
		RevCommit commit2 = add("file.txt", "c");
		CommitterFilter filter = new CommitterFilter(findUser);
		CommitListFilter commits = new CommitListFilter();
		CommitFinder service = new CommitFinder(testRepo);
		service.setFilter(new AndCommitFilter(filter, commits));
		service.find();
		assertEquals(2, commits.getCommits().size());
		assertEquals(commit2, commits.getCommits().get(0));
		assertEquals(commit1, commits.getCommits().get(1));
	}

	/**
	 * Test non-match of {@link CommitterFilter}
	 * 
	 * @throws Exception
	 */
	public void testNonMatch() throws Exception {
		add("file.txt", "a");
		CommitterFilter filter = new CommitterFilter("not the committer",
				"not@committer.org");
		CommitListFilter commits = new CommitListFilter();
		CommitFinder service = new CommitFinder(testRepo);
		service.setFilter(new AndCommitFilter(filter, commits));
		service.find();
		assertEquals(0, commits.getCommits().size());
	}

	/**
	 * Test of {@link CommitterSetFilter}
	 * 
	 * @throws Exception
	 */
	public void testCommitterSetFilter() throws Exception {
		add("file.txt", "a");
		PersonIdent findUser = new PersonIdent("find user", "find@user.com");
		committer = findUser;
		add("file.txt", "b");
		CommitterSetFilter filter = new CommitterSetFilter();
		assertTrue(filter.getPersons().isEmpty());
		assertFalse(filter.getPersons().contains(findUser));
		CommitFinder service = new CommitFinder(testRepo);
		service.setFilter(filter).find();
		assertEquals(2, filter.getPersons().size());
		assertTrue(filter.getPersons().contains(findUser));
	}

}
