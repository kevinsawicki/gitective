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

import org.eclipse.jgit.revwalk.RevCommit;
import org.gitective.core.CommitFinder;
import org.gitective.core.PathFilterUtils;
import org.gitective.core.filter.commit.AllCommitFilter;
import org.gitective.core.filter.commit.AndCommitFilter;
import org.gitective.core.filter.commit.BugFilter;
import org.gitective.core.filter.commit.CommitCountFilter;
import org.gitective.core.filter.commit.CommitListFilter;
import org.gitective.core.filter.commit.LastCommitFilter;
import org.gitective.core.filter.tree.CommitParentTreeFilter;
import org.junit.Test;

/**
 * Unit tests of path filtering
 */
public class PathTest extends GitTestCase {

	/**
	 * Test creating anonymous class
	 */
	@Test
	public void constructor() {
		assertNotNull(new PathFilterUtils() {
		});
	}

	/**
	 * Test bugs to paths
	 *
	 * @throws Exception
	 */
	@Test
	public void bugsToPaths() throws Exception {
		add("foo.cpp", "a");
		RevCommit commit2 = add("bar.cpp", "a", "Fixing bug\nBug: 123");
		RevCommit commit3 = add("bar.cpp", "b", "Fixing bug\nBug: 124");
		RevCommit commit4 = add("Main.java", "public", "Fixing bug\nBug: 555");
		RevCommit commit5 = add("Buffer.java", "private",
				"Fixing bug\nBug: 888");

		CommitListFilter bugCommits = new CommitListFilter();
		AndCommitFilter bugFilters = new AndCommitFilter(new BugFilter(),
				bugCommits);

		CommitListFilter javaBugCommits = new CommitListFilter();
		AndCommitFilter javaBugFilters = new AndCommitFilter(
				new CommitParentTreeFilter(PathFilterUtils.andSuffix(".java")),
				javaBugCommits);

		CommitFinder finder = new CommitFinder(testRepo);
		finder.setFilter(new AllCommitFilter(bugFilters, javaBugFilters));
		finder.find();
		assertEquals(2, javaBugCommits.getCommits().size());
		assertTrue(javaBugCommits.getCommits().contains(commit4));
		assertTrue(javaBugCommits.getCommits().contains(commit5));
		assertEquals(4, bugCommits.getCommits().size());
		assertTrue(bugCommits.getCommits().contains(commit2));
		assertTrue(bugCommits.getCommits().contains(commit3));
		assertTrue(bugCommits.getCommits().contains(commit4));
		assertTrue(bugCommits.getCommits().contains(commit5));
	}

	/**
	 * Test matching single suffix
	 *
	 * @throws Exception
	 */
	@Test
	public void singleSuffix() throws Exception {
		add("foo.cpp", "a");
		RevCommit commit = add("bar.java", "b");
		LastCommitFilter matcher = new LastCommitFilter();
		CommitFinder finder = new CommitFinder(testRepo);
		finder.setFilter(PathFilterUtils.andSuffix(".java"));
		finder.setMatcher(matcher);
		finder.find();
		assertEquals(commit, matcher.getLast());
	}

	/**
	 * Test matching two suffixes
	 *
	 * @throws Exception
	 */
	@Test
	public void twoSuffixes() throws Exception {
		add("foo.cpp", "a");
		RevCommit commit = add("bar.java", "b");
		LastCommitFilter matcher = new LastCommitFilter();
		CommitFinder finder = new CommitFinder(testRepo);
		finder.setFilter(PathFilterUtils.andSuffix(".java"));
		finder.setMatcher(matcher);
		finder.find();
		assertEquals(commit, matcher.getLast());
	}

	/**
	 * Test counting some commits that match a path
	 *
	 * @throws Exception
	 */
	@Test
	public void countingSubset() throws Exception {
		add("file1.txt", "a");
		RevCommit commit2 = add("file2.txt", "b");
		RevCommit commit3 = add("file2.txt", "c");
		add("file3.txt", "d");
		CommitCountFilter all = new CommitCountFilter();
		CommitListFilter path = new CommitListFilter();
		CommitFinder finder = new CommitFinder(testRepo);
		finder.setFilter(new AllCommitFilter(all, new AndCommitFilter(
				new CommitParentTreeFilter(PathFilterUtils.and("file2.txt")),
				path)));
		finder.find();
		assertEquals(4, all.getCount());
		assertEquals(2, path.getCommits().size());
		assertTrue(path.getCommits().contains(commit2));
		assertTrue(path.getCommits().contains(commit3));
	}

