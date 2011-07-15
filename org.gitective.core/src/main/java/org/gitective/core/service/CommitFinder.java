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
package org.gitective.core.service;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.eclipse.jgit.errors.StopWalkException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.revwalk.filter.RevFilter;
import org.eclipse.jgit.treewalk.filter.TreeFilter;
import org.gitective.core.Assert;
import org.gitective.core.CommitUtils;
import org.gitective.core.GitException;
import org.gitective.core.filter.commit.CommitFilter;

/**
 * Commit locator class
 */
public class CommitFinder extends RepositoryService {

	/**
	 * Filter for selecting commits to match
	 */
	protected RevFilter preFilter;

	/**
	 * Tree filter
	 */
	protected TreeFilter treeFilter;

	/**
	 * Filter for matches
	 */
	protected RevFilter postFilter;

	/**
	 * @param gitDirs
	 */
	public CommitFinder(final File... gitDirs) {
		super(gitDirs);
	}

	/**
	 * @param repositories
	 */
	public CommitFinder(final Repository... repositories) {
		super(repositories);
	}

	/**
	 * @param gitDirs
	 */
	public CommitFinder(final String... gitDirs) {
		super(gitDirs);
	}

	/**
	 * Set the {@link RevFilter} to use to filter commits during searches.
	 * 
	 * @param filter
	 * @return this service
	 */
	public CommitFinder setFilter(final RevFilter filter) {
		preFilter = filter;
		return this;
	}

	/**
	 * Set the {@link RevFilter} to use to match filtered commits.
	 * 
	 * @param filter
	 * @return this service
	 */
	public CommitFinder setMatcher(final RevFilter filter) {
		postFilter = filter;
		return this;
	}

	/**
	 * Set the {@link TreeFilter} to use to limit commits visited.
	 * 
	 * @param filter
	 * @return this service
	 */
	public CommitFinder setFilter(final TreeFilter filter) {
		treeFilter = filter;
		return this;
	}

	/**
	 * Create new rev walk
	 * 
	 * @param repository
	 * @return rev walk
	 */
	protected RevWalk createWalk(final Repository repository) {
		final RevWalk walk = new RevWalk(repository);
		walk.setRetainBody(true);
		walk.setRevFilter(preFilter);
		walk.setTreeFilter(treeFilter);
		if (preFilter instanceof CommitFilter)
			((CommitFilter) preFilter).setRepository(repository);
		if (postFilter instanceof CommitFilter)
			((CommitFilter) postFilter).setRepository(repository);
		return walk;
	}

	/**
	 * Traverse commits in given rev walk
	 * 
	 * @param walk
	 * @return this finder
	 */
	protected CommitFinder walk(final RevWalk walk) {
		try {
			final RevFilter filter = postFilter;
			if (filter != null) {
				RevCommit commit;
				while ((commit = walk.next()) != null)
					if (!filter.include(walk, commit))
						return this;
			} else
				while (walk.next() != null)
					;
		} catch (IOException e) {
			throw new GitException(e);
		} catch (StopWalkException ignored) {
			// Ignored
		}
		return this;
	}

	/**
	 * Walk commits between the start commit id and end commit id. Starting
	 * commit and filter cannot be null.
	 * 
	 * @param repository
	 * @param start
	 * @param end
	 * @return this service
	 */
	protected CommitFinder walk(final Repository repository,
			final ObjectId start, final ObjectId end) {
		if (start == null)
			throw new IllegalArgumentException(
					Assert.formatNotNull("Starting commit id"));
		final RevWalk walk = createWalk(repository);
		try {
			walk.markStart(walk.parseCommit(start));
			if (end != null)
				walk.markUninteresting(walk.parseCommit(end));
			return walk(walk);
		} catch (IOException e) {
			throw new GitException(e);
		} finally {
			walk.release();
		}
	}

	/**
	 * Search commits starting from the given commit id.
	 * 
	 * @param start
	 * @return this service
	 */
	public CommitFinder findFrom(final ObjectId start) {
		findBetween(start, (ObjectId) null);
		return this;
	}

	/**
	 * Search commits starting from the given revision string.
	 * 
	 * @param start
	 * @return this service
	 */
	public CommitFinder findFrom(final String start) {
		return findBetween(start, (ObjectId) null);
	}

	/**
	 * Search commits starting at the commit that {@link Constants#HEAD}
	 * currently points to.
	 * 
	 * @return this service
	 */
	public CommitFinder find() {
		return findFrom(Constants.HEAD);
	}

	/**
	 * Search commits starting at the commit that each tag is pointing to
	 * 
	 * @return this finder
	 */
	public CommitFinder findInTags() {
		final Repository[] repos = repositories;
		final int repoCount = repositories.length;
		Repository repo;
		for (int i = 0; i < repoCount; i++) {
			repo = repos[i];
			final Collection<RevCommit> commits = CommitUtils.getTags(repo);
			if (commits.isEmpty())
				continue;
			final RevWalk walk = createWalk(repo);
			try {
				walk.markStart(commits);
			} catch (IOException e) {
				throw new GitException(e);
			}
			walk(walk);
		}
		return this;
	}

	/**
	 * Search commits starting at the commit that each branch is pointing to
	 * 
	 * @return this finder
	 */
	public CommitFinder findInBranches() {
		final Repository[] repos = repositories;
		final int repoCount = repositories.length;
		Repository repo;
		for (int i = 0; i < repoCount; i++) {
			repo = repos[i];
			final Collection<RevCommit> commits = CommitUtils.getBranches(repo);
			if (commits.isEmpty())
				continue;
			final RevWalk walk = createWalk(repo);
			try {
				walk.markStart(commits);
			} catch (IOException e) {
				throw new GitException(e);
			}
			walk(walk);
		}
		return this;
	}

	/**
	 * Search commits between the given start and end commits.
	 * 
	 * @param start
	 * @param end
	 * @return this service
	 */
	public CommitFinder findBetween(final ObjectId start, final ObjectId end) {
		final Repository[] repos = repositories;
		final int repoCount = repositories.length;
		for (int i = 0; i < repoCount; i++)
			walk(repos[i], start, end);
		return this;
	}

	/**
	 * Search commits between the given start revision string and the given end
	 * commit id.
	 * 
	 * @param start
	 * @param end
	 * @return this service
	 */
	public CommitFinder findBetween(final String start, final ObjectId end) {
		final Repository[] repos = repositories;
		final int repoCount = repositories.length;
		Repository repo;
		for (int i = 0; i < repoCount; i++) {
			repo = repos[i];
			walk(repo, CommitUtils.getCommit(repo, start), end);
		}
		return this;
	}

	/**
	 * Search commits between the given start commit id and the given end
	 * revision string.
	 * 
	 * @param start
	 * @param end
	 * @return this service
	 */
	public CommitFinder findBetween(final ObjectId start, final String end) {
		final Repository[] repos = repositories;
		final int repoCount = repositories.length;
		Repository repo;
		for (int i = 0; i < repoCount; i++) {
			repo = repos[i];
			walk(repo, start, CommitUtils.getCommit(repo, end));
		}
		return this;
	}

	/**
	 * Search commits between the given start revision string and the given end
	 * revision string.
	 * 
	 * @param start
	 * @param end
	 * @return this service
	 */
	public CommitFinder findBetween(final String start, final String end) {
		final Repository[] repos = repositories;
		final int repoCount = repositories.length;
		Repository repo;
		for (int i = 0; i < repoCount; i++) {
			repo = repos[i];
			walk(repo, CommitUtils.getCommit(repo, start),
					CommitUtils.getCommit(repo, end));
		}
		return this;
	}
}
