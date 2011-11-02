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
package org.gitective.core;

import static java.lang.Integer.MAX_VALUE;
import static org.eclipse.jgit.diff.RawTextComparator.DEFAULT;
import static org.eclipse.jgit.lib.Constants.CHARSET;
import static org.eclipse.jgit.lib.Constants.HEAD;
import static org.eclipse.jgit.lib.Constants.OBJ_BLOB;
import static org.eclipse.jgit.lib.FileMode.TYPE_FILE;
import static org.eclipse.jgit.lib.FileMode.TYPE_MASK;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.diff.HistogramDiff;
import org.eclipse.jgit.diff.RawText;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectStream;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.treewalk.TreeWalk;

/**
 * Blob utilities
 */
public abstract class BlobUtils {

	/**
	 * Get blob as byte array
	 *
	 * @param repository
	 * @param id
	 * @return blob bytes
	 */
	protected static byte[] getBytes(final Repository repository,
			final ObjectId id) {
		try {
			return repository.open(id, OBJ_BLOB).getCachedBytes(MAX_VALUE);
		} catch (IOException e) {
			throw new GitException(e, repository);
		}
	}

	/**
	 * Get blob content from given repository as string
	 *
	 * @param repository
	 * @param id
	 * @return blob as UTF-8 string
	 */
	public static String getContent(final Repository repository,
			final ObjectId id) {
		if (repository == null)
			throw new IllegalArgumentException(
					Assert.formatNotNull("Repository"));
		if (id == null)
			throw new IllegalArgumentException(
					Assert.formatNotNull("Object id"));
		return new String(getBytes(repository, id), CHARSET);
	}

	/**
	 * Get id of blob at path in given commit
	 *
	 * @param repository
	 * @param commit
	 * @param path
	 * @return raw content
	 */
	protected static ObjectId lookupId(final Repository repository,
			final RevCommit commit, final String path) {
		final TreeWalk walk;
		try {
			walk = TreeWalk.forPath(repository, path, commit.getTree());
		} catch (IOException e) {
			throw new GitException(e, repository);
		}
		if (walk == null)
			return null;
		if ((walk.getRawMode(0) & TYPE_MASK) != TYPE_FILE)
			return null;
		return walk.getObjectId(0);
	}

	/**
	 * Get raw content
	 *
	 * @param repository
	 * @param commit
	 * @param path
	 * @return raw content
	 */
	protected static byte[] getBytes(final Repository repository,
			final RevCommit commit, final String path) {
		final ObjectId id = lookupId(repository, commit, path);
		return id != null ? getBytes(repository, id) : null;
	}

	/**
	 * Get raw contents of blob at given path in commit that given revision
	 * points to
	 *
	 * @param repository
	 * @param revision
	 * @param path
	 * @return raw content or null if no blob with path at given commit
	 */
	public static byte[] getRawContent(final Repository repository,
			final String revision, final String path) {
		if (repository == null)
			throw new IllegalArgumentException(
					Assert.formatNotNull("Repository"));
		if (revision == null)
			throw new IllegalArgumentException(Assert.formatNotNull("Revision"));
		if (revision.length() == 0)
			throw new IllegalArgumentException(
					Assert.formatNotEmpty("Revision"));
		if (path == null)
			throw new IllegalArgumentException(Assert.formatNotNull("Path"));
		if (path.length() == 0)
			throw new IllegalArgumentException(Assert.formatNotEmpty("Path"));

		final RevCommit commit = CommitUtils.parse(repository,
				CommitUtils.resolve(repository, revision));
		return getBytes(repository, commit, path);
	}

	/**
	 * Get raw contents of blob at given path in commit that given ref points to
	 *
	 * @param repository
	 * @param commitId
	 * @param path
	 * @return raw content or null if no blob with path at given commit
	 */
	public static byte[] getRawContent(final Repository repository,
			final ObjectId commitId, final String path) {
		if (repository == null)
			throw new IllegalArgumentException(
					Assert.formatNotNull("Repository"));
		if (commitId == null)
			throw new IllegalArgumentException(
					Assert.formatNotNull("Commit id"));
		if (path == null)
			throw new IllegalArgumentException(Assert.formatNotNull("Path"));
		if (path.length() == 0)
			throw new IllegalArgumentException(Assert.formatNotEmpty("Path"));

		final RevCommit commit = CommitUtils.parse(repository, commitId);
		return getBytes(repository, commit, path);
	}

