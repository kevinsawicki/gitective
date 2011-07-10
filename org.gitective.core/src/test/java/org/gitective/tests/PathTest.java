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
import org.gitective.core.PathFilterUtils;
import org.gitective.core.filter.commit.AndCommitFilter;
import org.gitective.core.filter.commit.BugFilter;
import org.gitective.core.filter.commit.CommitCountFilter;
import org.gitective.core.filter.commit.LastCommitFilter;
import org.gitective.core.service.CommitFinder;
import org.junit.Test;

/**
 * Unit tests of path filtering
 */
public class PathTest extends GitTestCase {

	/**
	 * Test bugs to paths
	 * 
	 * @throws Exception
	 */
	@Test
	public void bugsToPaths() throws Exception {
		add("foo.cpp", "a");
		add("bar.cpp", "a", "Fixing bug\nBug: 123");
		add("bar.cpp", "b", "Fixing bug\nBug: 124");
		add("Main.java", "public", "Fixing bug\nBug: 555");
		add("Buffer.java", "private", "Fixing bug\nBug: 888");
		CommitCountFilter bugCommits = new CommitCountFilter();
		CommitCountFilter javaBugCommits = new CommitCountFilter();
		AndCommitFilter filter = new AndCommitFilter(new BugFilter(),
				bugCommits);
		CommitFinder finder = new CommitFinder(testRepo);
		finder.setFilter(filter);
		finder.setFilter(PathFilterUtils.andSuffix(".java"));
		finder.setMatcher(javaBugCommits);
		finder.find();
		assertEquals(2, javaBugCommits.getCount());
		assertEquals(4, bugCommits.getCount());
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
		add("file2.txt", "b");
		add("file2.txt", "c");
		add("file3.txt", "d");
		CommitCountFilter all = new CommitCountFilter();
		CommitCountFilter path = new CommitCountFilter();
		CommitFinder finder = new CommitFinder(testRepo);
		finder.setFilter(all);
		finder.setFilter(PathFilterUtils.and("file2.txt"));
		finder.setMatcher(path);
		finder.find();
		assertEquals(4, all.getCount());
		assertEquals(2, path.getCount());
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
}
