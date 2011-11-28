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
import java.util.Collection;
import java.util.List;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.filter.RevFilter;
import org.gitective.core.CommitFinder;
import org.gitective.core.filter.commit.DiffCountFilter;
import org.junit.Test;

/**
 * Unit tests of {@link DiffCountFilter}
 */
public class DiffCountTest extends GitTestCase {

	/**
	 * Test default filter
	 *
	 * @throws Exception
	 */
	@Test
	public void defaultFilter() throws Exception {
		add("file.txt", "a\nb\nc");
		DiffCountFilter filter = new DiffCountFilter();
		filter.setStop(true);
		new CommitFinder(testRepo).setFilter(filter).find();
	}

	/**
	 * Test {@link DiffCountFilter#clone()}
	 *
	 * @throws Exception
	 */
	@Test
	public void cloneFilter() throws Exception {
		DiffCountFilter filter1 = new DiffCountFilter();
		RevFilter filter2 = filter1.clone();
		assertNotNull(filter2);
		assertTrue(filter2 instanceof DiffCountFilter);
		assertNotSame(filter1, filter2);
	}

	/**
	 * Test counting diffs
	 *
	 * @throws Exception
	 */
	@Test
	public void diffSingleFile() throws Exception {
		add("file.txt", "a\nb\nc");
		add("file.txt", "a\nb2\nc");
		add("file.txt", "");
		final List<Integer> counts = new ArrayList<Integer>();
		DiffCountFilter filter = new DiffCountFilter() {

			protected boolean include(RevCommit commit,
					Collection<DiffEntry> diffs, int diffCount) {
				counts.add(0, diffCount);
				return true;
			}

		};
		new CommitFinder(testRepo).setFilter(filter).find();
		assertEquals(3, counts.size());
		assertEquals(3, counts.get(0).intValue());
		assertEquals(1, counts.get(1).intValue());
		assertEquals(3, counts.get(2).intValue());
	}
}
