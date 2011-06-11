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
import org.eclipse.jgit.storage.file.FileRepository;
import org.gitective.core.CommitUtils;
import org.gitective.core.GitException;

/**
 * Unit tests of {@link CommitUtils#getLatest(org.eclipse.jgit.lib.Repository)}
 */
public class LatestTest extends GitTestCase {

	/**
	 * Test retrieving latest commit from repository
	 * 
	 * @throws Exception
	 */
	public void testLatest() throws Exception {
		RevCommit commit = add("file.txt", "content");
		assertEquals(commit,
				CommitUtils.getLatest(new FileRepository(testRepo)));
	}

	/**
	 * Test retrieving latest commit from repository
	 * 
	 * @throws Exception
	 */
	public void testLatestOnEmptyRepository() throws Exception {
		try {
			CommitUtils.getLatest(new FileRepository(testRepo));
			fail("Exception not thrown");
		} catch (GitException e) {
			assertNotNull(e.getMessage());
		}
	}

	/**
	 * Test lookup of commit by id
	 * 
	 * @throws Exception
	 */
	public void testById() throws Exception {
		RevCommit commit = add("file.txt", "content");
		assertEquals(commit,
				CommitUtils.getCommit(new FileRepository(testRepo), commit));
	}
}
