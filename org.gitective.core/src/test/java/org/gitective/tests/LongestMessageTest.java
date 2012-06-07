/*
 * Copyright (c) 2012 Kevin Sawicki <kevinsawicki@gmail.com>
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

import org.eclipse.jgit.revwalk.RevCommit;
import org.gitective.core.CommitFinder;
import org.gitective.core.filter.commit.LongestMessageFilter;
import org.junit.Test;

/**
 * Unit tests of {@link LongestMessageFilter}
 */
public class LongestMessageTest extends GitTestCase {

	/**
	 * Verify state when no commits are visited
	 */
	@Test
	public void noCommitsIncluded() {
		LongestMessageFilter filter = new LongestMessageFilter();
		assertEquals(-1, filter.getLength());
		assertNotNull(filter.getCommits());
		assertEquals(0, filter.getCommits().size());
	}

	/**
	 * Verify {@link LongestMessageFilter#reset()}
	 *
	 * @throws Exception
	 */
	@Test
	public void reset() throws Exception {
		LongestMessageFilter filter = new LongestMessageFilter();
		RevCommit longest = add("test.txt", "test", "test");
		new CommitFinder(testRepo).setMatcher(filter).find();
		assertEquals(4, filter.getLength());
		assertNotNull(filter.getCommits());
		assertEquals(1, filter.getCommits().size());
		assertEquals(longest, filter.iterator().next());
		filter.reset();
		assertEquals(-1, filter.getLength());
		assertNotNull(filter.getCommits());
		assertTrue(filter.getCommits().isEmpty());
		assertFalse(filter.iterator().hasNext());
	}

	/**
	 * Include commits where only one has the longest message
	 *
	 * @throws Exception
	 */
	@Test
	public void singleLongestMessage() throws Exception {
		LongestMessageFilter filter = new LongestMessageFilter();
		add("test.txt", "test1", "a");
		add("test.txt", "test2", "ab");
		RevCommit longest = add("test.txt", "test3", "abcd");
		add("test.txt", "test4", "abc");
		new CommitFinder(testRepo).setMatcher(filter).find();
		assertEquals(4, filter.getLength());
		assertNotNull(filter.getCommits());
		assertEquals(1, filter.getCommits().size());
		assertEquals(longest, filter.getCommits().iterator().next());
	}

	/**
	 * Include commits where multiples have the longest message
	 *
	 * @throws Exception
	 */
	@Test
	public void multipleLongestMessages() throws Exception {
		LongestMessageFilter filter = new LongestMessageFilter();
		add("test.txt", "test1", "a");
		add("test.txt", "test2", "ab");
		RevCommit longest1 = add("test.txt", "test3", "abcd");
		add("test.txt", "test4", "abc");
		RevCommit longest2 = add("test.txt", "test5", "abce");
		new CommitFinder(testRepo).setMatcher(filter).find();
		assertEquals(4, filter.getLength());
		assertNotNull(filter.getCommits());
		assertEquals(2, filter.getCommits().size());
		assertTrue(filter.getCommits().contains(longest1));
		assertTrue(filter.getCommits().contains(longest2));
	}
}
