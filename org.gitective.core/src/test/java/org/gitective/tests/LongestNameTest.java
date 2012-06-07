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

import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.revwalk.RevCommit;
import org.gitective.core.CommitFinder;
import org.gitective.core.filter.commit.LongestAuthorNameFilter;
import org.junit.Test;

/**
 * Unit tests of {@link LongestAuthorNameFilter}
 */
public class LongestNameTest extends GitTestCase {

	/**
	 * Verify state when no commits are visited
	 */
	@Test
	public void noCommitsIncluded() {
		LongestAuthorNameFilter filter = new LongestAuthorNameFilter();
		assertEquals(-1, filter.getLength());
		assertNotNull(filter.getCommits());
		assertEquals(0, filter.getCommits().size());
	}

	/**
	 * Include commits where only one has the longest name
	 *
	 * @throws Exception
	 */
	@Test
	public void singleLongestName() throws Exception {
		LongestAuthorNameFilter filter = new LongestAuthorNameFilter();
		author = new PersonIdent("a", "a@b.c");
		add("test.txt", "test1");
		author = new PersonIdent("ab", "a@b.c");
		add("test.txt", "test2");
		author = new PersonIdent("abc", "a@b.c");
		RevCommit longest = add("test.txt", "test3");
		author = new PersonIdent("ad", "a@b.c");
		add("test.txt", "test4");
		new CommitFinder(testRepo).setMatcher(filter).find();
		assertEquals(3, filter.getLength());
		assertNotNull(filter.getCommits());
		assertEquals(1, filter.getCommits().size());
		assertEquals(longest, filter.getCommits().iterator().next());
	}

	/**
	 * Include commits where multiples have the longest name
	 *
	 * @throws Exception
	 */
	@Test
	public void multipleLongestNames() throws Exception {
		LongestAuthorNameFilter filter = new LongestAuthorNameFilter();
		author = new PersonIdent("a", "a@b.c");
		add("test.txt", "test1");
		author = new PersonIdent("ab", "a@b.cd");
		add("test.txt", "test2");
		author = new PersonIdent("abc", "a@b.cde");
		RevCommit longest1 = add("test.txt", "test3");
		author = new PersonIdent("ad", "a@b.cf");
		add("test.txt", "test4");
		author = new PersonIdent("aef", "a@b.cdg");
		RevCommit longest2 = add("test.txt", "test5");
		new CommitFinder(testRepo).setMatcher(filter).find();
		assertEquals(3, filter.getLength());
		assertNotNull(filter.getCommits());
		assertEquals(2, filter.getCommits().size());
		assertTrue(filter.getCommits().contains(longest1));
		assertTrue(filter.getCommits().contains(longest2));
	}
}
