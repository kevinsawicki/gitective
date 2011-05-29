/******************************************************************************
 *  Copyright (c) 2011, Kevin Sawicki <kevinsawicki@gmail.com>
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *****************************************************************************/
package org.gitective.tests;

import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepository;
import org.gitective.core.CommitUtils;
import org.gitective.core.GitException;

/**
 * Unit tests of {@link CommitUtils#getLatest(org.eclipse.jgit.lib.Repository)}
 */
public class LatestTest extends GitTestCase {

	/**
	 * Test retrieving latest commit from repository
	 * 
	 * @throws Exception
	 */
	public void testLatest() throws Exception {
		RevCommit commit = add("file.txt", "content");
		assertEquals(commit,
				CommitUtils.getLatest(new FileRepository(testRepo)));
	}

	/**
	 * Test retrieving latest commit from repository
	 * 
	 * @throws Exception
	 */
	public void testLatestOnEmptyRepository() throws Exception {
		try {
			CommitUtils.getLatest(new FileRepository(testRepo));
			fail("Exception not thrown");
		} catch (GitException e) {
			assertNotNull(e.getMessage());
		}
	}

	/**
	 * Test lookup of commit by id
	 * 
	 * @throws Exception
	 */
	public void testById() throws Exception {
		RevCommit commit = add("file.txt", "content");
		assertEquals(commit,
				CommitUtils.getCommit(new FileRepository(testRepo), commit));
	}
}
