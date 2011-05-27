/******************************************************************************
 *  Copyright (c) 2011, Kevin Sawicki <kevinsawicki@gmail.com>
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *****************************************************************************/
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
import org.gitective.core.service.CommitService;

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
		CommitService service = new CommitService(testRepo);
		service.search(new AllCommitFilter().add(filter));
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
		CommitService service = new CommitService(testRepo);
		service.search(new AllCommitFilter(filter1, filter2));
		assertEquals(commit, visited1.get());
		assertEquals(commit, visited2.get());
	}
}
