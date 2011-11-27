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

import java.util.Arrays;
import java.util.Map;

import org.eclipse.jgit.revwalk.RevCommit;
import org.gitective.core.CommitFinder;
import org.gitective.core.filter.commit.DuplicateContainer;
import org.gitective.core.filter.commit.DuplicateTreeFilter;
import org.junit.Test;

/**
 * Unit tests of {@link DuplicateTreeFilter}
 */
public class DuplicateTreeTest extends GitTestCase {

	/**
	 * Verify single commit with two duplicate folders
	 *
	 * @throws Exception
	 */
	@Test
	public void oneDuplicate() throws Exception {
		RevCommit commit = add(testRepo,
				Arrays.asList("dir1/f1.txt", "dir2/f1.txt"),
				Arrays.asList("abc", "abc"), "commit");
		DuplicateTreeFilter filter = new DuplicateTreeFilter();
		new CommitFinder(testRepo).setFilter(filter).find();
		Map<RevCommit, DuplicateContainer> dupes = filter.getDuplicates();
		assertNotNull(dupes);
		assertEquals(1, dupes.size());
		assertEquals(commit, dupes.keySet().iterator().next());
	}

	/**
	 * Verify commit with no duplicate trees
	 *
	 * @throws Exception
	 */
	@Test
	public void noDuplicates() throws Exception {
		add(testRepo, Arrays.asList("dir1/f1.txt", "dir2/f2.txt"),
				Arrays.asList("a", "b"), "commit");
		add(testRepo, Arrays.asList("dir1/f1.txt", "dir2/f2.txt"),
				Arrays.asList("a1", "b2"), "commit");
		DuplicateTreeFilter filter = new DuplicateTreeFilter();
		new CommitFinder(testRepo).setFilter(filter).find();
		Map<RevCommit, DuplicateContainer> dupes = filter.getDuplicates();
		assertNotNull(dupes);
		assertTrue(dupes.isEmpty());
	}
}
