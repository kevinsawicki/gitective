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

import java.io.File;

import junit.framework.Assert;

import org.eclipse.jgit.lib.Repository;
import org.gitective.core.service.RepositoryService;
import org.junit.Test;

/**
 * Unit tests of {@link RepositoryService} class
 */
public class RepositoryServiceTest extends Assert {

	/**
	 * Test constructor with null repositories
	 */
	@Test(expected = IllegalArgumentException.class)
	public void nullStringDirectories() {
		new RepositoryService((String[]) null);
	}

	/**
	 * Test constructor with empty repositories
	 */
	@Test(expected = IllegalArgumentException.class)
	public void emptyStringDirectories() {
		new RepositoryService(new String[0]);
	}

	/**
	 * Test constructor with null repositories
	 */
	@Test(expected = IllegalArgumentException.class)
	public void nullFileDirectories() {
		new RepositoryService((File[]) null);
	}

	/**
	 * Test constructor with empty repositories
	 */
	@Test(expected = IllegalArgumentException.class)
	public void emptyFileDirectories() {
		new RepositoryService(new File[0]);
	}

	/**
	 * Test constructor with null repositories
	 */
	@Test(expected = IllegalArgumentException.class)
	public void nullRepositories() {
		new RepositoryService((Repository[]) null);
	}

	/**
	 * Test constructor with empty repositories
	 */
	@Test(expected = IllegalArgumentException.class)
	public void emptyRepositories() {
		new RepositoryService(new Repository[0]);
	}

}