	/**
	 * Test filtering paths matching one, two, and three commits
	 *
	 * @throws Exception
	 */
	@Test
	public void multipleCommits() throws Exception {
		add("file1.txt", "a");
		add("file2.txt", "b");
		add("file2.txt", "c");
		add("file3.txt", "d");
		add("file3.txt", "e");
		add("file3.txt", "f");

		CommitCountFilter count = new CommitCountFilter();
		CommitFinder finder = new CommitFinder(testRepo);
		finder.setMatcher(count);
		finder.setFilter(PathFilterUtils.and("file0.txt"));
		finder.find();
		assertEquals(0, count.getCount());
		finder.setFilter(PathFilterUtils.and("file1.txt"));
		finder.find();
		assertEquals(1, count.getCount());
		count.reset();
		finder.setFilter(PathFilterUtils.and("file2.txt"));
		finder.find();
		assertEquals(2, count.getCount());
		count.reset();
		finder.setFilter(PathFilterUtils.and("file3.txt"));
		finder.find();
		assertEquals(3, count.getCount());
	}

	/**
	 * Test or path with null parameter
	 */
	@Test(expected = IllegalArgumentException.class)
	public void nullOrPath() {
		PathFilterUtils.or((String[]) null);
	}

	/**
	 * Test or path with empty parameter
	 */
	@Test(expected = IllegalArgumentException.class)
	public void emptyOrPath() {
		PathFilterUtils.or(new String[0]);
	}

	/**
	 * Test or suffix with null parameter
	 */
	@Test(expected = IllegalArgumentException.class)
	public void nullOrSuffix() {
		PathFilterUtils.orSuffix((String[]) null);
	}

	/**
	 * Test or suffix with empty parameter
	 */
	@Test(expected = IllegalArgumentException.class)
	public void emptyOrSuffix() {
		PathFilterUtils.orSuffix(new String[0]);
	}

	/**
	 * Test and path with null parameter
	 */
	@Test(expected = IllegalArgumentException.class)
	public void nullAndPath() {
		PathFilterUtils.and((String[]) null);
	}

	/**
	 * Test and path with empty parameter
	 */
	@Test(expected = IllegalArgumentException.class)
	public void emptyAndPath() {
		PathFilterUtils.and(new String[0]);
	}

	/**
	 * Test and suffix with null parameter
	 */
	@Test(expected = IllegalArgumentException.class)
	public void nullAndSuffix() {
		PathFilterUtils.andSuffix((String[]) null);
	}

	/**
	 * Test and suffix with empty parameter
	 */
	@Test(expected = IllegalArgumentException.class)
	public void emptyAndSuffix() {
		PathFilterUtils.andSuffix(new String[0]);
	}

	/**
	 * Test creation of valid or suffix path filter
	 */
	@Test
	public void singleOrSuffix() {
		assertNotNull(PathFilterUtils.orSuffix(".java"));
	}

	/**
	 * Test creation of valid or suffix path filter
	 */
	@Test
	public void multiOrSuffix() {
		assertNotNull(PathFilterUtils.orSuffix(".h", ".cpp"));
	}

	/**
	 * Test creation of valid and suffix path filter
	 */
	@Test
	public void singleAndSuffix() {
		assertNotNull(PathFilterUtils.andSuffix(".txt"));
	}

	/**
	 * Test creation of valid and suffix path filter
	 */
	@Test
	public void multiAndSuffix() {
		assertNotNull(PathFilterUtils.andSuffix(".rb", ".erb"));
	}

	/**
	 * Test creation of valid or path filter
	 */
	@Test
	public void singleOr() {
		assertNotNull(PathFilterUtils.or(".java"));
	}

	/**
	 * Test creation of valid or path filter
	 */
	@Test
	public void multiOr() {
		assertNotNull(PathFilterUtils.or(".h", ".cpp"));
	}

	/**
	 * Test creation of valid and path filter
	 */
	@Test
	public void singleAnd() {
		assertNotNull(PathFilterUtils.and(".txt"));
	}

	/**
	 * Test creation of valid and path filter
	 */
	@Test
	public void multiAnd() {
		assertNotNull(PathFilterUtils.and(".rb", ".erb"));
	}
}
