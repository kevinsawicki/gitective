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
import java.util.Map;

import org.eclipse.jgit.lib.AbbreviatedObjectId;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.filter.RevFilter;
import org.gitective.core.filter.commit.DuplicateBlobFilter;
import org.gitective.core.filter.commit.DuplicateBlobFilter.DuplicateContainer;
import org.gitective.core.service.CommitFinder;
import org.junit.Test;

/**
 * Unit tests of {@link DuplicateBlobFilter}
 */
public class DuplicateBlobTest extends GitTestCase {

	/**
	 * Test commit with a duplicated file
	 *
	 * @throws Exception
	 */
	@Test
	public void oneDuplicate() throws Exception {
		List<String> names = new ArrayList<String>();
		names.add("file1.txt");
		names.add("file2.txt");
		names.add("file3.txt");
		List<String> contents = new ArrayList<String>();
		contents.add("abcd");
		contents.add("abcd");
		contents.add("not");
		RevCommit commit = add(testRepo, names, contents, "duplicates");
		DuplicateBlobFilter filter = new DuplicateBlobFilter();
		assertFalse(filter.hasDuplicates());
		new CommitFinder(testRepo).setFilter(filter).find();
		assertTrue(filter.hasDuplicates());
		Map<RevCommit, DuplicateContainer> dupes = filter.getDuplicates();
		assertNotNull(dupes);
		assertTrue(dupes.containsKey(commit));
		DuplicateContainer container = dupes.get(commit);
		assertNotNull(container);
		assertEquals(commit, container.getCommit());
		Map<AbbreviatedObjectId, List<String>> files = container
				.getDuplicates();
		assertNotNull(files);
		assertEquals(1, files.size());
		List<String> paths = files.values().iterator().next();
		assertEquals(2, paths.size());
		assertTrue(paths.contains("file1.txt"));
		assertTrue(paths.contains("file2.txt"));

		filter.reset();
		Map<RevCommit, DuplicateContainer> reset = filter.getDuplicates();
		assertNotNull(reset);
		assertTrue(reset.isEmpty());
	}

	/**
	 * Test commit with no duplicates
	 *
	 * @throws Exception
	 */
	@Test
	public void noDuplicate() throws Exception {
		List<String> names = new ArrayList<String>();
		names.add("file1.txt");
		names.add("file2.txt");
		names.add("file3.txt");
		List<String> contents = new ArrayList<String>();
		contents.add("1");
		contents.add("2");
		contents.add("3");
		add(testRepo, names, contents, "duplicates");
		DuplicateBlobFilter filter = new DuplicateBlobFilter();
		new CommitFinder(testRepo).setFilter(filter).find();
		assertFalse(filter.hasDuplicates());
		Map<RevCommit, DuplicateContainer> dupes = filter.getDuplicates();
		assertNotNull(dupes);
		assertTrue(dupes.isEmpty());
	}

	/**
	 * Test of {@link DuplicateBlobFilter#clone()}
	 *
	 * @throws Exception
	 */
	@Test
	public void cloneFilter() throws Exception {
		DuplicateBlobFilter filter = new DuplicateBlobFilter();
		RevFilter clone = filter.clone();
		assertNotNull(clone);
		assertNotSame(filter, clone);
		assertTrue(clone instanceof DuplicateBlobFilter);
	}
}
