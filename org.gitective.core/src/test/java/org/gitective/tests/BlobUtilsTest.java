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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.diff.Edit.Type;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.LargeObjectException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.AbbreviatedObjectId;
import org.eclipse.jgit.lib.AnyObjectId;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.ObjectStream;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.gitective.core.BlobUtils;
import org.gitective.core.CommitFinder;
import org.gitective.core.GitException;
import org.gitective.core.filter.commit.CommitDiffFilter;
import org.junit.Test;

/**
 * Unit tests of {@link BlobUtilsTest}
 */
public class BlobUtilsTest extends GitTestCase {

	/**
	 * Test creation of anonymous class
	 */
	@Test
	public void constructor() {
		assertNotNull(new BlobUtils() {
		});
	}

	/**
	 * Test get content with null repository
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getContentNullRepository() {
		BlobUtils.getContent(null, ObjectId.zeroId());
	}

	/**
	 * Test get content with null object id
	 *
	 * @throws Exception
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getContentNullObjectId() throws Exception {
		BlobUtils.getContent(new FileRepository(testRepo), null);
	}

	/**
	 * Test get content for missing object
	 *
	 * @throws Exception
	 */
	@Test(expected = GitException.class)
	public void badObjectLoader() throws Exception {
		BlobUtils.getContent(new FileRepository(testRepo), ObjectId.zeroId());
	}

	/**
	 * Test getting blob content with object load that throws a
	 * {@link LargeObjectException}
	 *
	 * @throws Exception
	 */
	@Test
	public void largeLoader() throws Exception {
		add("test.txt", "content");
		final AtomicReference<AbbreviatedObjectId> blob = new AtomicReference<AbbreviatedObjectId>();
		CommitDiffFilter diffs = new CommitDiffFilter() {

			public boolean include(RevCommit commit, Collection<DiffEntry> diffs) {
				blob.set(diffs.iterator().next().getNewId());
				return true;
			}

		};
		new CommitFinder(testRepo).setFilter(diffs).find();
		assertNotNull(blob.get());

		Repository repo = new FileRepository(testRepo) {

			public ObjectLoader open(AnyObjectId objectId, int typeHint)
					throws MissingObjectException,
					IncorrectObjectTypeException, IOException {
				final ObjectLoader loader = super.open(objectId, typeHint);
				return new ObjectLoader() {

					public ObjectStream openStream()
							throws MissingObjectException, IOException {
						return loader.openStream();
					}

					public int getType() {
						return loader.getType();
					}

					public long getSize() {
						return loader.getSize();
					}

					public byte[] getCachedBytes() throws LargeObjectException {
						throw new LargeObjectException();
					}
				};
			}

		};
		assertEquals("content",
				BlobUtils.getContent(repo, blob.get().toObjectId()));
	}

	/**
	 * Diff blobs that are different
	 *
	 * @throws Exception
	 */
	@Test
	public void diffBlobs() throws Exception {
		add("file.txt", "a\\nb");
		add("file.txt", "c\\nb");
		final List<AbbreviatedObjectId> ids = new ArrayList<AbbreviatedObjectId>();
		CommitDiffFilter filter = new CommitDiffFilter() {

			public boolean include(RevCommit commit, Collection<DiffEntry> diffs) {
				for (DiffEntry diff : diffs)
					ids.add(diff.getNewId());
				return true;
			}

		};
		new CommitFinder(testRepo).setFilter(filter).find();
		assertEquals(2, ids.size());
		Collection<Edit> diffs = BlobUtils.diff(new FileRepository(testRepo),
				ids.get(0).toObjectId(), ids.get(1).toObjectId());
		assertNotNull(diffs);
		assertEquals(1, diffs.size());
		assertNotNull(diffs.toArray()[0]);
	}

	/**
	 * Diff blobs with zero object ids
	 *
	 * @throws IOException
	 */
	@Test
	public void diffWithZeroObjectIds() throws IOException {
		Collection<Edit> edits = BlobUtils.diff(new FileRepository(testRepo),
				ObjectId.zeroId(), ObjectId.zeroId());
		assertNotNull(edits);
		assertTrue(edits.isEmpty());
	}

