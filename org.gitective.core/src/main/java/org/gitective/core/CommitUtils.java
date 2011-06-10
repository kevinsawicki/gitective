/******************************************************************************
 *  Copyright (c) 2011, Kevin Sawicki <kevinsawicki@gmail.com>
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *****************************************************************************/
package org.gitective.core;

import java.io.IOException;
import java.text.MessageFormat;

import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.revwalk.filter.RevFilter;

/**
 * Commit utilities
 */
public abstract class CommitUtils {

	/**
	 * Get commit that revision points to
	 * 
	 * @param repository
	 * @param revision
	 * @return commit
	 */
	public static RevCommit getCommit(final Repository repository,
			final String revision) {
		Assert.notNull("Repository cannot be null", repository);
		Assert.notNull("Revision cannot be null", revision);
		Assert.notNull("Revision cannot be empty", revision);
		return parse(repository, resolve(repository, revision));
	}

	/**
	 * Get commit with id
	 * 
	 * @param repository
	 * @param commitId
	 * @return commit
	 */
	public static RevCommit getCommit(final Repository repository,
			final ObjectId commitId) {
		Assert.notNull("Repository cannot be null", repository);
		Assert.notNull("Commit id cannot be null", commitId);
		return parse(repository, commitId);
	}

	/**
	 * Get latest commit. This will be the commit that {@link Constants#HEAD} is
	 * pointing to.
	 * 
	 * @param repository
	 * @return commit never null
	 */
	public static RevCommit getLatest(final Repository repository) {
		return getCommit(repository, Constants.HEAD);
	}

	/**
	 * Get the common base commit of the given commits.
	 * 
	 * @param repository
	 * @param commits
	 * @return base commit or null if none
	 */
	public static RevCommit getBase(final Repository repository,
			final ObjectId... commits) {
		Assert.notNull("Repository cannot be null", repository);
		Assert.notNull("Commits cannot be null", commits);
		Assert.notEmpty("Commits cannot be empty", commits);
		return walkToBase(repository, commits);
	}

	/**
	 * Get the common base commit between the given revisions.
	 * 
	 * @param repository
	 * @param revisions
	 * @return base commit or null if none
	 */
	public static RevCommit getBase(final Repository repository,
			final String... revisions) {
		Assert.notNull("Repository cannot be null", repository);
		Assert.notNull("Revisions cannot be null", revisions);
		Assert.notEmpty("Revisions cannot be empty", revisions);
		final ObjectId[] commits = new ObjectId[revisions.length];
		for (int i = 0; i < revisions.length; i++)
			commits[i] = resolve(repository, revisions[i]);
		return walkToBase(repository, commits);
	}

	private static ObjectId resolve(final Repository repository,
			final String revision) {
		try {
			ObjectId id = repository.resolve(revision);
			if (id != null)
				return id;
		} catch (IOException e) {
			throw new GitException(e);
		}
		throw new GitException(MessageFormat.format(
				"Revision ''{0}'' could not be resolved", revision));
	}

	private static RevCommit walkToBase(final Repository repository,
			final ObjectId... commits) {
		RevWalk walk = new RevWalk(repository);
		walk.setRetainBody(true);
		walk.setRevFilter(RevFilter.MERGE_BASE);
		try {
			for (int i = 0; i < commits.length; i++)
				walk.markStart(walk.parseCommit(commits[i]));
			return walk.next();
		} catch (IOException e) {
			throw new GitException(e);
		} finally {
			walk.release();
		}
	}

	private static RevCommit parse(final Repository repository,
			final ObjectId commit) {
		RevWalk walk = new RevWalk(repository);
		walk.setRetainBody(true);
		try {
			return walk.parseCommit(commit);
		} catch (IOException e) {
			throw new GitException(e);
		} finally {
			walk.release();
		}
	}

}
