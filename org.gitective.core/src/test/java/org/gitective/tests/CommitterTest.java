/******************************************************************************
 *  Copyright (c) 2011 Kevin Sawicki <kevinsawicki@gmail.com>
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *****************************************************************************/
package org.gitective.tests;

import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.filter.RevFilter;
import org.gitective.core.filter.commit.AndCommitFilter;
import org.gitective.core.filter.commit.CommitListFilter;
import org.gitective.core.filter.commit.CommitterFilter;
import org.gitective.core.filter.commit.CommitterSetFilter;
import org.gitective.core.service.CommitService;

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
		CommitService service = new CommitService(testRepo);
		service.walkFromHead(new AndCommitFilter().add(filter).add(commits));
		assertEquals(2, commits.getCommits().size());
		assertEquals(commit2, commits.getCommits().get(0));
		assertEquals(commit1, commits.getCommits().get(1));
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
		CommitService service = new CommitService(testRepo);
		service.walkFromHead(filter);
		assertEquals(2, filter.getPersons().size());
		assertTrue(filter.getPersons().contains(findUser));
	}

}