	/**
	 * Diff valid blob against zero id blob
	 *
	 * @throws Exception
	 */
	@Test
	public void diffWithEmptyObjectId2() throws Exception {
		add("file.txt", "a");
		final List<AbbreviatedObjectId> ids = new ArrayList<AbbreviatedObjectId>();
		CommitDiffFilter filter = new CommitDiffFilter() {

			public boolean include(RevCommit commit, Collection<DiffEntry> diffs) {
				for (DiffEntry diff : diffs)
					ids.add(diff.getNewId());
				return true;
			}

		};
		new CommitFinder(testRepo).setFilter(filter).find();
		assertEquals(1, ids.size());
		Collection<Edit> diffs = BlobUtils.diff(new FileRepository(testRepo),
				ids.get(0).toObjectId(), ObjectId.zeroId());
		assertNotNull(diffs);
		assertEquals(1, diffs.size());
		assertEquals(Type.DELETE, diffs.iterator().next().getType());
	}

	/**
	 * Diff with binary blob as first object id
	 *
	 * @throws Exception
	 */
	@Test
	public void diffWithBinaryObject1() throws Exception {
		add("file.txt", Character.toString('\0'));
		final List<AbbreviatedObjectId> ids = new ArrayList<AbbreviatedObjectId>();
		CommitDiffFilter filter = new CommitDiffFilter() {

			public boolean include(RevCommit commit, Collection<DiffEntry> diffs) {
				for (DiffEntry diff : diffs)
					ids.add(diff.getNewId());
				return true;
			}

		};
		new CommitFinder(testRepo).setFilter(filter).find();
		assertEquals(1, ids.size());
		Collection<Edit> diffs = BlobUtils.diff(new FileRepository(testRepo),
				ids.get(0).toObjectId(), ObjectId.zeroId());
		assertNotNull(diffs);
		assertTrue(diffs.isEmpty());
	}

	/**
	 * Diff with binary blob as second object id
	 *
	 * @throws Exception
	 */
	@Test
	public void diffWithBinaryObject2() throws Exception {
		add("file.txt", Character.toString('\0'));
		final List<AbbreviatedObjectId> ids = new ArrayList<AbbreviatedObjectId>();
		CommitDiffFilter filter = new CommitDiffFilter() {

			public boolean include(RevCommit commit, Collection<DiffEntry> diffs) {
				for (DiffEntry diff : diffs)
					ids.add(diff.getNewId());
				return true;
			}

		};
		new CommitFinder(testRepo).setFilter(filter).find();
		assertEquals(1, ids.size());
		Collection<Edit> diffs = BlobUtils.diff(new FileRepository(testRepo),
				ObjectId.zeroId(), ids.get(0).toObjectId());
		assertNotNull(diffs);
		assertTrue(diffs.isEmpty());
	}

	/**
	 * Diff blobs with null repo
	 */
	@Test(expected = IllegalArgumentException.class)
	public void diffWithNullRepo() {
		BlobUtils.diff((Repository) null, ObjectId.zeroId(), ObjectId.zeroId());
	}

