/******************************************************************************
 *  Copyright (c) 2011, Kevin Sawicki <kevinsawicki@gmail.com>
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *****************************************************************************/
package org.gitective.tests;

import org.eclipse.jgit.revwalk.filter.RevFilter;
import org.gitective.core.filter.commit.CommitCountFilter;
import org.gitective.core.filter.commit.CommitLimitFilter;
import org.gitective.core.filter.commit.OrCommitFilter;
import org.gitective.core.service.CommitService;

/**
 * Unit tests of {@link OrCommitFilter}
 */
public class OrTest extends GitTestCase {

	/**
	 * Test of {@link OrCommitFilter#clone()}
	 */
	public void testClone() {
		CommitCountFilter count = new CommitCountFilter();
		OrCommitFilter or = new OrCommitFilter();
		or.add(count);
		RevFilter clone = or.clone();
		assertNotNull(clone);
		assertNotSame(or, clone);
		assertTrue(clone instanceof OrCommitFilter);
	}

	/**
	 * Test second filter in a {@link OrCommitFilter} not being called when the
	 * first filter matches.
	 * 
	 * @throws Exception
	 */
	public void testSecondNotCalled() throws Exception {
		add("file.txt", "test");
		add("file.txt", "testa");
		CommitLimitFilter limit = new CommitLimitFilter(1);
		CommitCountFilter count = new CommitCountFilter();
		CommitService service = new CommitService(testRepo);
		service.search(new OrCommitFilter().add(limit).add(count));
		assertEquals(1, count.getCount());
	}

}
