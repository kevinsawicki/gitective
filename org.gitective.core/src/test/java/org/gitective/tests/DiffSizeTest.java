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
import org.gitective.core.filter.commit.AllCommitFilter;
import org.gitective.core.filter.commit.AndCommitFilter;
import org.gitective.core.filter.commit.CommitListFilter;
import org.gitective.core.filter.commit.DiffSizeFilter;
import org.gitective.core.service.CommitFinder;
import org.junit.Test;

/**
 * Unit tests of {@link DiffSizeFilter}
 */
public class DiffSizeTest extends GitTestCase {

	/**
	 * Test selecting commits where only one file differs
	 *
	 * @throws Exception
	 */
	@Test
	public void diffSingleFile() throws Exception {
		RevCommit commit1 = add("file.txt", "a\nb\nc");
		RevCommit commit2 = add("file.txt", "a\nb2\nc");
		RevCommit commit3 = add("file.txt", "");
		CommitListFilter commits = new CommitListFilter();
		new CommitFinder(testRepo).setFilter(
				new AllCommitFilter(new AndCommitFilter(new DiffSizeFilter(3),
						commits))).find();
		assertTrue(commits.getCommits().contains(commit1));
		assertFalse(commits.getCommits().contains(commit2));
		assertTrue(commits.getCommits().contains(commit3));
	}

	/**
	 * Test of {@link DiffSizeFilter#clone()}
	 */
	@Test
	public void cloneFilter() {
		DiffSizeFilter filter = new DiffSizeFilter(10);
		RevFilter clone = filter.clone();
		assertNotNull(clone);
		assertNotSame(filter, clone);
		assertTrue(clone instanceof DiffSizeFilter);
		assertEquals(filter.getTotal(), ((DiffSizeFilter) clone).getTotal());
	}
}
