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

import java.util.Map;

import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.gitective.core.CommitFinder;
import org.gitective.core.CommitUtils;
import org.gitective.core.filter.commit.LastCommitDiffFilter;
import org.junit.Test;

/**
 * Unit tests of {@link LastCommitDiffFilter}
 */
public class LastCommitDiffFilterTest extends GitTestCase {

	/**
	 * Get last commit that last modified paths
	 *
	 * @throws Exception
	 */
	@Test
	public void getHeadCommits() throws Exception {
		add("a.txt", "a");
		RevCommit commit2 = add("a.txt", "aa");
		add("b.txt", "b");
		RevCommit commit4 = add("b.txt", "bb");
		RevCommit commit5 = add("c.txt", "c");

		LastCommitDiffFilter filter = new LastCommitDiffFilter();
		new CommitFinder(testRepo).setFilter(filter).find();

		Map<String, RevCommit> commits = filter.getCommits();
		assertNotNull(commits);
		assertEquals(3, commits.size());
		assertEquals(commit2, commits.get("a.txt"));
		assertEquals(commit4, commits.get("b.txt"));
		assertEquals(commit5, commits.get("c.txt"));
	}

	/**
	 * Get last commits with file in subfolder
	 *
	 * @throws Exception
	 */
	@Test
	public void getHeadCommitsWithFileInSubfolder() throws Exception {
		RevCommit commit1 = add("f1/a.txt", "a");

		LastCommitDiffFilter filter = new LastCommitDiffFilter();
		new CommitFinder(testRepo).setFilter(filter).find();

		Map<String, RevCommit> commits = filter.getCommits();
		assertNotNull(commits);
		assertEquals(1, commits.size());
		assertEquals(commit1, commits.get("f1/a.txt"));
	}

	/**
	 * Get last commits with deleted file in history
	 *
	 * @throws Exception
	 */
	@Test
	public void getHeadCommitsWithDeletedFile() throws Exception {
		RevCommit commit1 = add("a.txt", "a");
		add("b.txt", "b");
		delete("b.txt");
		RevCommit commit4 = add("c.txt", "c");

		LastCommitDiffFilter filter = new LastCommitDiffFilter();
		new CommitFinder(testRepo).setFilter(filter).find();

		Map<String, RevCommit> commits = filter.getCommits();
		assertNotNull(commits);
		assertEquals(2, commits.size());
		assertEquals(commit1, commits.get("a.txt"));
		assertEquals(commit4, commits.get("c.txt"));
	}

	/**
	 * Get last commits for merged branch
	 *
	 * @throws Exception
	 */
	@Test
	public void getHeadCommitsWithMergedFile() throws Exception {
		add("a.txt", "a");
		branch("b1");
		checkout("b1");
		RevCommit commit2 = add("a.txt", "a1");
		checkout("master");
		RevCommit commit3 = add("b.txt", "b");
		merge("b1");
		assertFalse(commit3.equals(CommitUtils.getHead(new FileRepository(
				testRepo))));

		LastCommitDiffFilter filter = new LastCommitDiffFilter();
		new CommitFinder(testRepo).setFilter(filter).find();

		Map<String, RevCommit> commits = filter.getCommits();
		assertNotNull(commits);
		assertEquals(2, commits.size());
		assertEquals(commit2, commits.get("a.txt"));
		assertEquals(commit3, commits.get("b.txt"));
	}

}
