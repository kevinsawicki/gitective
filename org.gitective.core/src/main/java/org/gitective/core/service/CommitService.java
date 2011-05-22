/******************************************************************************
 *  Copyright (c) 2011 Kevin Sawicki <kevinsawicki@gmail.com>
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.gitective.core.service;

import java.io.File;
import java.io.IOException;

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
	 * Walk commits between the start commit id and end commit id. Starting
	 * commit and filter cannot be null.
	 * 
	 * @param walk
	 * @param start
	 * @param end
	 * @param filter
	 * @return this service
	 */
	protected CommitService walk(RevWalk walk, ObjectId start, ObjectId end,
			RevFilter filter) {
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
	 * Walk commits matching the filter starting at a given commit.
	 * 
	 * @param start
	 * @param filter
	 * @return this service
	 */
	public CommitService walkFrom(ObjectId start, RevFilter filter) {
		walkBetween(start, (ObjectId) null, filter);
		return this;
	}

	/**
	 * Walk commits matching the filter start at a given revision string.
	 * 
	 * @param start
	 * @param filter
	 * @return this service
	 */
	public CommitService walkFrom(String start, RevFilter filter) {
		try {
			walkFrom(repository.resolve(start), filter);
		} catch (IOException e) {
			throw new GitException(e);
		}
		return this;
	}

	/**
	 * Walk commits matching the filter starting at the commit that
	 * {@link Constants#HEAD} is currently pointing at.
	 * 
	 * @param filter
	 * @return this service
	 */
	public CommitService walkFromHead(RevFilter filter) {
		return walkFrom(Constants.HEAD, filter);
	}

	/**
	 * Walk commits between the given start and end commits that also match the
	 * filter.
	 * 
	 * @param start
	 * @param end
	 * @param filter
	 * @return this srevice
	 */
	public CommitService walkBetween(ObjectId start, ObjectId end,
			RevFilter filter) {
		return walk(null, start, end, filter);
	}

	/**
	 * Walk commits between the given start revision string and end commit that
	 * also match the filter.
	 * 
	 * @param start
	 * @param end
	 * @param filter
	 * @return this service
	 */
	public CommitService walkBetween(String start, ObjectId end,
			RevFilter filter) {
		try {
			return walkBetween(repository.resolve(start), end, filter);
		} catch (IOException e) {
			throw new GitException(e);
		}
	}

	/**
	 * Walk commits betwee the given start and end revisions string that also
	 * match the filter.
	 * 
	 * @param start
	 * @param end
	 * @param filter
	 */
	public void walkBetween(ObjectId start, String end, RevFilter filter) {
		try {
			walkBetween(start, repository.resolve(end), filter);
		} catch (IOException e) {
			throw new GitException(e);
		}
	}

	/**
	 * Get the base commit between revisions.
	 * 
	 * @param revisions
	 * @return base commit or null if none
	 */
	public RevCommit getBase(String... revisions) {
		Assert.notNull("Ref names cannot be null", revisions);
		Assert.notEmpty("Ref names cannot be empty", revisions);
		RevWalk walk = new RevWalk(repository);
		walk.setRetainBody(true);
		walk.setRevFilter(RevFilter.MERGE_BASE);
		try {
			for (String revision : revisions) {
				ObjectId id = repository.resolve(revision);
				if (id == null)
					throw new GitException(
							"Revision string could not be resolved to commit id");
				walk.markStart(walk.parseCommit(id));
			}
			return walk.next();
		} catch (IOException e) {
			throw new GitException(e);
		} finally {
			walk.release();
		}
	}

	/**
	 * Get the base commit between commits.
	 * 
	 * @param commits
	 * @return base commit or null if none
	 */
	public RevCommit getBase(ObjectId... commits) {
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

}
