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

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashSet;

import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.RefDatabase;
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
		checkRepo(repository);
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
		checkRepo(repository);
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
		checkRepo(repository);
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
		checkRepo(repository);
		Assert.notNull("Revisions cannot be null", revisions);
		Assert.notEmpty("Revisions cannot be empty", revisions);
		final int length = revisions.length;
		final ObjectId[] commits = new ObjectId[length];
		for (int i = 0; i < length; i++)
			commits[i] = resolve(repository, revisions[i]);
		return walkToBase(repository, commits);
	}

	/**
	 * Get commit that given ref points to
	 * 
	 * @param repository
	 * @param refName
	 * @return commit, may be null
	 */
	public static RevCommit getRef(final Repository repository,
			final String refName) {
		checkRepo(repository);
		Assert.notNull("Ref name cannot be null", refName);
		Assert.notEmpty("Ref name cannot be empty", refName);
		Ref ref;
		try {
			ref = repository.getRef(refName);
		} catch (IOException e) {
			throw new GitException(e);
		}
		return ref != null ? lookupRef(repository, ref) : null;
	}

	/**
	 * Get commit that given ref points to
	 * 
	 * @param repository
	 * @param ref
	 * @return commit, may be null
	 */
	public static RevCommit getRef(final Repository repository, final Ref ref) {
		checkRepo(repository);
		Assert.notNull("Ref cannot be null", ref);
		return lookupRef(repository, ref);
	}

	/**
	 * Get all commits that tags in the given repository point to
	 * 
	 * @param repository
	 * @return non-null but possibly empty collection of commits
	 */
	public static Collection<RevCommit> getTags(final Repository repository) {
		checkRepo(repository);
		final Collection<RevCommit> commits = new HashSet<RevCommit>();
		final RevWalk walk = new RevWalk(repository);
		try {
			for (Ref tag : repository.getTags().values()) {
				final RevCommit commit = getRef(walk, tag);
				if (commit != null)
					commits.add(commit);
			}
		} finally {
			walk.release();
		}
		return commits;
	}

	/**
	 * Get all commits that branches in the given repository point to
	 * 
	 * @param repository
	 * @return non-null but possibly empty collection of commits
	 */
	public static Collection<RevCommit> getBranches(final Repository repository) {
		checkRepo(repository);
		final Collection<RevCommit> commits = new HashSet<RevCommit>();
		final RevWalk walk = new RevWalk(repository);
		final RefDatabase refDb = repository.getRefDatabase();
		try {
			for (Ref ref : refDb.getRefs(Constants.R_HEADS).values()) {
				final RevCommit commit = getRef(walk, ref);
				if (commit != null)
					commits.add(commit);
			}
			for (Ref ref : refDb.getRefs(Constants.R_REMOTES).values()) {
				final RevCommit commit = getRef(walk, ref);
				if (commit != null)
					commits.add(commit);
			}
		} catch (IOException e) {
			throw new GitException(e);
		} finally {
			walk.release();
		}
		return commits;
	}

	private static RevCommit lookupRef(final Repository repository,
			final Ref ref) {
		final RevWalk walk = new RevWalk(repository);
		try {
			return getRef(walk, ref);
		} finally {
			walk.release();
		}
	}

	private static RevCommit getRef(final RevWalk walk, final Ref ref) {
		ObjectId id = ref.getPeeledObjectId();
		if (id == null)
			id = ref.getObjectId();
		if (id != null)
			try {
				return walk.parseCommit(id);
			} catch (IOException e) {
				throw new GitException(e);
			}
		return null;
	}

	private static ObjectId resolve(final Repository repository,
			final String revision) {
		try {
			final ObjectId id = repository.resolve(revision);
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
		final RevWalk walk = new RevWalk(repository);
		walk.setRevFilter(RevFilter.MERGE_BASE);
		try {
			for (int i = 0; i < commits.length; i++)
				walk.markStart(walk.parseCommit(commits[i]));
			final RevCommit base = walk.next();
			if (base != null)
				walk.parseBody(base);
			return base;
		} catch (IOException e) {
			throw new GitException(e);
		} finally {
			walk.release();
		}
	}

	private static RevCommit parse(final Repository repository,
			final ObjectId commit) {
		final RevWalk walk = new RevWalk(repository);
		walk.setRetainBody(true);
		try {
			return walk.parseCommit(commit);
		} catch (IOException e) {
			throw new GitException(e);
		} finally {
			walk.release();
		}
	}

	private static void checkRepo(final Repository repository) {
		Assert.notNull("Repository cannot be null", repository);
	}
}