	/**
	 * Diff blobs with null object id
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void diffWithNullObjectId1() throws IOException {
		BlobUtils.diff(new FileRepository(testRepo), null, ObjectId.zeroId());
	}

	/**
	 * Diff blobs with null object id
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void diffWithNullObjectId2() throws IOException {
		BlobUtils.diff(new FileRepository(testRepo), ObjectId.zeroId(), null);
	}

	/**
	 * Diff blobs with null comparator
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void diffWithNullComparator() throws IOException {
		BlobUtils.diff(new FileRepository(testRepo), ObjectId.zeroId(),
				ObjectId.zeroId(), null);
	}

	/**
	 * Get raw content with null repository
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getRawContentNullRepo() {
		BlobUtils.getRawContent(null, ObjectId.zeroId(), "test.txt");
	}

	/**
	 * Get raw content with null repository
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getRawContentNullRepo2() {
		BlobUtils.getRawContent(null, "master", "test.txt");
	}

	/**
	 * Get raw content with null commit id
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getRawContentNullCommitId() throws IOException {
		BlobUtils.getRawContent(new FileRepository(testRepo), (ObjectId) null,
				"test.txt");
	}

	/**
	 * Get raw content with null revision
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getRawContentNullRevision() throws IOException {
		BlobUtils.getRawContent(new FileRepository(testRepo), (String) null,
				"test.txt");
	}

	/**
	 * Get raw content with empty revision
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getRawContentEmptyRevision() throws IOException {
		BlobUtils.getRawContent(new FileRepository(testRepo), "", "test.txt");
	}

	/**
	 * Get raw content with null path
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getRawContentNullPath() throws IOException {
		BlobUtils.getRawContent(new FileRepository(testRepo),
				ObjectId.zeroId(), null);
	}

	/**
	 * Get raw content with null path
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getRawContentNullPath2() throws IOException {
		BlobUtils.getRawContent(new FileRepository(testRepo), "master", null);
	}

	/**
	 * Get raw content with empty path
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getRawContentEmptyPath() throws IOException {
		BlobUtils.getRawContent(new FileRepository(testRepo),
				ObjectId.zeroId(), "");
	}

	/**
	 * Get raw content with empty path
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getRawContentEmptyPath2() throws IOException {
		BlobUtils.getRawContent(new FileRepository(testRepo), "master", "");
	}

	/**
	 * Get content at path
	 *
	 * @throws Exception
	 */
	@Test
	public void getContent() throws Exception {
		RevCommit commit = add("test.txt", "content");
		assertEquals("content", BlobUtils.getContent(new FileRepository(
				testRepo), commit, "test.txt"));
	}

	/**
	 * Get stream at path
	 *
	 * @throws Exception
	 */
	@Test
	public void getStream() throws Exception {
		RevCommit commit = add("test.txt", "content");
		assertNotNull("content", BlobUtils.getStream(new FileRepository(
				testRepo), commit, "test.txt"));
	}

	/**
	 * Get stream at non-existent path
	 *
	 * @throws Exception
	 */
	@Test
	public void getStreamNonExistentPath() throws Exception {
		RevCommit commit = add("test.txt", "content");
		assertNull("content", BlobUtils.getStream(new FileRepository(testRepo),
				commit, "test2.txt"));
	}

	/**
	 * Get HEAD stream at non-existent path
	 *
	 * @throws Exception
	 */
	@Test
	public void getHeadStreamNonExistentPath() throws Exception {
		add("test.txt", "content");
		assertNull("content", BlobUtils.getHeadStream(new FileRepository(
				testRepo), "test2.txt"));
	}

	/**
	 * Get HEAD stream at path
	 *
	 * @throws Exception
	 */
	@Test
	public void getHeadStream() throws Exception {
		add("test.txt", "content");
		assertNotNull("content", BlobUtils.getHeadStream(new FileRepository(
				testRepo), "test.txt"));
	}

	/**
	 * Get stream with null repository
	 *
	 * @throws Exception
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getStreamNullRepo() throws Exception {
		BlobUtils.getStream(null, ObjectId.zeroId(), "test.txt");
	}

	/**
	 * Get stream with null repository
	 *
	 * @throws Exception
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getStreamNullRepo2() throws Exception {
		BlobUtils.getStream(null, "master", "test.txt");
	}

	/**
	 * Get stream with null commit id
	 *
	 * @throws Exception
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getStreamNullId() throws Exception {
		BlobUtils.getStream(new FileRepository(testRepo), (ObjectId) null,
				"test.txt");
	}

	/**
	 * Get stream with null revision
	 *
	 * @throws Exception
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getStreamNullRevision() throws Exception {
		BlobUtils.getStream(new FileRepository(testRepo), (String) null,
				"test.txt");
	}

	/**
	 * Get stream with empty revision
	 *
	 * @throws Exception
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getStreamEmptyRevision() throws Exception {
		BlobUtils.getStream(new FileRepository(testRepo), "", "test.txt");
	}

	/**
	 * Get stream with null path
	 *
	 * @throws Exception
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getStreamNullPath() throws Exception {
		BlobUtils.getStream(new FileRepository(testRepo), ObjectId.zeroId(),
				null);
	}

	/**
	 * Get stream with null path
	 *
	 * @throws Exception
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getStreamNullPath2() throws Exception {
		BlobUtils.getStream(new FileRepository(testRepo), "master", null);
	}

	/**
	 * Get stream with empty path
	 *
	 * @throws Exception
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getStreamEmptyPath() throws Exception {
		BlobUtils
				.getStream(new FileRepository(testRepo), ObjectId.zeroId(), "");
	}

	/**
	 * Get stream with empty path
	 *
	 * @throws Exception
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getStreamEmptyPath2() throws Exception {
		BlobUtils.getStream(new FileRepository(testRepo), "master", "");
	}

	/**
	 * Get HEAD content at path
	 *
	 * @throws Exception
	 */
	@Test
	public void getHeadContent() throws Exception {
		add("test.txt", "content");
		assertEquals("content", BlobUtils.getHeadContent(new FileRepository(
				testRepo), "test.txt"));
	}

