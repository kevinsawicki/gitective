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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.filter.RevFilter;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.gitective.core.CommitFinder;
import org.gitective.core.CommitUtils;
import org.gitective.core.filter.commit.AndCommitFilter;
import org.gitective.core.filter.commit.CommitCursorFilter;
import org.gitective.core.filter.commit.CommitLimitFilter;
import org.gitective.core.filter.commit.CommitListFilter;
import org.junit.Test;

/**
 * Unit tests of {@link CommitCursorFilter}
 */
public class CursorTest extends GitTestCase {

	/**
	 * Test traversing commits in chunks
	 *
	 * @throws Exception
	 */
	@Test
	public void chunk() throws Exception {
		int commitCount = 50;
		final List<RevCommit> commits = new ArrayList<RevCommit>(commitCount);
		for (int i = 0; i < commitCount; i++)
			commits.add(add("file.txt", "revision " + i));

		CommitFinder service = new CommitFinder(testRepo);
		CommitListFilter bucket = new CommitListFilter();
		CommitLimitFilter limit = new CommitLimitFilter(10);
		limit.setStop(true);

		CommitCursorFilter cursor = new CommitCursorFilter(new AndCommitFilter(
				limit, bucket));
		service.setFilter(cursor);
		int chunks = 0;
		RevCommit commit = CommitUtils.getHead(new FileRepository(testRepo));
		while (commit != null) {
			service.findFrom(commit);
			assertEquals(limit.getLimit(), bucket.getCommits().size());
			commits.removeAll(bucket.getCommits());
			commit = cursor.getLast();
			cursor.reset();
			chunks++;
		}
		assertEquals(commitCount / limit.getLimit(), chunks);
		assertTrue(commits.isEmpty());
	}

	/**
	 * Test clone of {@link CommitCursorFilter}
	 *
	 * @throws Exception
	 */
	@Test
	public void cloneFilter() throws Exception {
		CommitCursorFilter filter = new CommitCursorFilter(RevFilter.NONE);
		assertFalse(filter.include(null, null));
		RevFilter clone = filter.clone();
		assertNotNull(clone);
		assertNotSame(filter, clone);
		assertTrue(clone instanceof CommitCursorFilter);
		assertFalse(clone.include(null, null));
	}

	/**
	 * Test creating a cursor filter with a null filter
	 */
	@Test(expected = IllegalArgumentException.class)
	public void nullFilter() {
		new CommitCursorFilter(null);
	}
}
