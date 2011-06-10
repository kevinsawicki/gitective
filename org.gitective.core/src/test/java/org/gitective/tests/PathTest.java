/******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *****************************************************************************/
package org.gitective.tests;

import org.eclipse.jgit.revwalk.RevCommit;
import org.gitective.core.PathFilterUtils;
import org.gitective.core.filter.commit.AndCommitFilter;
import org.gitective.core.filter.commit.BugFilter;
import org.gitective.core.filter.commit.CommitCountFilter;
import org.gitective.core.filter.commit.LastCommitFilter;
import org.gitective.core.service.CommitFinder;

/**
 * Unit tests of path filtering
 */
public class PathTest extends GitTestCase {

	/**
	 * Test bugs to paths
	 * 
	 * @throws Exception
	 */
	public void testBugsToPaths() throws Exception {
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
	public void testSingleSuffix() throws Exception {
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
	public void testTwoSuffixes() throws Exception {
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
	public void testCountingSubset() throws Exception {
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
	 * Test filtering commits matching either of two paths
	 * 
	 * @throws Exception
	 */
	public void testTwoPaths() throws Exception {
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
