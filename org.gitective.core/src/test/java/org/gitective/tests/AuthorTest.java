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
import org.gitective.core.filter.commit.AndCommitFilter;
import org.gitective.core.filter.commit.AuthorFilter;
import org.gitective.core.filter.commit.AuthorSetFilter;
import org.gitective.core.filter.commit.CommitListFilter;
import org.gitective.core.service.CommitService;

/**
 * Unit tests of author filters
 */
public class AuthorTest extends GitTestCase {

	/**
	 * Test {@link AuthorFilter}
	 * 
	 * @throws Exception
	 */
	public void testAuthorFilter() throws Exception {
		add("file.txt", "a");
		PersonIdent findUser = new PersonIdent("find user", "find@user.com");
		setUser(findUser);
		RevCommit commit1 = add("file.txt", "b");
		RevCommit commit2 = add("file.txt", "c");
		AuthorFilter filter = new AuthorFilter(findUser);
		CommitListFilter commits = new CommitListFilter();
		CommitService service = new CommitService(testRepo);
		service.walkFromHead(new AndCommitFilter().add(filter).add(commits));
		assertEquals(2, commits.getCommits().size());
		assertEquals(commit2, commits.getCommits().get(0));
		assertEquals(commit1, commits.getCommits().get(1));
	}

	/**
	 * Test {@link AuthorSetFilter}
	 * 
	 * @throws Exception
	 */
	public void testAuthorSetFilter() throws Exception {
		add("file.txt", "a");
		PersonIdent findUser = new PersonIdent("find user", "find@user.com");
		setUser(findUser);
		add("file.txt", "b");
		AuthorSetFilter filter = new AuthorSetFilter();
		assertTrue(filter.getPersons().isEmpty());
		assertFalse(filter.getPersons().contains(findUser));
		CommitService service = new CommitService(testRepo);
		service.walkFromHead(filter);
		assertEquals(2, filter.getPersons().size());
		assertTrue(filter.getPersons().contains(findUser));
	}
}