	/**
	 * Get contents of blob at given path in given commit
	 *
	 * @param repository
	 * @param commitId
	 * @param path
	 * @return content or null if no blob with path at given commit
	 */
	public static String getContent(final Repository repository,
			final ObjectId commitId, final String path) {
		final byte[] raw = getRawContent(repository, commitId, path);
		return raw != null ? new String(raw, CHARSET) : null;
	}

	/**
	 * Get contents of blob at given path in commit that given revision points
	 * to
	 *
	 * @param repository
	 * @param revision
	 * @param path
	 * @return content or null if no blob with path at given commit
	 */
	public static String getContent(final Repository repository,
			final String revision, final String path) {
		final byte[] raw = getRawContent(repository, revision, path);
		return raw != null ? new String(raw, CHARSET) : null;
	}

	/**
	 * Get contents of blob at given path in commit that HEAD points to
	 *
	 * @param repository
	 * @param path
	 * @return content or null if no blob with path at given commit
	 */
	public static String getHeadContent(final Repository repository,
			final String path) {
		return getContent(repository, HEAD, path);
	}

	/**
	 * Get raw contents of blob at given path in commit that HEAD points to
	 *
	 * @param repository
	 * @param path
	 * @return content or null if no blob with path at given commit
	 */
	public static byte[] getRawHeadContent(final Repository repository,
			final String path) {
		return getRawContent(repository, HEAD, path);
	}

	/**
	 * Get id of blob at path in commit that given revision points to
	 *
	 * @param repository
	 * @param revision
	 * @param path
	 * @return blob id
	 */
	public static ObjectId getId(final Repository repository,
			final String revision, final String path) {
		if (repository == null)
			throw new IllegalArgumentException(
					Assert.formatNotNull("Repository"));
		if (revision == null)
			throw new IllegalArgumentException(Assert.formatNotNull("Revision"));
		if (revision.length() == 0)
			throw new IllegalArgumentException(
					Assert.formatNotEmpty("Revision"));
		if (path == null)
			throw new IllegalArgumentException(Assert.formatNotNull("Path"));
		if (path.length() == 0)
			throw new IllegalArgumentException(Assert.formatNotEmpty("Path"));

		final RevCommit commit = CommitUtils.parse(repository,
				CommitUtils.resolve(repository, revision));
		return lookupId(repository, commit, path);
	}

	/**
	 * Get id of blob at path in commit that HEAD currently points to
	 *
	 * @param repository
	 * @param path
	 * @return blob id
	 */
	public static ObjectId getHeadId(final Repository repository,
			final String path) {
		return getId(repository, HEAD, path);
	}

	/**
	 * Get id of blob at path in commit that given revision points to
	 *
	 * @param repository
	 * @param commitId
	 * @param path
	 * @return blob id
	 */
	public static ObjectId getId(final Repository repository,
			final ObjectId commitId, final String path) {
		if (repository == null)
			throw new IllegalArgumentException(
					Assert.formatNotNull("Repository"));
		if (commitId == null)
			throw new IllegalArgumentException(
					Assert.formatNotNull("Commit id"));
		if (path == null)
			throw new IllegalArgumentException(Assert.formatNotNull("Path"));
		if (path.length() == 0)
			throw new IllegalArgumentException(Assert.formatNotEmpty("Path"));

		final RevCommit commit = CommitUtils.parse(repository, commitId);
		return lookupId(repository, commit, path);
	}

