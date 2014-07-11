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

import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.gitective.core.GitException;
import org.junit.Test;

/**
 * Unit tests of {@link GitException}
 */
public class GitExceptionTest extends GitTestCase {

	/**
	 * Test constructors
	 *
	 * @throws IOException
	 */
	@Test
	public void constructors() throws IOException {
		String message = "test";
		NullPointerException cause = new NullPointerException();
		FileRepository repo = new FileRepository(testRepo);

		GitException exception = new GitException(message, repo);
		assertEquals(repo, exception.getRepository());
		assertEquals(message, exception.getMessage());

		exception = new GitException(message, cause, repo);
		assertEquals(repo, exception.getRepository());
		assertEquals(cause, exception.getCause());
		assertEquals(message, exception.getMessage());

		exception = new GitException(repo);
		assertEquals(repo, exception.getRepository());
		assertNull(exception.getCause());
		assertNull(exception.getMessage());
	}
}
