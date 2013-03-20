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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.internal.storage.file.FileRepository;

/**
 * Base service class for working with one or more {@link Repository} instances.
 */
public class RepositoryService {

	/**
	 * Repositories
	 */
	protected final Repository[] repositories;

	/**
	 * Create a repository service for the repositories at the specified
	 * directory paths.
	 *
	 * @param gitDirs
	 */
	public RepositoryService(final String... gitDirs) {
		if (gitDirs == null)
			throw new IllegalArgumentException(
					Assert.formatNotNull("Directories"));
		if (gitDirs.length == 0)
			throw new IllegalArgumentException(
					Assert.formatNotEmpty("Directories"));

		final int length = gitDirs.length;
		repositories = new Repository[length];
		try {
			for (int i = 0; i < length; i++)
				repositories[i] = new FileRepository(gitDirs[i]);
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * Create a repository service for the repositories at the specified
	 * directories.
	 *
	 * @param gitDirs
	 */
	public RepositoryService(final File... gitDirs) {
		if (gitDirs == null)
			throw new IllegalArgumentException(
					Assert.formatNotNull("Directories"));
		if (gitDirs.length == 0)
			throw new IllegalArgumentException(
					Assert.formatNotEmpty("Directories"));

		final int length = gitDirs.length;
		repositories = new Repository[length];
		try {
			for (int i = 0; i < length; i++)
				repositories[i] = new FileRepository(gitDirs[i]);
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * Create a repository service for the specified repositories.
	 *
	 * @param repositories
	 */
	public RepositoryService(final Repository... repositories) {
		if (repositories == null)
			throw new IllegalArgumentException(
					Assert.formatNotNull("Repositories"));
		if (repositories.length == 0)
			throw new IllegalArgumentException(
					Assert.formatNotEmpty("Repositories"));

		this.repositories = new Repository[repositories.length];
		System.arraycopy(repositories, 0, this.repositories, 0,
				repositories.length);
	}

	/**
	 * Create a repository service for the given {@link Collection} that can be
	 * either {@link String} paths, {@link File} handles to directories, or
	 * existing {@link Repository} instances.
	 *
	 * @param repositories
	 */
	public RepositoryService(final Collection<?> repositories) {
		if (repositories == null)
			throw new IllegalArgumentException(
					Assert.formatNotNull("Repositories"));
		if (repositories.isEmpty())
			throw new IllegalArgumentException(
					Assert.formatNotEmpty("Repositories"));

		final List<Repository> created = new ArrayList<Repository>(
				repositories.size());
		try {
			for (Object repo : repositories)
				if (repo instanceof String)
					created.add(new FileRepository((String) repo));
				else if (repo instanceof File)
					created.add(new FileRepository((File) repo));
				else if (repo instanceof Repository)
					created.add((Repository) repo);
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
		this.repositories = created.toArray(new Repository[created.size()]);
	}
}
