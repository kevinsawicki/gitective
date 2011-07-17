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
import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.LargeObjectException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.AbbreviatedObjectId;
import org.eclipse.jgit.lib.AnyObjectId;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.ObjectStream;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepository;
import org.gitective.core.BlobUtils;
import org.gitective.core.GitException;
import org.gitective.core.filter.commit.CommitDiffFilter;
import org.gitective.core.service.CommitFinder;
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

			protected boolean include(RevCommit commit,
					Collection<DiffEntry> diffs) {
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
		BlobUtils.getContent(repo, blob.get().toObjectId());
	}
}
