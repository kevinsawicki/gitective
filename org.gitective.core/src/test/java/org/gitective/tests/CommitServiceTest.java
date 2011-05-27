/******************************************************************************
 *  Copyright (c) 2011, Kevin Sawicki <kevinsawicki@gmail.com>
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *****************************************************************************/
package org.gitective.tests;

import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.revwalk.RevCommit;
import org.gitective.core.GitException;
import org.gitective.core.service.CommitService;

/**
 * Unit tests of {@link CommitService}
 */
public class CommitServiceTest extends GitTestCase {

	/**
	 * Test of {@link CommitService#getLatest()}
	 * 
	 * @throws Exception
	 */
	public void testLatest() throws Exception {
		RevCommit commit = add("file.txt", "abcd");
		assertEquals(commit, new CommitService(testRepo).getLatest());
	}

	/**
	 * Test of {@link CommitService#getLatest()}
	 */
	public void testLatestEmpty() {
		try {
			assertNull(new CommitService(testRepo).getLatest());
			fail("Exception not thrown");
		} catch (GitException e) {
			assertNotNull(e.getMessage());
		}
	}

	/**
	 * Test of
	 * {@link CommitService#lookup(org.eclipse.jgit.lib.Repository, String)}
	 * 
	 * @throws Exception
	 */
	public void testLookup() throws Exception {
		RevCommit commit = add("file.txt", "abcd");
		assertEquals(commit, new CommitService(testRepo).lookup(Constants.HEAD));
	}

	/**
	 * Test of
	 * {@link CommitService#lookup(org.eclipse.jgit.lib.Repository, String)}
	 */
	public void testLookupEmpty() {
		try {
			assertNull(new CommitService(testRepo).lookup(Constants.HEAD));
			fail("Exception not thrown");
		} catch (GitException e) {
			assertNotNull(e.getMessage());
		}
	}
}
