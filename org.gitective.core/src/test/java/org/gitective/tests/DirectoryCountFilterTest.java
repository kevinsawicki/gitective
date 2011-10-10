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

import org.eclipse.jgit.treewalk.filter.TreeFilter;
import org.gitective.core.filter.tree.CommitTreeFilter;
import org.gitective.core.filter.tree.TypeCountFilter;
import org.gitective.core.service.CommitFinder;
import org.junit.Test;

/**
 * Unit tests of {@link DirectoryCountFilter}
 */
public class DirectoryCountFilterTest extends GitTestCase {

	/**
	 * Test commit with zero directories
	 *
	 * @throws Exception
	 */
	@Test
	public void zeroDirectories() throws Exception {
		add("f.txt", "content");
		TypeCountFilter filter = TypeCountFilter.tree();
		new CommitFinder(testRepo).setFilter(new CommitTreeFilter(filter))
				.find();
		assertEquals(0, filter.getCount());
	}

	/**
	 * Test commit with one directory
	 *
	 * @throws Exception
	 */
	@Test
	public void oneDirectory() throws Exception {
		add(testRepo, Arrays.asList("a/b.txt", "a/c.txt"),
				Arrays.asList("f1", "f2"), "commit");
		TypeCountFilter filter = TypeCountFilter.tree();
		new CommitFinder(testRepo).setFilter(new CommitTreeFilter(filter))
				.find();
		assertEquals(1, filter.getCount());
	}

	/**
	 * Test commit with two directories
	 *
	 * @throws Exception
	 */
	@Test
	public void twoDirectories() throws Exception {
		add(testRepo, Arrays.asList("a/b.txt", "c/d.txt"),
				Arrays.asList("f1", "f2"), "commit");
		TypeCountFilter filter = TypeCountFilter.tree();
		new CommitFinder(testRepo).setFilter(new CommitTreeFilter(filter))
				.find();
		assertEquals(2, filter.getCount());
	}

	/**
	 * Test commit with nested directories
	 *
	 * @throws Exception
	 */
	@Test
	public void nestedDirectories() throws Exception {
		add(testRepo, Arrays.asList("a/b.txt", "a/g/h.txt", "a/u/x.txt"),
				Arrays.asList("f1", "f2", "f3"), "commit");
		TypeCountFilter filter = TypeCountFilter.tree();
		new CommitFinder(testRepo).setFilter(new CommitTreeFilter(filter))
				.find();
		assertEquals(3, filter.getCount());
	}

	/**
	 * Reset filter
	 *
	 * @throws Exception
	 */
	@Test
	public void resetFilter() throws Exception {
		add("a/b.txt", "content");
		TypeCountFilter filter = TypeCountFilter.tree();
		new CommitFinder(testRepo).setFilter(new CommitTreeFilter(filter))
				.find();
		assertEquals(1, filter.getCount());
		filter.reset();
		assertEquals(0, filter.getCount());
	}

	/**
	 * Clone filter
	 *
	 * @throws Exception
	 */
	@Test
	public void cloneFilter() throws Exception {
		TypeCountFilter filter = TypeCountFilter.tree();
		TreeFilter clone = filter.clone();
		assertNotNull(clone);
		assertNotSame(filter, clone);
		assertTrue(clone instanceof TypeCountFilter);
		assertEquals(filter.getType(), ((TypeCountFilter) clone).getType());
	}
}
