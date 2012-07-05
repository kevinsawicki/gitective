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
import org.eclipse.jgit.revwalk.filter.RevFilter;
import org.gitective.core.CommitFinder;
import org.gitective.core.filter.commit.AllCommitFilter;
import org.gitective.core.filter.commit.AndCommitFilter;
import org.gitective.core.filter.commit.CommitListFilter;
import org.gitective.core.filter.commit.EmptyBlobAddFilter;
import org.gitective.core.filter.commit.EmptyBlobRemoveFilter;
import org.junit.Test;

/**
 * Unit tests of {@link EmptyBlobAddFilter} and {@link EmptyBlobRemoveFilter}
 */
public class EmptyBlobTest extends GitTestCase {

	/**
	 * Test commits with no empty blobs
	 *
	 * @throws Exception
	 */
	@Test
	public void noEmpties() throws Exception {
		add("file1.txt", "a");
		add("file2.txt", "b");
		delete("file2.txt");

		CommitListFilter commits = new CommitListFilter();
		new CommitFinder(testRepo).setFilter(
				new AllCommitFilter(new AndCommitFilter(
						new EmptyBlobAddFilter(), commits),
						new AndCommitFilter(new EmptyBlobRemoveFilter(),
								commits))).find();

		assertTrue(commits.getCommits().isEmpty());
	}

	/**
	 * Test adding an empty blob
	 *
	 * @throws Exception
	 */
	@Test
	public void add() throws Exception {
		add("file1.txt", "content");
		RevCommit commit = add("file2.txt", "");
		CommitListFilter commits = new CommitListFilter();
		new CommitFinder(testRepo).setFilter(
				new AllCommitFilter(new AndCommitFilter(
						new EmptyBlobAddFilter(), commits))).find();

		assertEquals(1, commits.getCommits().size());
		assertTrue(commits.getCommits().contains(commit));
	}

	/**
	 * Test {@link EmptyBlobAddFilter#clone()}
	 */
	@Test
	public void cloneAdd() {
		EmptyBlobAddFilter filter = new EmptyBlobAddFilter(true);
		RevFilter clone = filter.clone();
		assertNotNull(clone);
		assertNotSame(filter, clone);
		assertTrue(clone instanceof EmptyBlobAddFilter);
	}

	/**
	 * Test {@link EmptyBlobRemoveFilter#clone()}
	 */
	@Test
	public void cloneRemove() {
		EmptyBlobRemoveFilter filter = new EmptyBlobRemoveFilter(true);
		RevFilter clone = filter.clone();
		assertNotNull(clone);
		assertNotSame(filter, clone);
		assertTrue(clone instanceof EmptyBlobRemoveFilter);
	}

	/**
	 * Test removing an empty blob
	 *
	 * @throws Exception
	 */
	@Test
	public void remove() throws Exception {
		add("file1.txt", "content");
		add("file2.txt", "");
		RevCommit commit = delete("file2.txt");
		CommitListFilter commits = new CommitListFilter();
		new CommitFinder(testRepo).setFilter(
				new AllCommitFilter(new AndCommitFilter(
						new EmptyBlobRemoveFilter(), commits))).find();

		assertEquals(1, commits.getCommits().size());
		assertTrue(commits.getCommits().contains(commit));
	}
}