	/**
	 * Open stream to contents of blob at path in given commit
	 *
	 * @param repository
	 * @param commitId
	 * @param path
	 * @return stream, null if no blob at given path
	 */
	public static ObjectStream getStream(final Repository repository,
			final ObjectId commitId, final String path) {
		if (repository == null)
			throw new IllegalArgumentException(
					Assert.formatNotNull("Repository"));
		if (commitId == null)
			throw new IllegalArgumentException(
					Assert.formatNotNull("Commit id"));
		if (path == null)
			throw new IllegalArgumentException(Assert.formatNotNull("Path"));
		if (path.length() == 0)
			throw new IllegalArgumentException(Assert.formatNotEmpty("Path"));

		final RevCommit commit = CommitUtils.parse(repository, commitId);
		final ObjectId blobId = lookupId(repository, commit, path);
		if (blobId == null)
			return null;
		try {
			return repository.open(blobId, OBJ_BLOB).openStream();
		} catch (IOException e) {
			throw new GitException(e, repository);
		}
	}

	/**
	 * Open stream to contents of blob at path in commit that HEAD currently
	 * points to
	 *
	 * @param repository
	 * @param path
	 * @return stream
	 */
	public static ObjectStream getHeadStream(final Repository repository,
			final String path) {
		return getStream(repository, HEAD, path);
	}

	/**
	 * Open stream to contents of blob at path in commit that given revision
	 * points to
	 *
	 * @param repository
	 * @param revision
	 * @param path
	 * @return stream
	 */
	public static ObjectStream getStream(final Repository repository,
			final String revision, final String path) {
		if (repository == null)
			throw new IllegalArgumentException(
					Assert.formatNotNull("Repository"));
		if (revision == null)
			throw new IllegalArgumentException(Assert.formatNotNull("Revision"));
		if (revision.length() == 0)
			throw new IllegalArgumentException(
					Assert.formatNotEmpty("Revision"));
		if (path == null)
			throw new IllegalArgumentException(Assert.formatNotNull("Path"));
		if (path.length() == 0)
			throw new IllegalArgumentException(Assert.formatNotEmpty("Path"));

		final RevCommit commit = CommitUtils.parse(repository,
				CommitUtils.resolve(repository, revision));
		final ObjectId blobId = lookupId(repository, commit, path);
		if (blobId == null)
			return null;
		try {
			return repository.open(blobId, OBJ_BLOB).openStream();
		} catch (IOException e) {
			throw new GitException(e, repository);
		}
	}

	/**
	 * Diff blobs at given object ids using the
	 * {@link RawTextComparator#DEFAULT} comparator
	 *
	 * @see #diff(Repository, ObjectId, ObjectId, RawTextComparator)
	 * @param repository
	 * @param blob1
	 * @param blob2
	 * @return list of edits
	 */
	public static Collection<Edit> diff(final Repository repository,
			final ObjectId blob1, final ObjectId blob2) {
		return diff(repository, blob1, blob2, DEFAULT);
	}

	/**
	 * Diff blobs at given object ids.
	 *
	 * <p>
	 * This method will return an empty list if the content of either blob is
	 * binary.
	 *
	 * @param repository
	 * @param blob1
	 * @param blob2
	 * @param comparator
	 * @return list of edits
	 */
	public static Collection<Edit> diff(final Repository repository,
			final ObjectId blob1, final ObjectId blob2,
			final RawTextComparator comparator) {
		if (repository == null)
			throw new IllegalArgumentException(
					Assert.formatNotNull("Repository"));
		if (blob1 == null)
			throw new IllegalArgumentException(
					Assert.formatNotNull("Blob id 1"));
		if (blob2 == null)
			throw new IllegalArgumentException(
					Assert.formatNotNull("Blob id 2"));
		if (comparator == null)
			throw new IllegalArgumentException(
					Assert.formatNotNull("Comparator"));

		if (blob1.equals(blob2))
			return Collections.emptyList();

		final byte[] data1;
		if (!blob1.equals(ObjectId.zeroId()))
			data1 = getBytes(repository, blob1);
		else
			data1 = new byte[0];
		if (RawText.isBinary(data1))
			return Collections.emptyList();

		final byte[] data2;
		if (!blob2.equals(ObjectId.zeroId()))
			data2 = getBytes(repository, blob2);
		else
			data2 = new byte[0];
		if (RawText.isBinary(data2))
			return Collections.emptyList();

		final HistogramDiff diff = new HistogramDiff();
		return diff.diff(comparator, new RawText(data1), new RawText(data2));
	}
}
