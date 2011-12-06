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

import org.eclipse.jgit.revwalk.filter.RevFilter;
import org.gitective.core.CommitFinder;
import org.gitective.core.filter.commit.DiffLineCountFilter;
import org.junit.Test;

/**
 * Unit tests of {@link DiffLineCountFilter}
 */
public class DiffLineCountFilterTest extends GitTestCase {

	/**
	 * Validate default state of filter
	 */
	@Test
	public void defaultState() {
		DiffLineCountFilter filter = new DiffLineCountFilter();
		assertEquals(0, filter.getAdded());
		assertEquals(0, filter.getDeleted());
		assertEquals(0, filter.getDeleted());
	}

	/**
	 * Clone a {@link DiffLineCountFilter}
	 */
	@Test
	public void cloneFilter() {
		DiffLineCountFilter filter = new DiffLineCountFilter();
		RevFilter clone = filter.clone();
		assertNotNull(clone);
		assertTrue(clone instanceof DiffLineCountFilter);
		assertNotSame(filter, clone);
	}

	/**
	 * Add single line
	 *
	 * @throws Exception
	 */
	@Test
	public void singleAdd() throws Exception {
		add("test.txt", "content");
		DiffLineCountFilter filter = new DiffLineCountFilter();
		new CommitFinder(testRepo).setFilter(filter).find();
		assertEquals(1, filter.getAdded());
		assertEquals(0, filter.getEdited());
		assertEquals(0, filter.getDeleted());
		filter.reset();
		assertEquals(0, filter.getAdded());
	}

	/**
	 * Add multiple lines in different files
	 *
	 * @throws Exception
	 */
	@Test
	public void multipleAdd() throws Exception {
		add("test.txt", "content");
		add("test2.txt", "more content");
		DiffLineCountFilter filter = new DiffLineCountFilter();
		new CommitFinder(testRepo).setFilter(filter).find();
		assertEquals(2, filter.getAdded());
		assertEquals(0, filter.getEdited());
		assertEquals(0, filter.getDeleted());
	}

	/**
	 * Add, edit, and delete the same line
	 *
	 * @throws Exception
	 */
	@Test
	public void addEditDelete() throws Exception {
		add("test.txt", "content");
		add("test.txt", "content2");
		add("test.txt", "");
		DiffLineCountFilter filter = new DiffLineCountFilter();
		new CommitFinder(testRepo).setFilter(filter).find();
		assertEquals(1, filter.getAdded());
		assertEquals(1, filter.getEdited());
		assertEquals(1, filter.getDeleted());
	}
}