	/**
	 * Get raw HEAD content at path
	 *
	 * @throws Exception
	 */
	@Test
	public void getRawHeadContent() throws Exception {
		add("test.txt", "content");
		assertArrayEquals("content".getBytes(Constants.CHARACTER_ENCODING),
				BlobUtils.getRawHeadContent(new FileRepository(testRepo),
						"test.txt"));
	}

	/**
	 * Get HEAD content at path with no content
	 *
	 * @throws Exception
	 */
	@Test
	public void getHeadContentNonExistentPath() throws Exception {
		add("test.txt", "content");
		assertNull(BlobUtils.getHeadContent(new FileRepository(testRepo),
				"test2.txt"));
	}

	/**
	 * Get HEAD content at path that is a directory
	 *
	 * @throws Exception
	 */
	@Test
	public void getHeadContentDirectoryPath() throws Exception {
		add("src/test.txt", "content");
		assertNull(BlobUtils
				.getHeadContent(new FileRepository(testRepo), "src"));
	}

	/**
	 * Get blob id with null repository
	 *
	 * @throws Exception
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getIdNullRepository() throws Exception {
		BlobUtils.getId(null, ObjectId.zeroId(), "test.txt");
	}

	/**
	 * Get blob id with null repository
	 *
	 * @throws Exception
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getIdNullRepository2() throws Exception {
		BlobUtils.getId(null, "master", "test.txt");
	}

	/**
	 * Get blob id with null revision
	 *
	 * @throws Exception
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getIdNullRevision() throws Exception {
		BlobUtils
				.getId(new FileRepository(testRepo), (String) null, "test.txt");
	}

	/**
	 * Get blob id with empty revision
	 *
	 * @throws Exception
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getIdEmptyRevision() throws Exception {
		BlobUtils.getId(new FileRepository(testRepo), "", "test.txt");
	}

	/**
	 * Get blob id with null commit id
	 *
	 * @throws Exception
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getIdNullId() throws Exception {
		BlobUtils.getId(new FileRepository(testRepo), (ObjectId) null,
				"test.txt");
	}

	/**
	 * Get blob id with null path
	 *
	 * @throws Exception
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getIdNullPath() throws Exception {
		BlobUtils.getId(new FileRepository(testRepo), ObjectId.zeroId(), null);
	}

	/**
	 * Get blob id with null path
	 *
	 * @throws Exception
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getIdNullPath2() throws Exception {
		BlobUtils.getId(new FileRepository(testRepo), "master", null);
	}

	/**
	 * Get blob id with null path
	 *
	 * @throws Exception
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getIdEmptyPath() throws Exception {
		BlobUtils.getId(new FileRepository(testRepo), ObjectId.zeroId(), "");
	}

	/**
	 * Get blob id with null path
	 *
	 * @throws Exception
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getIdEmptyPath2() throws Exception {
		BlobUtils.getId(new FileRepository(testRepo), "master", "");
	}

	/**
	 * Get HEAD id of blob
	 *
	 * @throws Exception
	 */
	@Test
	public void getHeadId() throws Exception {
		add("test.txt", "content");
		assertNotNull(BlobUtils.getHeadId(new FileRepository(testRepo),
				"test.txt"));
	}

	/**
	 * Get id of blob in commit
	 *
	 * @throws Exception
	 */
	@Test
	public void getId() throws Exception {
		RevCommit commit = add("test.txt", "content");
		assertNotNull(BlobUtils.getId(new FileRepository(testRepo), commit,
				"test.txt"));
	}
}
