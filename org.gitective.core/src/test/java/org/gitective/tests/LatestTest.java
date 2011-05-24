/******************************************************************************
 *  Copyright (c) 2011, Kevin Sawicki <kevinsawicki@gmail.com>
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *****************************************************************************/
package org.gitective.tests;

import org.eclipse.jgit.revwalk.RevCommit;
import org.gitective.core.GitException;
import org.gitective.core.service.CommitService;

/**
 * Unit tests of {@link CommitService#getLatest()}
 */
public class LatestTest extends GitTestCase {

	/**
	 * Test retrieving latest commit from repository
	 * 
	 * @throws Exception
	 */
	public void testLatest() throws Exception {
		RevCommit commit = add("file.txt", "content");
		CommitService service = new CommitService(testRepo);
		assertEquals(commit, service.getLatest());
	}

	/**
	 * Test retrieving latest commit from repository
	 * 
	 * @throws Exception
	 */
	public void testLatestOnEmptyRepository() throws Exception {
		CommitService service = new CommitService(testRepo);
		try {
			service.getLatest();
			fail("Exception not thrown");
		} catch (GitException e) {
			assertNotNull(e);
		}
	}
}
