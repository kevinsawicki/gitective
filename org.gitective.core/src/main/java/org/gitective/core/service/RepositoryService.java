/*******************************************************************************
 *  Copyright (c) 2011 Kevin Sawicki <kevinsawicki@gmail.com>
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.gitective.core.service;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepository;
import org.gitective.core.Assert;
import org.gitective.core.GitException;

/**
 * Base service class for working with a {@link Repository}
 */
public class RepositoryService {

	/**
	 * Repository
	 */
	protected final Repository repository;

	/**
	 * Create a service for the repository at the specified directory path.
	 * 
	 * @param gitDir
	 */
	public RepositoryService(String gitDir) {
		try {
			this.repository = new FileRepository(gitDir);
		} catch (IOException e) {
			throw new GitException(e);
		}
	}

	/**
	 * Create a service for the repository at the specified directory file.
	 * 
	 * @param gitDir
	 */
	public RepositoryService(File gitDir) {
		try {
			this.repository = new FileRepository(gitDir);
		} catch (IOException e) {
			throw new GitException(e);
		}
	}

	/**
	 * Create a service for the specified repository
	 * 
	 * @param repository
	 */
	public RepositoryService(Repository repository) {
		Assert.notNull("Repository cannot be null", repository);
		this.repository = repository;
	}

}
