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

import static org.eclipse.jgit.lib.Constants.HEAD;
import static org.eclipse.jgit.lib.Constants.MASTER;
import static org.eclipse.jgit.lib.Constants.R_HEADS;
import static org.eclipse.jgit.lib.Constants.R_REMOTES;
import static org.eclipse.jgit.lib.Constants.R_TAGS;
import static org.eclipse.jgit.revwalk.filter.RevFilter.MERGE_BASE;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashSet;

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.RefDatabase;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

/**
 * Utilities for dealing with Git commits.
 * <p>
 * This class provides helpers for finding the commits that branches and tags
 * reference.
 */
public abstract class CommitUtils {

	/**
	 * Get the commit that the revision references.
	 *
	 * @param repository
	 * @param revision
	 * @return commit
	 */
	public static RevCommit getCommit(final Repository repository,
			final String revision) {
		if (repository == null)
			throw new IllegalArgumentException(
					Assert.formatNotNull("Repository"));
		if (revision == null)
			throw new IllegalArgumentException(Assert.formatNotNull("Revision"));
		if (revision.length() == 0)
			throw new IllegalArgumentException(
					Assert.formatNotEmpty("Revision"));

		return parse(repository, resolve(repository, revision));
	}

	/**
	 * Get the commit with the given id
	 *
	 * @param repository
	 * @param commitId
	 * @return commit
	 */
	public static RevCommit getCommit(final Repository repository,
			final ObjectId commitId) {
		if (repository == null)
			throw new IllegalArgumentException(
					Assert.formatNotNull("Repository"));
		if (commitId == null)
			throw new IllegalArgumentException(
					Assert.formatNotNull("Commit id"));

		return parse(repository, commitId);
	}

	/**
	 * Get the HEAD commit in the given repository.
	 *
	 * @param repository
	 * @return commit never null
	 */
	public static RevCommit getHead(final Repository repository) {
		return getCommit(repository, HEAD);
	}

	/**
	 * Get the commit at the tip of the master branch in the given repository.
	 *
	 * @param repository
	 * @return commit never null
	 */
	public static RevCommit getMaster(final Repository repository) {
		return getCommit(repository, MASTER);
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
		if (repository == null)
			throw new IllegalArgumentException(
					Assert.formatNotNull("Repository"));
		if (commits == null)
			throw new IllegalArgumentException(Assert.formatNotNull("Commits"));
		if (commits.length == 0)
			throw new IllegalArgumentException(Assert.formatNotEmpty("Commits"));

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
		if (repository == null)
			throw new IllegalArgumentException(
					Assert.formatNotNull("Repository"));
		if (revisions == null)
			throw new IllegalArgumentException(
					Assert.formatNotNull("Revisions"));
		if (revisions.length == 0)
			throw new IllegalArgumentException(
					Assert.formatNotEmpty("Revisions"));

		final int length = revisions.length;
		final ObjectId[] commits = new ObjectId[length];
		for (int i = 0; i < length; i++) {
			commits[i] = strictResolve(repository, revisions[i]);
		}
		return walkToBase(repository, commits);
	}

	/**
	 * Get the commit that the given name references.
	 *
	 * @param repository
	 * @param refName
	 * @return commit, may be null
	 */
	public static RevCommit getRef(final Repository repository,
			final String refName) {
		if (repository == null)
			throw new IllegalArgumentException(
					Assert.formatNotNull("Repository"));
		if (refName == null)
			throw new IllegalArgumentException(Assert.formatNotNull("Ref name"));
		if (refName.length() == 0)
			throw new IllegalArgumentException(
					Assert.formatNotEmpty("Ref name"));

		Ref ref;
		try {
			ref = repository.getRef(refName);
		} catch (IOException e) {
			throw new GitException(e, repository);
		}
		return ref != null ? lookupRef(repository, ref) : null;
	}

	/**
	 * Get the commit for the given reference.
	 *
	 * @param repository
	 * @param ref
	 * @return commit, may be null
	 */
	public static RevCommit getRef(final Repository repository, final Ref ref) {
		if (repository == null)
			throw new IllegalArgumentException(
					Assert.formatNotNull("Repository"));
		if (ref == null)
			throw new IllegalArgumentException(Assert.formatNotNull("Ref"));

		return lookupRef(repository, ref);
	}

	/**
	 * Get all the commits that tags in the given repository reference.
	 *
	 * @param repository
	 * @return non-null but possibly empty collection of commits
	 */
	public static Collection<RevCommit> getTags(final Repository repository) {
		if (repository == null)
			throw new IllegalArgumentException(
					Assert.formatNotNull("Repository"));

		final Collection<RevCommit> commits = new HashSet<RevCommit>();
		final RevWalk walk = new RevWalk(repository);
		final RefDatabase refDb = repository.getRefDatabase();
		try {
			getRefCommits(walk, refDb, R_TAGS, commits);
		} catch (IOException e) {
			throw new GitException(e, repository);
		} finally {
			walk.release();
		}
		return commits;
	}

