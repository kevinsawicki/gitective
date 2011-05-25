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
import java.text.MessageFormat;

import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.revwalk.filter.RevFilter;
import org.gitective.core.Assert;
import org.gitective.core.GitException;

/**
 * Commit service class
 */
public class CommitService extends RepositoryService {

	/**
	 * @param gitDir
	 */
	public CommitService(File gitDir) {
		super(gitDir);
	}

	/**
	 * @param repository
	 */
	public CommitService(Repository repository) {
		super(repository);
	}

	/**
	 * @param gitDir
	 */
	public CommitService(String gitDir) {
		super(gitDir);
	}

	/**
	 * Resolve a revision string to an object id. This method will return the
	 * resolved object id for the first repository configured for this service.
	 * 
	 * @see #lookup(Repository, String)
	 * @param revision
	 * @return object id never null
	 */
	public ObjectId resolve(String revision) {
		return lookup(repositories.get(0), revision);
	}

	/**
	 * Resolve a revision string to an object id in the given repository. This
	 * method will never return null and instead will throw a
	 * {@link GitException} when the given revision does not resolve.
	 * 
	 * @param repository
	 * @param revision
	 * @return object id never null
	 */
	public ObjectId lookup(Repository repository, String revision) {
		Assert.notNull("Repository cannot be null", repository);
		Assert.notNull("Revision cannot be null", revision);
		Assert.notEmpty("Revision cannot be empty", revision);
		return resolve(repository, revision);
	}

	/**
	 * Resolve revision string to object id
	 * 
	 * @param repository
	 * @param revision
	 * @return object id never null
	 */
	protected ObjectId resolve(Repository repository, String revision) {
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
	 * @param walk
	 * @param start
	 * @param end
	 * @param filter
	 * @return this service
	 */
	protected CommitService walk(Repository repository, RevWalk walk,
			ObjectId start, ObjectId end, RevFilter filter) {
		Assert.notNull("Starting commit id cannot be null", start);
		Assert.notNull("Filter cannot be null", filter);

		boolean release = walk == null;
		if (release)
			walk = new RevWalk(repository);
		else
			walk.reset();

		walk.setRevFilter(filter);
		try {
			walk.markStart(walk.parseCommit(start));
			if (end != null)
				walk.markUninteresting(walk.parseCommit(end));
			while (walk.next() != null)
				;
		} catch (IOException e) {
			throw new GitException(e);
		} finally {
			if (release)
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
	public CommitService searchFrom(ObjectId start, RevFilter filter) {
		searchBetween(start, (ObjectId) null, filter);
		return this;
	}

	/**
	 * Search commits starting from the given revision string.
	 * 
	 * @param start
	 * @param filter
	 * @return this service
	 */
	public CommitService searchFrom(String start, RevFilter filter) {
		for (Repository repository : repositories)
			searchFrom(lookup(repository, start), filter);
		return this;
	}

	/**
	 * Search commits starting at the commit that {@link Constants#HEAD}
	 * currently points to.
	 * 
	 * @param filter
	 * @return this service
	 */
	public CommitService search(RevFilter filter) {
		return searchFrom(Constants.HEAD, filter);
	}

	/**
	 * Search commits between the given start and end commits.
	 * 
	 * @param start
	 * @param end
	 * @param filter
	 * @return this srevice
	 */
	public CommitService searchBetween(ObjectId start, ObjectId end,
			RevFilter filter) {
		for (Repository repository : repositories)
			walk(repository, null, start, end, filter);
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
	public CommitService searchBetween(String start, ObjectId end,
			RevFilter filter) {
		for (Repository repository : repositories)
			searchBetween(lookup(repository, start), end, filter);
		return this;
	}

	/**
	 * Search commits between the given start commid id and the given end
	 * revision string.
	 * 
	 * @param start
	 * @param end
	 * @param filter
	 * @return this service
	 */
	public CommitService searchBetween(ObjectId start, String end,
			RevFilter filter) {
		for (Repository repository : repositories)
			searchBetween(start, lookup(repository, end), filter);
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
	public CommitService searchBetween(String start, String end,
			RevFilter filter) {
		for (Repository repository : repositories)
			searchBetween(lookup(repository, start), lookup(repository, end),
					filter);
		return this;
	}

	/**
	 * Search commits between the given start commit id and end revision string.
	 * 
	 * @param start
	 * @param end
	 * @param filter
	 * @return this service
	 */
	public CommitService walkBetween(ObjectId start, String end,
			RevFilter filter) {
		for (Repository repository : repositories)
			searchBetween(start, lookup(repository, end), filter);
		return this;
	}

	/**
	 * Get the base commit between revisions.
	 * 
	 * @see #getBase(Repository, String...)
	 * @param revisions
	 * @return base commit or null if none
	 */
	public RevCommit getBase(String... revisions) {
		return getBase(repositories.get(0), revisions);
	}

	/**
	 * Get the base commit between revisions. This method will return the commit
	 * base for the first repository configured for this service.
	 * 
	 * @param repository
	 * @param revisions
	 * @return base commit or null if none
	 */
	public RevCommit getBase(Repository repository, String... revisions) {
		Assert.notNull("Repository cannot be null", repository);
		Assert.notNull("Ref names cannot be null", revisions);
		Assert.notEmpty("Ref names cannot be empty", revisions);
		RevWalk walk = new RevWalk(repository);
		walk.setRetainBody(true);
		walk.setRevFilter(RevFilter.MERGE_BASE);
		try {
			for (String revision : revisions)
				walk.markStart(walk.parseCommit(lookup(repository, revision)));
			return walk.next();
		} catch (IOException e) {
			throw new GitException(e);
		} finally {
			walk.release();
		}
	}

	/**
	 * Get the base commit between commits. This method will return the commit
	 * base for the first repository configured for this service.
	 * 
	 * @param commits
	 * @return base commit or null if none
	 */
	public RevCommit getBase(ObjectId... commits) {
		return getBase(repositories.get(0), commits);
	}

	/**
	 * Get the base commit between commits.
	 * 
	 * @param repository
	 * @param commits
	 * @return base commit or null if none
	 */
	public RevCommit getBase(Repository repository, ObjectId... commits) {
		Assert.notNull("Repository cannot be null", repository);
		Assert.notNull("Commits cannot be null", commits);
		Assert.notEmpty("Commits cannot be empty", commits);
		RevWalk walk = new RevWalk(repository);
		walk.setRetainBody(true);
		walk.setRevFilter(RevFilter.MERGE_BASE);
		try {
			for (ObjectId id : commits)
				walk.markStart(walk.parseCommit(id));
			return walk.next();
		} catch (IOException e) {
			throw new GitException(e);
		} finally {
			walk.release();
		}
	}

	/**
	 * Get latest commit. This method will return the latest commit base for the
	 * first repository configured for this service.
	 * 
	 * @see #getLatest(Repository)
	 * @return commit never null
	 */
	public RevCommit getLatest() {
		return getLatest(repositories.get(0));
	}

	/**
	 * Get latest commit. This will be the commit that {@link Constants#HEAD} is
	 * pointing to.
	 * 
	 * @param repository
	 * @return commit never null
	 */
	public RevCommit getLatest(Repository repository) {
		Assert.notNull("Repository cannot be null", repository);
		RevWalk walk = new RevWalk(repository);
		walk.setRetainBody(true);
		try {
			return walk.parseCommit(lookup(repository, Constants.HEAD));
		} catch (IOException e) {
			throw new GitException(e);
		} finally {
			walk.release();
		}
	}
}
