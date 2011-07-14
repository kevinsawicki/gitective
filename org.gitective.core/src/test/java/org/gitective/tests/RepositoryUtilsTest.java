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

import org.eclipse.jgit.storage.file.FileRepository;
import org.gitective.core.RepositoryUtils;
import org.junit.Test;

/**
 * Unit tests of {@link RepositoryUtils}
 */
public class RepositoryUtilsTest extends GitTestCase {

	/**
	 * Test creating a {@link RepositoryUtils} anonymous class
	 */
	@Test
	public void constructor() {
		assertNotNull(new RepositoryUtils() {
		});
	}

	/**
	 * Test getting note refs for null repository
	 */
	@Test
	public void noteRefsForNullRepository() {
		try {
			RepositoryUtils.getNoteRefs(null);
		} catch (IllegalArgumentException e) {
			assertNotNull(e);
			assertNotNull(e.getMessage());
			assertTrue(e.getMessage().length() > 0);
		}
	}

	/**
	 * Test getting note refs for empty repository
	 * 
	 * @throws Exception
	 */
	@Test
	public void noteRefsForEmptyRepository() throws Exception {
		String[] noteRefs = RepositoryUtils.getNoteRefs(new FileRepository(
				testRepo));
		assertNotNull(noteRefs);
		assertTrue(noteRefs.length > 0);
		for (String ref : noteRefs)
			assertNotNull(ref);
	}
}
