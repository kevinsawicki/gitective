/******************************************************************************
 *  Copyright (c) 2011, Kevin Sawicki <kevinsawicki@gmail.com>
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *****************************************************************************/
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
	public RepositoryService(String... gitDirs) {
		Assert.notNull("Directories cannot be null", gitDirs);
		Assert.notEmpty("Directories cannot be empty", gitDirs);
		repositories = new Repository[gitDirs.length];
		try {
			for (int i = 0; i < gitDirs.length; i++)
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
	public RepositoryService(File... gitDirs) {
		Assert.notNull("Directories cannot be null", gitDirs);
		Assert.notEmpty("Directories cannot be empty", gitDirs);
		repositories = new Repository[gitDirs.length];
		try {
			for (int i = 0; i < gitDirs.length; i++)
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
	public RepositoryService(Repository... repositories) {
		Assert.notNull("Repositories cannot be null", repositories);
		Assert.notEmpty("Repositories cannot be empty", repositories);
		this.repositories = new Repository[repositories.length];
		System.arraycopy(repositories, 0, this.repositories, 0,
				repositories.length);
	}
}
