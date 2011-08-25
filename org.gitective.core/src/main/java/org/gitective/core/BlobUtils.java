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
import static org.eclipse.jgit.lib.Constants.OBJ_BLOB;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.diff.HistogramDiff;
import org.eclipse.jgit.diff.RawText;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;

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
	private static byte[] getBytes(final Repository repository,
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
	 * Diff blobs at given object ids
	 *
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
	 * Diff blobs at given object ids
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

		final byte[] data2;
		if (!blob2.equals(ObjectId.zeroId()))
			data2 = getBytes(repository, blob2);
		else
			data2 = new byte[0];

		if (RawText.isBinary(data1) || RawText.isBinary(data2))
			return Collections.emptyList();

		final HistogramDiff diff = new HistogramDiff();
		return diff.diff(comparator, new RawText(data1), new RawText(data2));
	}
}