	/**
	 * Get all the commits that branches in the given repository reference.
	 *
	 * @param repository
	 * @return non-null but possibly empty collection of commits
	 */
	public static Collection<RevCommit> getBranches(final Repository repository) {
		if (repository == null)
			throw new IllegalArgumentException(
					Assert.formatNotNull("Repository"));

		final Collection<RevCommit> commits = new HashSet<RevCommit>();
		final RevWalk walk = new RevWalk(repository);
		final RefDatabase refDb = repository.getRefDatabase();
		try {
			getRefCommits(walk, refDb, R_HEADS, commits);
			getRefCommits(walk, refDb, R_REMOTES, commits);
		} catch (IOException e) {
			throw new GitException(e, repository);
		} finally {
			walk.release();
		}
		return commits;
	}

	private static void getRefCommits(final RevWalk walk,
			final RefDatabase refDb, final String prefix,
			final Collection<RevCommit> commits) throws IOException {
		for (Ref ref : refDb.getRefs(prefix).values()) {
			final RevCommit commit = getRef(walk, ref);
			if (commit != null)
				commits.add(commit);
		}
	}

	private static RevCommit lookupRef(final Repository repository,
			final Ref ref) {
		final RevWalk walk = new RevWalk(repository);
		try {
			return getRef(walk, ref);
		} catch (IOException e) {
			throw new GitException(e, repository);
		} finally {
			walk.release();
		}
	}

	private static RevCommit getRef(final RevWalk walk, final Ref ref)
			throws IOException {
		ObjectId id = ref.getPeeledObjectId();
		if (id == null)
			id = ref.getObjectId();
		return id != null ? walk.parseCommit(id) : null;
	}

	/**
	 * Resolve the revision string to a commit object id
	 *
	 * @param repository
	 * @param revision
	 * @return commit id
	 */
	protected static ObjectId resolve(final Repository repository,
			final String revision) {
		try {
			return repository.resolve(revision);
		} catch (IOException e) {
			throw new GitException(e, repository);
		}
	}

	/**
	 * Resolve the revision string to a commit object id.
	 * <p>
	 * A {@link GitException} will be thrown when the revision can not be
	 * resolved to an {@link ObjectId}
	 *
	 * @param repository
	 * @param revision
	 * @return commit id
	 */
	protected static ObjectId strictResolve(final Repository repository,
			final String revision) {
		final ObjectId resolved = resolve(repository, revision);
		if (resolved == null)
			throw new GitException(MessageFormat.format(
					"Revision ''{0}'' could not be resolved", revision),
					repository);
		return resolved;
	}

	private static RevCommit walkToBase(final Repository repository,
			final ObjectId... commits) {
		final RevWalk walk = new RevWalk(repository);
		walk.setRevFilter(MERGE_BASE);
		try {
			for (int i = 0; i < commits.length; i++)
				walk.markStart(walk.parseCommit(commits[i]));
			final RevCommit base = walk.next();
			if (base != null)
				walk.parseBody(base);
			return base;
		} catch (IOException e) {
			throw new GitException(e, repository);
		} finally {
			walk.release();
		}
	}

	/**
	 * Parse a commit from the repository
	 *
	 * @param repository
	 * @param commit
	 * @return commit
	 */
	protected static RevCommit parse(final Repository repository,
			final ObjectId commit) {
		final RevWalk walk = new RevWalk(repository);
		walk.setRetainBody(true);
		try {
			return walk.parseCommit(commit);
		} catch (IOException e) {
			throw new GitException(e, repository);
		} finally {
			walk.release();
		}
	}

	/**
	 * Parse a commit from the object reader
	 *
	 * @param repository
	 * @param reader
	 * @param commit
	 * @return commit
	 */
	protected static RevCommit parse(final Repository repository,
			final ObjectReader reader, final ObjectId commit) {
		final RevWalk walk = new RevWalk(reader);
		walk.setRetainBody(true);
		try {
			return walk.parseCommit(commit);
		} catch (IOException e) {
			throw new GitException(e, repository);
		}
	}

	/**
	 * Find the commit that last changed the given path starting at the commit
	 * that HEAD currently points to
	 *
	 * @param repository
	 * @param path
	 * @return commit
	 */
	public static RevCommit getLastCommit(final Repository repository,
			final String path) {
		return getLastCommit(repository, HEAD, path);
	}

	/**
	 * Find the commit that last changed the given path starting with the commit
	 * at the given revision
	 *
	 * @param repository
	 * @param revision
	 * @param path
	 * @return commit
	 */
	public static RevCommit getLastCommit(final Repository repository,
			final String revision, final String path) {
		if (repository == null)
			throw new IllegalArgumentException(
					Assert.formatNotNull("Repository"));
		if (revision == null)
			throw new IllegalArgumentException(Assert.formatNotNull("Revision"));
		if (revision.length() == 0)
			throw new IllegalArgumentException(
					Assert.formatNotEmpty("Revision"));
		if (path == null)
			throw new IllegalArgumentException(Assert.formatNotNull("Path"));
		if (path.length() == 0)
			throw new IllegalArgumentException(Assert.formatNotEmpty("Path"));

		final RevWalk walk = new RevWalk(repository);
		walk.setRetainBody(true);
		try {
			walk.markStart(walk
					.parseCommit(strictResolve(repository, revision)));
			walk.setTreeFilter(PathFilterUtils.and(path));
			return walk.next();
		} catch (IOException e) {
			throw new GitException(e, repository);
		} finally {
			walk.release();
		}
	}
}
