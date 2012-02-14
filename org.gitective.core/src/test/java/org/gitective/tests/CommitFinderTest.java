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

import java.io.IOException;

import org.eclipse.jgit.errors.StopWalkException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.revwalk.filter.RevFilter;
import org.gitective.core.CommitFinder;
import org.gitective.core.GitException;
import org.gitective.core.filter.commit.AndCommitFilter;
import org.gitective.core.filter.commit.CommitCountFilter;
import org.gitective.core.filter.commit.CommitFilter;
import org.gitective.core.filter.commit.CommitListFilter;
import org.gitective.core.filter.commit.DiffFileCountFilter;
import org.gitective.core.filter.commit.DiffLineCountFilter;
import org.junit.Test;

/**
 * Unit tests of {@link CommitFinder}
 */
public class CommitFinderTest extends GitTestCase {

	/**
	 * Test matcher throwing an {@link IOException} when visiting a commit
	 *
	 * @throws Exception
	 */
	@Test
	public void matcherThrowsIOException() throws Exception {
		add("test.txt", "content");
		CommitFinder finder = new CommitFinder(testRepo);
		final IOException exception = new IOException("message");
		finder.setMatcher(new CommitFilter() {

			public boolean include(RevWalk walker, RevCommit cmit)
					throws IOException {
				throw exception;
			}

			public RevFilter clone() {
				return this;
			}
		});
		try {
			finder.find();
			fail("Exception not thrown by matcher filter");
		} catch (GitException e) {
			assertNotNull(e);
			assertEquals(exception, e.getCause());
		}
	}

	/**
	 * Test matcher throwing a {@link StopWalkException} and it being suppressed
	 * and the walk stopping
	 *
	 * @throws Exception
	 */
	@Test
	public void suppressedStopWalkExceptionThrownByMatcher() throws Exception {
		add("test.txt", "content");
		CommitFinder finder = new CommitFinder(testRepo);
		CommitCountFilter count = new CommitCountFilter();
		finder.setMatcher(new AndCommitFilter(new CommitFilter() {

			public boolean include(RevWalk walker, RevCommit cmit)
					throws IOException {
				throw StopWalkException.INSTANCE;
			}

			public RevFilter clone() {
				return this;
			}
		}, count));
		finder.find();
		assertEquals(0, count.getCount());
	}

	/**
	 * Test searching commit range that has none in-between start and end
	 *
	 * @throws Exception
	 */
	@Test
	public void noCommitsBetween() throws Exception {
		add("test.txt", "content");
		CommitFinder finder = new CommitFinder(testRepo);
		CommitCountFilter count = new CommitCountFilter();
		finder.setFilter(count);
		finder.findBetween(Constants.HEAD, Constants.HEAD);
		assertEquals(0, count.getCount());
	}

	/**
	 * Test searching commit range that has none in-between start and end
	 *
	 * @throws Exception
	 */
	@Test
	public void noCommitsBetweenHeadAndHead() throws Exception {
		add("test.txt", "content");
		CommitFinder finder = new CommitFinder(testRepo);
		CommitCountFilter count = new CommitCountFilter();
		finder.setFilter(count);
		finder.findBetween(Constants.HEAD, Constants.HEAD);
		assertEquals(0, count.getCount());
	}

	/**
	 * Test searching commit range that has none in-between start and end
	 *
	 * @throws Exception
	 */
	@Test
	public void noCommitsBetweenCommitIdAndHead() throws Exception {
		add("test.txt", "content");
		RevCommit commit = add("test.txt", "content2");
		CommitFinder finder = new CommitFinder(testRepo);
		CommitListFilter commits = new CommitListFilter();
		finder.setFilter(commits);
		finder.findBetween(commit, Constants.HEAD + "~1");
		assertEquals(1, commits.getCommits().size());
		assertEquals(commit, commits.getCommits().get(0));
	}

	/**
	 * Find commits between with null starting id
	 */
	@Test(expected = IllegalArgumentException.class)
	public void findNullStartId() {
		CommitFinder finder = new CommitFinder(testRepo);
		finder.findBetween((ObjectId) null, ObjectId.zeroId());
	}

	/**
	 * Parse commits in reverse order
	 *
	 * @throws Exception
	 */
	@Test
	public void reverseOrder() throws Exception {
		RevCommit commit1 = add("test.txt", "content");
		RevCommit commit2 = add("test.txt", "content2");
		CommitFinder finder = new CommitFinder(testRepo);
		CommitListFilter commits = new CommitListFilter();
		DiffFileCountFilter diffFiles = new DiffFileCountFilter();
		DiffLineCountFilter diffLines = new DiffLineCountFilter();
		finder.setMatcher(new AndCommitFilter(commits, diffFiles, diffLines));
		finder.setReverseOrder(true);
		finder.find();
		assertEquals(2, commits.getCommits().size());
		assertEquals(commit1, commits.getCommits().get(0));
		assertEquals(commit2, commits.getCommits().get(1));
		assertEquals(1, diffFiles.getAdded());
		assertEquals(1, diffFiles.getEdited());
		assertEquals(1, diffLines.getAdded());
		assertEquals(1, diffLines.getEdited());
	}

	/**
	 * Find until with given end revision
	 *
	 * @throws Exception
	 */
	@Test
	public void findUntilRevision() throws Exception {
		add("test.txt", "content");
		add("test.txt", "content2");
		RevCommit commit3 = add("test.txt", "content3");

		CommitListFilter commits = new CommitListFilter();
		new CommitFinder(testRepo).setMatcher(commits).findUntil("HEAD~1");
		assertEquals(1, commits.getCommits().size());
		assertTrue(commits.getCommits().contains(commit3));
	}

	/**
	 * Find until with given end commit id
	 *
	 * @throws Exception
	 */
	@Test
	public void findUntilCommitId() throws Exception {
		add("test.txt", "content");
		RevCommit commit2 = add("test.txt", "content2");
		RevCommit commit3 = add("test.txt", "content3");
		RevCommit commit4 = add("test.txt", "content4");

		CommitListFilter commits = new CommitListFilter();
		new CommitFinder(testRepo).setMatcher(commits).findUntil(commit2);
		assertEquals(2, commits.getCommits().size());
		assertTrue(commits.getCommits().contains(commit4));
		assertTrue(commits.getCommits().contains(commit3));
	}
}
