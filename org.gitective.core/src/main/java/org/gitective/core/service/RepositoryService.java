/******************************************************************************
 *  Copyright (c) 2011, Kevin Sawicki <kevinsawicki@gmail.com>
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.gitective.core.service;

import java.io.File;
import java.io.IOException;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepository;
import org.gitective.core.Assert;
import org.gitective.core.GitException;

/**
 * Base service class for working with a {@link Repository}
 */
public class RepositoryService implements Iterable<Repository> {

	/**
	 * Repository
	 */
	protected final Deque<Repository> repositories = new LinkedList<Repository>();

	/**
	 * Add non-null repository to this service
	 * 
	 * @param repository
	 */
	protected void add(Repository repository) {
		Assert.notNull("Repository cannot be null", repository);
		repositories.addLast(repository);
	}

	/**
	 * Create a service for the repositories at the specified directory paths.
	 * 
	 * @param gitDirs
	 */
	public RepositoryService(String... gitDirs) {
		Assert.notNull("Directories cannot be null", gitDirs);
		Assert.notEmpty("Directories cannot be empty", gitDirs);
		for (String gitDir : gitDirs)
			try {
				add(new FileRepository(gitDir));
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
		for (File gitDir : gitDirs)
			try {
				add(new FileRepository(gitDir));
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
		for (Repository repository : repositories)
			add(repository);
	}

	/**
	 * Get an iterator over all repositories configured for this service. This
	 * iterator will always have at least one element.
	 */
	public Iterator<Repository> iterator() {
		return this.repositories.iterator();
	}

}
