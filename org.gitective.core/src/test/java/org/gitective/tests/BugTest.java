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
import org.gitective.core.filter.commit.BugFilter;
import org.gitective.core.filter.commit.BugSetFilter;
import org.gitective.core.filter.commit.CommitCountFilter;
import org.gitective.core.service.CommitFinder;

/**
 * Unit tests of {@link BugSetFilter} and {@link BugFilter}
 */
public class BugTest extends GitTestCase {

	/**
	 * Test match of {@link BugFilter}
	 * 
	 * @throws Exception
	 */
	public void testMatch() throws Exception {
		add("file.txt", "content", "fixes NPE\nBug: 1234");

		CommitCountFilter count = new CommitCountFilter();

		new CommitFinder(testRepo).setFilter(
				new AndCommitFilter().add(new BugFilter(), count)).find();

		assertEquals(1, count.getCount());
	}

	/**
	 * Test non-match of {@link BugFilter}
	 * 
	 * @throws Exception
	 */
	public void testNonMatch() throws Exception {
		add("file.txt", "content", "fixes NPE\nIssue: 1234");

		CommitCountFilter count = new CommitCountFilter();

		new CommitFinder(testRepo).setFilter(
				new AndCommitFilter(new BugFilter(), count)).find();

		assertEquals(0, count.getCount());
	}

	/**
	 * Test collecting bug ids from multiple commits referencing bugs
	 * 
	 * @throws Exception
	 */
	public void testMultipleCommits() throws Exception {
		add("file.txt", "content", "fixes NPE\nBug: 1");
		add("file.txt2", "content", "fixes NPE\nBug: a");

		BugSetFilter bugs = new BugSetFilter();
		new CommitFinder(testRepo).setFilter(bugs).find();

		assertEquals(2, bugs.getBugs().size());
		assertTrue(bugs.getBugs().contains("1"));
		assertTrue(bugs.getBugs().contains("a"));
	}

	/**
	 * Test collecting bug ids from commit that references multiple bugs
	 * 
	 * @throws Exception
	 */
	public void testMultipleBugsSameCommit() throws Exception {
		add("file.txt", "content", "fixes NPE\nBug: 123\nBug: 456");

		BugSetFilter bugs = new BugSetFilter();
		new CommitFinder(testRepo).setFilter(bugs).find();

		assertEquals(2, bugs.getBugs().size());
		assertTrue(bugs.getBugs().contains("123"));
		assertTrue(bugs.getBugs().contains("456"));
	}

	/**
	 * Test of {@link BugFilter#clone()}
	 * 
	 * @throws Exception
	 */
	public void testCloneBugFilter() throws Exception {
		add("file.txt", "content", "fixes NPE\nBug: abcd");

		CommitCountFilter count = new CommitCountFilter();
		BugFilter filter = new BugFilter();
		RevFilter clone = filter.clone();
		assertNotNull(clone);
		assertNotSame(filter, clone);
		new CommitFinder(testRepo).setFilter(
				new AndCommitFilter(filter, clone, count)).find();

		assertEquals(1, count.getCount());
	}

	/**
	 * Test of {@link BugSetFilter#clone()}
	 * 
	 * @throws Exception
	 */
	public void testCloneBugSetFilter() throws Exception {
		add("file.txt", "content", "fixes NPE\nBug: abcd");

		BugSetFilter filter = new BugSetFilter();
		BugSetFilter clone = (BugSetFilter) filter.clone();
		assertNotNull(clone);
		assertNotSame(filter, clone);
		new CommitFinder(testRepo)
				.setFilter(new AndCommitFilter(filter, clone)).find();

		assertEquals(1, filter.getBugs().size());
		assertEquals(1, clone.getBugs().size());
		assertEquals("abcd", filter.getBugs().iterator().next());
		assertEquals("abcd", clone.getBugs().iterator().next());
	}

	/**
	 * Test of {@link BugSetFilter#reset()}
	 * 
	 * @throws Exception
	 */
	public void testReset() throws Exception {
		add("file.txt", "content", "fixes NPE\nBug: abcd");

		BugSetFilter filter = new BugSetFilter();
		assertEquals(0, filter.getBugs().size());
		new CommitFinder(testRepo).setFilter(filter).find();
		assertEquals(1, filter.getBugs().size());
		filter.reset();
		assertEquals(0, filter.getBugs().size());
		new CommitFinder(testRepo).setFilter(filter).find();
		assertEquals(1, filter.getBugs().size());
	}
}
