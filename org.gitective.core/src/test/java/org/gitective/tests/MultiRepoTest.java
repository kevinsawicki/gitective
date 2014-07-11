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

import java.io.File;

import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.gitective.core.CommitFinder;
import org.gitective.core.filter.commit.CommitCountFilter;
import org.junit.Test;

/**
 * Unit tests of using multiple repositories from the same service class.
 */
public class MultiRepoTest extends GitTestCase {

	/**
	 * Test service constructors
	 *
	 * @throws Exception
	 */
	@Test
	public void constructors() throws Exception {
		new CommitFinder(testRepo);
		new CommitFinder(new FileRepository(testRepo));
		new CommitFinder(testRepo.getAbsolutePath());
	}

	/**
	 * Test search two repos from same service
	 *
	 * @throws Exception
	 */
	@Test
	public void twoRepos() throws Exception {
		add("repo1file.txt", "content");
		File repo2 = initRepo();
		RevCommit repo2Commit = add(repo2, "repo2file.txt", "test");
		assertEquals(0, repo2Commit.getParentCount());

		CommitFinder service = new CommitFinder(testRepo, repo2);
		CommitCountFilter count = new CommitCountFilter();
		service.setFilter(count).find();
		assertEquals(2, count.getCount());
	}
}
