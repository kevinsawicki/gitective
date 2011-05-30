/******************************************************************************
 *  Copyright (c) 2011, Kevin Sawicki <kevinsawicki@gmail.com>
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *****************************************************************************/
package org.gitective.tests;

import org.eclipse.jgit.revwalk.filter.RevFilter;
import org.gitective.core.filter.commit.AndCommitFilter;
import org.gitective.core.filter.commit.CommitCountFilter;
import org.gitective.core.filter.commit.CommitLimitFilter;
import org.gitective.core.service.CommitFinder;

/**
 * Unit tests of {@link CommitLimitFilter}
 */
public class LimitTest extends GitTestCase {

	/**
	 * Test of {@link CommitLimitFilter#clone()}
	 * 
	 * @throws Exception
	 */
	public void testClone() throws Exception {
		add("file1.txt", "a");
		add("file1.txt", "b");
		add("file1.txt", "c");
		CommitFinder service = new CommitFinder(testRepo);
		CommitCountFilter count = new CommitCountFilter();
		CommitLimitFilter limit = new CommitLimitFilter(2);
		service.setFilter(new AndCommitFilter(limit, count)).find();
		assertEquals(2, count.getCount());
		count.reset();
		RevFilter clone = limit.clone();
		assertNotNull(clone);
		assertNotSame(limit, clone);
		service.setFilter(new AndCommitFilter(clone, count)).find();
		assertEquals(2, count.getCount());
	}

	/**
	 * Test of {@link CommitLimitFilter#reset()}
	 * 
	 * @throws Exception
	 */
	public void testReset() throws Exception {
		add("file1.txt", "a");
		CommitFinder service = new CommitFinder(testRepo);
		CommitCountFilter count = new CommitCountFilter();
		CommitLimitFilter limit = new CommitLimitFilter(2);
		service.setFilter(new AndCommitFilter(limit, count));
		service.find();
		assertEquals(1, count.getCount());
		service.find();
		assertEquals(2, count.getCount());
		service.find();
		assertEquals(2, count.getCount());
		limit.reset();
		service.find();
		assertEquals(3, count.getCount());
	}

	/**
	 * Test limiting number of commits in walk
	 * 
	 * @throws Exception
	 */
	public void testLimit() throws Exception {
		add("file1.txt", "a");
		add("file2.txt", "b");
		add("file3.txt", "c");
		CommitFinder service = new CommitFinder(testRepo);
		CommitCountFilter count = new CommitCountFilter();
		service.setFilter(new AndCommitFilter(new CommitLimitFilter(1), count));
		service.find();
		assertEquals(1, count.getCount());
		count.reset();
		service.setFilter(new AndCommitFilter(new CommitLimitFilter(3), count));
		service.find();
		assertEquals(3, count.getCount());
	}

}
