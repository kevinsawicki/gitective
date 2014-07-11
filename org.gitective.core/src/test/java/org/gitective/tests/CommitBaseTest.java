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

import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.gitective.core.CommitFinder;
import org.gitective.core.CommitUtils;
import org.gitective.core.filter.commit.CommitListFilter;
import org.junit.Test;

/**
 * Tests of
 * {@link CommitUtils#getBase(Repository, org.eclipse.jgit.lib.ObjectId...)} and
 * {@link CommitUtils#getBase(Repository, String...)}
 */
public class CommitBaseTest extends GitTestCase {

	/**
	 * Test getting the base commit of a branch and master and then walking
	 * between the tip of the branch and its branch point.
	 *
	 * @throws Exception
	 */
	@Test
	public void baseCommits() throws Exception {
		RevCommit commit1 = add("file.txt", "content");
		branch("release1");
		RevCommit commit2 = add("file.txt", "edit 1");
		RevCommit commit3 = add("file.txt", "edit 2");

		Repository repo = new FileRepository(testRepo);
		RevCommit base = CommitUtils
				.getBase(repo, Constants.MASTER, "release1");
		assertEquals(commit1, base);

		RevCommit base2 = CommitUtils.getBase(repo,
				CommitUtils.getCommit(repo, Constants.MASTER),
				CommitUtils.getCommit(repo, "release1"));
		assertEquals(base, base2);

		CommitListFilter filter = new CommitListFilter();
		new CommitFinder(testRepo).setFilter(filter).findBetween(commit3, base);
		assertEquals(2, filter.getCommits().size());
		assertEquals(commit3, filter.getCommits().get(0));
		assertEquals(commit2, filter.getCommits().get(1));
	}
}
