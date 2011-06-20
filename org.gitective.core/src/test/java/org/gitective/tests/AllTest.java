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
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.errors.StopWalkException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.revwalk.filter.RevFilter;
import org.gitective.core.filter.commit.AllCommitFilter;
import org.gitective.core.filter.commit.CommitCountFilter;
import org.gitective.core.filter.commit.CommitFilter;
import org.gitective.core.service.CommitFinder;

/**
 * Unit tests of {@link AllCommitFilter}
 */
public class AllTest extends GitTestCase {

	/**
	 * Test {@link AllCommitFilter#clone()}
	 * 
	 * @throws Exception
	 */
	public void testClone() throws Exception {
		CommitCountFilter count = new CommitCountFilter();
		AllCommitFilter filter = new AllCommitFilter(count);
		RevFilter clone = filter.clone();
		assertNotNull(clone);
		assertNotSame(filter, clone);
		assertTrue(clone instanceof AllCommitFilter);
	}

	/**
	 * Test empty adds to filter
	 * 
	 * @throws Exception
	 */
	public void testEmptyAdds() throws Exception {
		AllCommitFilter filter = new AllCommitFilter();
		assertEquals(0, filter.getSize());
		filter.add((RevFilter[]) null);
		assertEquals(0, filter.getSize());
		filter.add(new RevFilter[0]);
		assertEquals(0, filter.getSize());
		filter.add(new CommitCountFilter());
		assertEquals(1, filter.getSize());
	}

	/**
	 * Test always matching despite child filter not-including
	 * 
	 * @throws Exception
	 */
	public void testMatch() throws Exception {
		final AtomicReference<RevCommit> visited = new AtomicReference<RevCommit>();
		CommitFilter filter = new CommitFilter() {

			public boolean include(RevWalk walker, RevCommit cmit)
					throws StopWalkException, MissingObjectException,
					IncorrectObjectTypeException, IOException {
				visited.set(cmit);
				return false;
			}

			public RevFilter clone() {
				return this;
			}
		};
		RevCommit commit = add("file.txt", "content");
		CommitFinder service = new CommitFinder(testRepo);
		service.setFilter(new AllCommitFilter(filter));
		service.find();
		assertEquals(commit, visited.get());
	}

	/**
	 * Test always matching despite children filters not-including
	 * 
	 * @throws Exception
	 */
	public void testMultiMatch() throws Exception {
		final AtomicReference<RevCommit> visited1 = new AtomicReference<RevCommit>();
		CommitFilter filter1 = new CommitFilter() {

			public boolean include(RevWalk walker, RevCommit cmit)
					throws StopWalkException, MissingObjectException,
					IncorrectObjectTypeException, IOException {
				visited1.set(cmit);
				return false;
			}

			public RevFilter clone() {
				return this;
			}
		};
		final AtomicReference<RevCommit> visited2 = new AtomicReference<RevCommit>();
		CommitFilter filter2 = new CommitFilter() {

			public boolean include(RevWalk walker, RevCommit cmit)
					throws StopWalkException, MissingObjectException,
					IncorrectObjectTypeException, IOException {
				visited2.set(cmit);
				return false;
			}

			public RevFilter clone() {
				return this;
			}
		};
		RevCommit commit = add("file.txt", "content");
		CommitFinder service = new CommitFinder(testRepo);
		service.setFilter(new AllCommitFilter(filter1, filter2));
		service.find();
		assertEquals(commit, visited1.get());
		assertEquals(commit, visited2.get());
	}
}
