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

import static org.eclipse.jgit.lib.FileMode.REGULAR_FILE;
import static org.eclipse.jgit.lib.FileMode.TREE;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.jgit.lib.AnyObjectId;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.FileMode;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.gitective.core.BlobUtils;
import org.gitective.core.TreeUtils;
import org.gitective.core.TreeUtils.ITreeVisitor;
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

	/**
	 * Parent tree walk with null repository
	 */
	@Test(expected = IllegalArgumentException.class)
	public void withCommitsNullRepository() {
		TreeUtils.withCommits(null, ObjectId.zeroId());
	}

	/**
	 * Parent tree walk with null repository
	 */
	@Test(expected = IllegalArgumentException.class)
	public void withCommitsNullRepository2() {
		TreeUtils.withCommits(null, Constants.MASTER);
	}

	/**
	 * /** Commit tree walk with null object ids
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void withCommitsNullIds() throws IOException {
		TreeUtils.withCommits(new FileRepository(testRepo), (ObjectId[]) null);
	}

	/**
	 * Commit tree walk with null revisions
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void withCommitsNullRevision() throws IOException {
		TreeUtils.withCommits(new FileRepository(testRepo), (String[]) null);
	}

	/**
	 * Commit tree walk with empty revisions
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void withCommitsEmptyRevision() throws IOException {
		TreeUtils.withCommits(new FileRepository(testRepo), new String[0]);
	}

	/**
	 * Commit tree walk with empty commits
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void withCommitsEmptyCommits() throws IOException {
		TreeUtils.withCommits(new FileRepository(testRepo), new ObjectId[0]);
	}

	/**
	 * Get tree id with null repository
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getIdNullRepository1() {
		TreeUtils.getId(null, ObjectId.zeroId(), "folder");
	}

	/**
	 * Get tree id with null repository
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getIdNullRepository2() {
		TreeUtils.getId(null, Constants.MASTER, "folder");
	}

	/**
	 * Get tree id with null commit id
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getIdNullCommitId() throws IOException {
		TreeUtils
				.getId(new FileRepository(testRepo), (ObjectId) null, "folder");
	}

	/**
	 * Get tree id with null commit id
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getIdNullRevision() throws IOException {
		TreeUtils.getId(new FileRepository(testRepo), (String) null, "folder");
	}

	/**
	 * Get tree id with null commit id
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getIdEmptyRevision() throws IOException {
		TreeUtils.getId(new FileRepository(testRepo), "", "folder");
	}

	/**
	 * Get tree id with null path
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getIdNullPath1() throws IOException {
		TreeUtils.getId(new FileRepository(testRepo), ObjectId.zeroId(), null);
	}

	/**
	 * Get tree id with null path
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getIdNullPath2() throws IOException {
		TreeUtils.getId(new FileRepository(testRepo), Constants.MASTER, null);
	}

	/**
	 * Get tree id with empty path
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getIdEmptyPath1() throws IOException {
		TreeUtils.getId(new FileRepository(testRepo), ObjectId.zeroId(), "");
	}

	/**
	 * Get tree id with empty path
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getIdEmptyPath2() throws IOException {
		TreeUtils.getId(new FileRepository(testRepo), Constants.MASTER, "");
	}

	/**
	 * Get id with commit id
	 *
	 * @throws Exception
	 */
	@Test
	public void getIdWithCommit() throws Exception {
		Repository repo = new FileRepository(testRepo);
		RevCommit commit = add("d1/f1.txt", "content");
		assertNull(TreeUtils.getId(repo, commit, "d2/f1.txt"));
		assertNull(TreeUtils.getId(repo, commit, "d1/f1.txt"));
		ObjectId treeId = TreeUtils.getId(repo, commit, "d1");
		assertNotNull(treeId);
		assertFalse(treeId.equals(commit.getTree()));
		assertNull(BlobUtils.getId(repo, commit, "d1"));
		assertFalse(treeId.equals(BlobUtils.getId(repo, commit, "d1/f1.txt")));
	}

	/**
	 * Get id with revision
	 *
	 * @throws Exception
	 */
	@Test
	public void getIdWithRevision() throws Exception {
		Repository repo = new FileRepository(testRepo);
		RevCommit commit = add("d1/f1.txt", "content");
		assertNull(TreeUtils.getId(repo, Constants.MASTER, "d2/f1.txt"));
		assertNull(TreeUtils.getId(repo, Constants.MASTER, "d1/f1.txt"));
		ObjectId treeId = TreeUtils.getId(repo, Constants.MASTER, "d1");
		assertNotNull(treeId);
		assertFalse(treeId.equals(commit.getTree()));
		assertNull(BlobUtils.getId(repo, commit, "d1"));
		assertFalse(treeId.equals(BlobUtils.getId(repo, commit, "d1/f1.txt")));
	}

	/**
	 * Visit with null repository
	 */
	@Test(expected = IllegalArgumentException.class)
	public void visitNullRepository() {
		TreeUtils.visit(null, ObjectId.zeroId(), new ITreeVisitor() {

			public boolean accept(FileMode mode, String path, String name,
					AnyObjectId id) {
				return false;
			}
		});
	}

	/**
	 * Visit with null tree id
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void visitNullTreeId() throws IOException {
		TreeUtils.visit(new FileRepository(testRepo), null, new ITreeVisitor() {

			public boolean accept(FileMode mode, String path, String name,
					AnyObjectId id) {
				return false;
			}
		});
	}

	/**
	 * Visit with null visitor
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void visitNullVisitor() throws IOException {
		TreeUtils.visit(new FileRepository(testRepo), ObjectId.zeroId(), null);
	}

	/**
	 * Visit tree
	 *
	 * @throws Exception
	 */
	@Test
	public void visit() throws Exception {
		RevCommit commit = add(
				Arrays.asList("test.txt", "foo/bar.txt", "foo/baz/qux.txt"),
				Arrays.asList("x1", "x2", "x3"));
		final AtomicInteger files = new AtomicInteger(0);
		final AtomicInteger folders = new AtomicInteger(0);
		final List<String> fullPaths = new ArrayList<String>();
		final Set<AnyObjectId> ids = new HashSet<AnyObjectId>();
		assertTrue(TreeUtils.visit(new FileRepository(testRepo),
				commit.getTree(), new ITreeVisitor() {

					public boolean accept(FileMode mode, String path,
							String name, AnyObjectId id) {
						if (mode == REGULAR_FILE)
							files.incrementAndGet();
						if (mode == TREE)
							folders.incrementAndGet();
						fullPaths.add(path != null ? path + '/' + name : name);
						ids.add(id);
						return true;
					}
				}));
		assertEquals(3, files.get());
		assertEquals(2, folders.get());
		assertEquals(5, fullPaths.size());
		assertTrue(fullPaths.contains("test.txt"));
		assertTrue(fullPaths.contains("foo"));
		assertTrue(fullPaths.contains("foo/bar.txt"));
		assertTrue(fullPaths.contains("foo/baz"));
		assertTrue(fullPaths.contains("foo/baz/qux.txt"));
		assertFalse(ids.contains(ObjectId.zeroId()));
		assertEquals(5, ids.size());
	}
}
