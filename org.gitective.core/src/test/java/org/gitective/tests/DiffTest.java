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

import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffEntry.ChangeType;
import org.eclipse.jgit.revwalk.RevCommit;
import org.gitective.core.CommitFinder;
import org.gitective.core.filter.commit.CommitDiffFilter;
import org.junit.Test;

/**
 * Unit tests of {@link CommitDiffFilter}
 */
public class DiffTest extends GitTestCase {

	/**
	 * Test diffs introduced by first commit
	 *
	 * @throws Exception
	 */
	@Test
	public void first() throws Exception {
		add("test.txt", "content");
		final AtomicReference<Collection<DiffEntry>> ref = new AtomicReference<Collection<DiffEntry>>();
		CommitDiffFilter filter = new CommitDiffFilter() {

			public boolean include(RevCommit commit, Collection<DiffEntry> diffs) {
				ref.set(diffs);
				return super.include(commit, diffs);
			}
		};
		new CommitFinder(testRepo).setFilter(filter).find();
		Collection<DiffEntry> diffs = ref.get();
		assertNotNull(diffs);
		assertEquals(1, diffs.size());
		DiffEntry diff = diffs.iterator().next();
		assertEquals(ChangeType.ADD, diff.getChangeType());
		assertEquals(DiffEntry.DEV_NULL, diff.getOldPath());
		assertEquals("test.txt", diff.getNewPath());
	}

	/**
	 * Test diffs introduced by second commit
	 *
	 * @throws Exception
	 */
	@Test
	public void second() throws Exception {
		add("test.txt", "content");
		add("test.txt", "content2");
		final AtomicReference<Collection<DiffEntry>> ref = new AtomicReference<Collection<DiffEntry>>();
		CommitDiffFilter filter = new CommitDiffFilter() {

			public boolean include(RevCommit commit, Collection<DiffEntry> diffs) {
				ref.set(diffs);
				return false;
			}
		};
		new CommitFinder(testRepo).setMatcher(filter).find();
		Collection<DiffEntry> diffs = ref.get();
		assertNotNull(diffs);
		assertEquals(1, diffs.size());
		DiffEntry diff = diffs.iterator().next();
		assertEquals(ChangeType.MODIFY, diff.getChangeType());
		assertEquals("test.txt", diff.getOldPath());
		assertEquals("test.txt", diff.getNewPath());
	}

	/**
	 * Test diffs introduced by second commit
	 *
	 * @throws Exception
	 */
	@Test
	public void merge() throws Exception {
		add("test.txt", "a\nb\nc");
		branch("test");
		add("test.txt", "a\nb\nc\nd");
		checkout("master");
		add("test.txt", "1\na\nb\nc");
		merge("test");
		final AtomicReference<Collection<DiffEntry>> ref = new AtomicReference<Collection<DiffEntry>>();
		CommitDiffFilter filter = new CommitDiffFilter() {

			public boolean include(RevCommit commit, Collection<DiffEntry> diffs) {
				ref.set(diffs);
				return false;
			}
		};
		new CommitFinder(testRepo).setMatcher(filter).find();
		Collection<DiffEntry> diffs = ref.get();
		assertNotNull(diffs);
		assertEquals(1, diffs.size());
		DiffEntry diff = diffs.iterator().next();
		assertEquals(ChangeType.MODIFY, diff.getChangeType());
		assertEquals("test.txt", diff.getOldPath());
		assertEquals("test.txt", diff.getNewPath());
	}
}
