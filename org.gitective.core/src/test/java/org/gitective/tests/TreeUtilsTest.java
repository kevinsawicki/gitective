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

import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepository;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.gitective.core.BlobUtils;
import org.gitective.core.TreeUtils;
import org.junit.Test;

/**
 * Unit tests of {@link TreeUtils}
 */
public class TreeUtilsTest extends GitTestCase {

	/**
	 * Parent tree walk with null repository
	 */
	@Test(expected = IllegalArgumentException.class)
	public void withParentsNullRepository() {
		TreeUtils.withParents(null, ObjectId.zeroId());
	}

	/**
	 * Parent tree walk with null repository
	 */
	@Test(expected = IllegalArgumentException.class)
	public void withParentsNullRepository2() {
		TreeUtils.withParents(null, Constants.MASTER);
	}

	/**
	 * Parent tree walk with null object id
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void withParentsNullId() throws IOException {
		TreeUtils.withParents(new FileRepository(testRepo), (ObjectId) null);
	}

	/**
	 * Parent tree walk with null revision
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void withParentsNullRevision() throws IOException {
		TreeUtils.withParents(new FileRepository(testRepo), (String) null);
	}

	/**
	 * Parent tree walk with empty revision
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void withParentsEmptyRevision() throws IOException {
		TreeUtils.withParents(new FileRepository(testRepo), "");
	}

	/**
	 * Diff walk for commit with no parents
	 *
	 * @throws Exception
	 */
	@Test
	public void diffWithNoParents() throws Exception {
		RevCommit commit = add("test.txt", "content");
		Repository repo = new FileRepository(testRepo);
		TreeWalk walk = TreeUtils.diffWithParents(repo, Constants.HEAD);
		assertNotNull(walk);
		assertEquals(2, walk.getTreeCount());
		assertTrue(walk.next());
		assertEquals("test.txt", walk.getPathString());
		assertEquals(ObjectId.zeroId(), walk.getObjectId(0));
		assertEquals(BlobUtils.getId(repo, commit, "test.txt"),
				walk.getObjectId(1));
		assertFalse(walk.next());
	}

	/**
	 * Diff walk for commit with one parent
	 *
	 * @throws Exception
	 */
	@Test
	public void diffWithOneParent() throws Exception {
		Repository repo = new FileRepository(testRepo);
		RevCommit commit1 = add("test.txt", "content");
		RevCommit commit2 = add("test.txt", "content2");
		TreeWalk walk = TreeUtils.diffWithParents(repo, Constants.HEAD);
		assertNotNull(walk);
		assertEquals(2, walk.getTreeCount());
		assertTrue(walk.next());
		assertEquals("test.txt", walk.getPathString());
		assertEquals(BlobUtils.getId(repo, commit1, "test.txt"),
				walk.getObjectId(0));
		assertEquals(BlobUtils.getId(repo, commit2, "test.txt"),
				walk.getObjectId(1));
		assertFalse(walk.next());
	}

	/**
	 * Diff walk for commit with one parent
	 *
	 * @throws Exception
	 */
	@Test
	public void diffRevisions() throws Exception {
		Repository repo = new FileRepository(testRepo);
		RevCommit commit1 = add("test.txt", "content");
		RevCommit commit2 = add("test.txt", "content2");
		TreeWalk walk = TreeUtils.diffWithCommits(repo,
				Constants.MASTER + "~1", Constants.MASTER);
		assertNotNull(walk);
		assertEquals(2, walk.getTreeCount());
		assertTrue(walk.next());
		assertEquals("test.txt", walk.getPathString());
		assertEquals(BlobUtils.getId(repo, commit1, "test.txt"),
				walk.getObjectId(0));
		assertEquals(BlobUtils.getId(repo, commit2, "test.txt"),
				walk.getObjectId(1));
		assertFalse(walk.next());
	}

	/**
	 * Diff walk for commit with one parent
	 *
	 * @throws Exception
	 */
	@Test
	public void diffCommits() throws Exception {
		Repository repo = new FileRepository(testRepo);
		RevCommit commit1 = add("test.txt", "content");
		RevCommit commit2 = add("test.txt", "content2");
		TreeWalk walk = TreeUtils.diffWithCommits(repo, commit1, commit2);
		assertNotNull(walk);
		assertEquals(2, walk.getTreeCount());
		assertTrue(walk.next());
		assertEquals("test.txt", walk.getPathString());
		assertEquals(BlobUtils.getId(repo, commit1, "test.txt"),
				walk.getObjectId(0));
		assertEquals(BlobUtils.getId(repo, commit2, "test.txt"),
				walk.getObjectId(1));
		assertFalse(walk.next());
	}
}
