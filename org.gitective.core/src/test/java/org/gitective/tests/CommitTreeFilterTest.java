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

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.gitective.core.filter.tree.BaseTreeFilter;
import org.gitective.core.filter.tree.CommitTreeFilter;
import org.junit.Test;

/**
 * Unit tests of {@link CommitTreeFilter}
 */
public class CommitTreeFilterTest extends GitTestCase {

	/**
	 * Create commit tree filter with null tree filter
	 */
	@Test(expected = IllegalArgumentException.class)
	public void nullTreeFilter() {
		new CommitTreeFilter(null);
	}

	/**
	 * Test resetting commit filter resets tree filter
	 */
	@Test
	public void nestedReset() {
		final AtomicBoolean reset = new AtomicBoolean();
		BaseTreeFilter treeFilter = new BaseTreeFilter() {

			public boolean include(TreeWalk walker) throws IOException {
				return true;
			}

			public BaseTreeFilter reset() {
				reset.set(true);
				return super.reset();
			}
		};
		assertFalse(reset.get());
		CommitTreeFilter filter = new CommitTreeFilter(treeFilter);
		filter.reset();
		assertTrue(reset.get());
	}

	/**
	 * Test resetting commit filter resets tree filter
	 *
	 * @throws Exception
	 */
	@Test
	public void nestedSetRepository() throws Exception {
		final AtomicReference<Repository> repo = new AtomicReference<Repository>();
		BaseTreeFilter treeFilter = new BaseTreeFilter() {

			public boolean include(TreeWalk walker) throws IOException {
				return true;
			}

			public BaseTreeFilter setRepository(Repository repository) {
				repo.set(repository);
				return super.setRepository(repository);
			}
		};
		CommitTreeFilter filter = new CommitTreeFilter(treeFilter);
		Repository fileRepo = new FileRepository(testRepo);
		filter.setRepository(fileRepo);
		assertNotNull(repo.get());
		assertEquals(fileRepo.getDirectory(), repo.get().getDirectory());
	}
}
