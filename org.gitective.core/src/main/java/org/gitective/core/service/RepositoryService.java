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
package org.gitective.core.service;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepository;
import org.gitective.core.Assert;
import org.gitective.core.GitException;

/**
 * Base service class for working with one or more {@link Repository} instances.
 */
public class RepositoryService {

	/**
	 * Repositories
	 */
	protected final Repository[] repositories;

	/**
	 * Create a service for the repositories at the specified directory paths.
	 * 
	 * @param gitDirs
	 */
	public RepositoryService(final String... gitDirs) {
		Assert.notNull("Directories cannot be null", gitDirs);
		Assert.notEmpty("Directories cannot be empty", gitDirs);
		final int length = gitDirs.length;
		repositories = new Repository[length];
		try {
			for (int i = 0; i < length; i++)
				repositories[i] = new FileRepository(gitDirs[i]);
		} catch (IOException e) {
			throw new GitException(e);
		}
	}

	/**
	 * Create a service for the repositories at the specified directory files.
	 * 
	 * @param gitDirs
	 */
	public RepositoryService(final File... gitDirs) {
		Assert.notNull("Directories cannot be null", gitDirs);
		Assert.notEmpty("Directories cannot be empty", gitDirs);
		final int length = gitDirs.length;
		repositories = new Repository[length];
		try {
			for (int i = 0; i < length; i++)
				repositories[i] = new FileRepository(gitDirs[i]);
		} catch (IOException e) {
			throw new GitException(e);
		}
	}

	/**
	 * Create a service for the specified repositories
	 * 
	 * @param repositories
	 */
	public RepositoryService(final Repository... repositories) {
		Assert.notNull("Repositories cannot be null", repositories);
		Assert.notEmpty("Repositories cannot be empty", repositories);
		this.repositories = new Repository[repositories.length];
		System.arraycopy(repositories, 0, this.repositories, 0,
				repositories.length);
	}
}
