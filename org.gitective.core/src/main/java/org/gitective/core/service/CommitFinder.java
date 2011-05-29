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
import java.text.MessageFormat;

import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.revwalk.filter.RevFilter;
import org.gitective.core.Assert;
import org.gitective.core.GitException;

/**
 * Commit locator class
 */
public class CommitFinder extends RepositoryService {

	/**
	 * @param gitDirs
	 */
	public CommitFinder(File... gitDirs) {
		super(gitDirs);
	}

	/**
	 * @param repositories
	 */
	public CommitFinder(Repository... repositories) {
		super(repositories);
	}

	/**
	 * @param gitDirs
	 */
	public CommitFinder(String... gitDirs) {
		super(gitDirs);
	}

	/**
	 * Resolve a revision string to an object id in the given repository. This
	 * method will never return null and instead will throw a
	 * {@link GitException} when the given revision does not resolve.
	 * 
	 * @param repository
	 * @param revision
	 * @return object id that is never null
	 */
	protected ObjectId lookup(Repository repository, String revision) {
		Assert.notNull("Repository cannot be null", repository);
		Assert.notNull("Revision cannot be null", revision);
		Assert.notEmpty("Revision cannot be empty", revision);
		try {
			ObjectId id = repository.resolve(revision);
			if (id == null)
				throw new GitException(MessageFormat.format(
						"Revision ''{0}'' could not be resolved", revision));
			return id;
		} catch (IOException e) {
			throw new GitException(e);
		}
	}

	/**
	 * Walk commits between the start commit id and end commit id. Starting
	 * commit and filter cannot be null.
	 * 
	 * @param repository
	 * @param start
	 * @param end
	 * @param filter
	 * @return this service
	 */
	protected CommitFinder walk(Repository repository, ObjectId start,
			ObjectId end, RevFilter filter) {
		Assert.notNull("Starting commit id cannot be null", start);
		Assert.notNull("Filter cannot be null", filter);

		RevWalk walk = new RevWalk(repository);
		try {
			walk.setRetainBody(true);
			walk.setRevFilter(filter);
			walk.markStart(walk.parseCommit(start));
			if (end != null)
				walk.markUninteresting(walk.parseCommit(end));
			while (walk.next() != null)
				;
		} catch (IOException e) {
			throw new GitException(e);
		} finally {
			walk.release();
		}
		return this;
	}

	/**
	 * Search commits starting from the given commit id.
	 * 
	 * @param start
	 * @param filter
	 * @return this service
	 */
	public CommitFinder findFrom(ObjectId start, RevFilter filter) {
		findBetween(start, (ObjectId) null, filter);
		return this;
	}

	/**
	 * Search commits starting from the given revision string.
	 * 
	 * @param start
	 * @param filter
	 * @return this service
	 */
	public CommitFinder findBetween(String start, RevFilter filter) {
		Repository[] repos = repositories;
		int repoCount = repositories.length;
		Repository repo;
		for (int i = 0; i < repoCount; i++) {
			repo = repos[i];
			walk(repo, lookup(repo, start), null, filter);
		}
		return this;
	}

	/**
	 * Search commits starting at the commit that {@link Constants#HEAD}
	 * currently points to.
	 * 
	 * @param filter
	 * @return this service
	 */
	public CommitFinder find(RevFilter filter) {
		return findBetween(Constants.HEAD, filter);
	}

	/**
	 * Search commits between the given start and end commits.
	 * 
	 * @param start
	 * @param end
	 * @param filter
	 * @return this service
	 */
	public CommitFinder findBetween(ObjectId start, ObjectId end,
			RevFilter filter) {
		Repository[] repos = repositories;
		int repoCount = repositories.length;
		for (int i = 0; i < repoCount; i++)
			walk(repos[i], start, end, filter);
		return this;
	}

	/**
	 * Search commits between the given start revision string and the given end
	 * commit id.
	 * 
	 * @param start
	 * @param end
	 * @param filter
	 * @return this service
	 */
	public CommitFinder findBetween(String start, ObjectId end, RevFilter filter) {
		Repository[] repos = repositories;
		int repoCount = repositories.length;
		Repository repo;
		for (int i = 0; i < repoCount; i++) {
			repo = repos[i];
			walk(repo, lookup(repo, start), end, filter);
		}
		return this;
	}

	/**
	 * Search commits between the given start commit id and the given end
	 * revision string.
	 * 
	 * @param start
	 * @param end
	 * @param filter
	 * @return this service
	 */
	public CommitFinder findBetween(ObjectId start, String end, RevFilter filter) {
		Repository[] repos = repositories;
		int repoCount = repositories.length;
		Repository repo;
		for (int i = 0; i < repoCount; i++) {
			repo = repos[i];
			walk(repo, start, lookup(repo, end), filter);
		}
		return this;
	}

	/**
	 * Search commits between the given start revision string and the given end
	 * revision string.
	 * 
	 * @param start
	 * @param end
	 * @param filter
	 * @return this service
	 */
	public CommitFinder findBetween(String start, String end, RevFilter filter) {
		Repository[] repos = repositories;
		int repoCount = repositories.length;
		Repository repo;
		for (int i = 0; i < repoCount; i++) {
			repo = repos[i];
			walk(repo, lookup(repo, start), lookup(repo, end), filter);
		}
		return this;
	}
}
