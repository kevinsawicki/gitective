/******************************************************************************
 *  Copyright (c) 2011, Kevin Sawicki <kevinsawicki@gmail.com>
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *****************************************************************************/
package org.gitective.tests;

import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.filter.RevFilter;
import org.gitective.core.filter.commit.CommitListFilter;
import org.gitective.core.service.CommitFinder;

/**
 * Unit tests of {@link CommitListFilter}
 */
public class CommitListTest extends GitTestCase {

	/**
	 * Unit test of {@link CommitListFilter#clone()}
	 */
	public void testClone() {
		CommitListFilter filter = new CommitListFilter();
		assertTrue(filter.getCommits().isEmpty());
		RevFilter clone = filter.clone();
		assertNotNull(clone);
		assertNotSame(filter, clone);
		assertTrue(clone instanceof CommitListFilter);
		assertTrue(((CommitListFilter) clone).getCommits().isEmpty());
	}

	/**
	 * Unit test of {@link CommitListFilter#reset()}
	 * 
	 * @throws Exception
	 */
	public void testReset() throws Exception {
		RevCommit commit = add("file.txt", "content");

		CommitListFilter filter = new CommitListFilter();
		CommitFinder service = new CommitFinder(testRepo);
		service.setRevFilter(filter);
		service.find();

		assertFalse(filter.getCommits().isEmpty());
		assertEquals(1, filter.getCommits().size());
		assertEquals(commit, filter.getCommits().get(0));

		filter.reset();

		assertTrue(filter.getCommits().isEmpty());

		service.find();

		assertFalse(filter.getCommits().isEmpty());
		assertEquals(1, filter.getCommits().size());
		assertEquals(commit, filter.getCommits().get(0));
	}
}
