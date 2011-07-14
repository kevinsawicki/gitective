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

import java.io.IOException;

import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.util.IO;

/**
 * Blob utilities
 */
public abstract class BlobUtils {

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
		final byte[] data;
		try {
			final ObjectLoader loader = repository.open(id, Constants.OBJ_BLOB);
			if (loader.isLarge())
				data = IO.readWholeStream(loader.openStream(),
						(int) loader.getSize()).array();
			else
				data = loader.getCachedBytes();
		} catch (IOException e) {
			throw new GitException(e);
		}
		return new String(data, Constants.CHARSET);
	}

}
