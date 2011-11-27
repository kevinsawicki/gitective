/*
 * Copyright (c) 2011 Kevin Sawicki <kevinsawicki@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to
 * deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 */
package org.gitective.tests;

import org.eclipse.jgit.revwalk.filter.RevFilter;
import org.gitective.core.CommitFinder;
import org.gitective.core.filter.commit.AndCommitFilter;
import org.gitective.core.filter.commit.BugFilter;
import org.gitective.core.filter.commit.BugSetFilter;
import org.gitective.core.filter.commit.CommitCountFilter;
import org.junit.Test;

/**
 * Unit tests of {@link BugSetFilter} and {@link BugFilter}
 */
public class BugTest extends GitTestCase {

	/**
	 * Test match of {@link BugFilter}
	 * 
	 * @throws Exception
	 */
	@Test
	public void match() throws Exception {
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
	@Test
	public void nonMatch() throws Exception {
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
	@Test
	public void multipleCommits() throws Exception {
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
	@Test
	public void multipleBugsSameCommit() throws Exception {
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
	@Test
	public void cloneBugFilter() throws Exception {
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
	@Test
	public void cloneBugSetFilter() throws Exception {
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
	@Test
	public void reset() throws Exception {
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
