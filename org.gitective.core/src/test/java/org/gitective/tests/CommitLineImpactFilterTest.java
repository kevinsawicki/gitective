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
import org.gitective.core.filter.commit.CommitLineImpactFilter;
import org.junit.Test;

/**
 * Unit tests of {@link CommitLineImpactFilter}
 */
public class CommitLineImpactFilterTest extends GitTestCase {

	/**
	 * Validate default state of {@link CommitLineImpactFilter}
	 */
	@Test
	public void defaultState() {
		CommitLineImpactFilter filter = new CommitLineImpactFilter();
		assertNotNull(filter.getCommits());
		assertTrue(filter.getCommits().isEmpty());
		assertNotNull(filter.iterator());
		assertFalse(filter.iterator().hasNext());
	}

	/**
	 * Clone a {@link CommitLineImpactFilter}
	 */
	@Test
	public void cloneFilter() {
		CommitLineImpactFilter filter = new CommitLineImpactFilter();
		RevFilter clone = filter.clone();
		assertNotNull(clone);
		assertTrue(clone instanceof CommitLineImpactFilter);
		assertNotSame(filter, clone);
		assertEquals(filter.getLimit(),
				((CommitLineImpactFilter) clone).getLimit());
	}

	/**
	 * Validate retention of single most impacting commit
	 *
	 * @throws Exception
	 */
	@Test
	public void mostImpacting() throws Exception {
		RevCommit commit1 = add("test.txt", "line1");
		CommitLineImpactFilter filter = new CommitLineImpactFilter(1);
		assertEquals(1, filter.getLimit());
		CommitFinder finder = new CommitFinder(testRepo).setFilter(filter);
		finder.find();
		assertEquals(1, filter.getCommits().size());
		assertEquals(commit1, filter.getCommits().first().getCommit());
		assertEquals(1, filter.getCommits().last().getAdd());
		filter.reset();
		assertTrue(filter.getCommits().isEmpty());
		RevCommit commit2 = add("test.txt", "line1\nline2\nline3");
		add("test.txt", "line3");
		finder.find();
		assertEquals(1, filter.getCommits().size());
		assertEquals(commit2, filter.getCommits().first().getCommit());
		assertEquals(3, filter.getCommits().last().getEdit());
	}
}
