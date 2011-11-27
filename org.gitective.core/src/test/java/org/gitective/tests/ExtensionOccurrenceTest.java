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

import org.gitective.core.CommitFinder;
import org.gitective.core.filter.tree.CommitTreeFilter;
import org.gitective.core.filter.tree.ExtensionOccurrenceFilter;
import org.junit.Test;

/**
 * Unit tests of {@link ExtensionOccurrenceFilter}
 */
public class ExtensionOccurrenceTest extends GitTestCase {

	/**
	 * Test filter that has not visited any commits
	 */
	@Test
	public void emptyFilter() {
		ExtensionOccurrenceFilter filter = new ExtensionOccurrenceFilter();
		assertNotNull(filter.getExtensions());
		assertEquals(0, filter.getExtensions().length);
		assertNotNull(filter.getOccurrences());
		assertTrue(filter.getOccurrences().isEmpty());
		assertEquals(0, filter.getCount(null));
		assertEquals(0, filter.getCount(""));
		assertEquals(0, filter.getCount("cpp"));
	}

	/**
	 * Test only commits containing filters with no extension
	 *
	 * @throws Exception
	 */
	@Test
	public void noFileExtensions() throws Exception {
		add("file", "content");
		ExtensionOccurrenceFilter filter = new ExtensionOccurrenceFilter();
		new CommitFinder(testRepo).setFilter(new CommitTreeFilter(filter)).find();
		assertNotNull(filter.getExtensions());
		assertEquals(0, filter.getExtensions().length);
		assertNotNull(filter.getOccurrences());
		assertTrue(filter.getOccurrences().isEmpty());
	}

	/**
	 * Test commit that introduced multiple files with different extensions
	 *
	 * @throws Exception
	 */
	@Test
	public void twoFileExtensions() throws Exception {
		List<String> paths = new ArrayList<String>();
		List<String> contents = new ArrayList<String>();
		paths.add("file.php");
		contents.add("php");
		paths.add("lib/io/repo.php");
		contents.add("content");
		paths.add("file.txt");
		contents.add("text file");
		add(testRepo, paths, contents, "first commit");
		ExtensionOccurrenceFilter filter = new ExtensionOccurrenceFilter();
		new CommitFinder(testRepo).setFilter(new CommitTreeFilter(filter)).find();
		assertEquals(2, filter.getCount("php"));
	}
}
