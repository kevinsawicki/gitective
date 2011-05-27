/******************************************************************************
 *  Copyright (c) 2011, Kevin Sawicki <kevinsawicki@gmail.com>
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *****************************************************************************/
package org.gitective.tests;

import java.io.File;

import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepository;
import org.gitective.core.filter.commit.CommitCountFilter;
import org.gitective.core.service.CommitService;

/**
 * Unit tests of using multiple repositories from the same service class.
 */
public class MultiRepoTest extends GitTestCase {

	/**
	 * Test service constructors
	 * 
	 * @throws Exception
	 */
	public void testConstructors() throws Exception {
		CommitService service1 = new CommitService(testRepo);
		CommitService service2 = new CommitService(new FileRepository(testRepo));
		CommitService service3 = new CommitService(testRepo.getAbsolutePath());
		assertEquals(testRepo, service1.iterator().next().getDirectory());
		assertEquals(testRepo, service2.iterator().next().getDirectory());
		assertEquals(testRepo, service3.iterator().next().getDirectory());
	}

	/**
	 * Test search two repos from same service
	 * 
	 * @throws Exception
	 */
	public void testTwoRepos() throws Exception {
		add("repo1file.txt", "content");
		File repo2 = initRepo();
		RevCommit repo2Commit = add(repo2, "repo2file.txt", "test");
		assertEquals(0, repo2Commit.getParentCount());

		CommitService service = new CommitService(testRepo, repo2);
		CommitCountFilter count = new CommitCountFilter();
		service.search(count);
		assertEquals(2, count.getCount());
	}

}
