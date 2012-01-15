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
import org.eclipse.jgit.revwalk.filter.RevFilter;
import org.gitective.core.CommitFinder;
import org.gitective.core.filter.commit.AndCommitFilter;
import org.gitective.core.filter.commit.CommitCountFilter;
import org.gitective.core.filter.commit.CommitterDiffFilter;
import org.junit.Test;

/**
 * Unit tests of {@link CommitterDiffFilter}
 */
public class CommitterDiffFilterTest extends GitTestCase {

	/**
	 * Verify a filter on a repository with no commits that have differing
	 * authors and committers in the same commit
	 *
	 * @throws Exception
	 */
	@Test
	public void samePersons() throws Exception {
		committer = author;
		add("file.txt", "a");
		add("file.txt", "b");
		CommitterDiffFilter filter = new CommitterDiffFilter();
		CommitCountFilter count = new CommitCountFilter();
		new CommitFinder(testRepo)
				.setFilter(new AndCommitFilter(filter, count)).find();
		assertEquals(0, count.getCount());
	}

	/**
	 * Verify a filter on a repository with a single commit where the author and
	 * committer differ
	 *
	 * @throws Exception
	 */
	@Test
	public void singleDiffPersonCommit() throws Exception {
		committer = author;
		add("file.txt", "a");
		committer = new PersonIdent("Name" + System.nanoTime(), "Email"
				+ System.nanoTime());
		add("file.txt", "b");
		CommitterDiffFilter filter = new CommitterDiffFilter();
		CommitCountFilter count = new CommitCountFilter();
		new CommitFinder(testRepo)
				.setFilter(new AndCommitFilter(filter, count)).find();
		assertEquals(1, count.getCount());
	}

	/**
	 * Verify filter clones
	 */
	@Test
	public void cloneFilter() {
		CommitterDiffFilter filter = new CommitterDiffFilter();
		RevFilter clone = filter.clone();
		assertNotNull(clone);
		assertNotSame(filter, clone);
		assertTrue(clone instanceof CommitterDiffFilter);
	}
}
