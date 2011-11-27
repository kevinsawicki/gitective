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

import static org.eclipse.jgit.treewalk.filter.TreeFilter.ANY_DIFF;

import java.io.IOException;

import org.eclipse.jgit.lib.AnyObjectId;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.EmptyTreeIterator;
import org.eclipse.jgit.treewalk.TreeWalk;

/**
 * Utilities for dealing with Git trees.
 */
public abstract class TreeUtils {

	/**
	 * Get the tree associated with the given commit.
	 *
	 * @param walk
	 * @param commit
	 * @return tree
	 * @throws IOException
	 */
	protected static RevTree getTree(final RevWalk walk, final RevCommit commit)
			throws IOException {
		final RevTree tree = commit.getTree();
		if (tree != null)
			return tree;
		walk.parseHeaders(commit);
		return commit.getTree();
	}

	/**
	 * Create a tree walk with the commit's parents.
	 *
	 * @param reader
	 * @param rWalk
	 * @param commit
	 * @return tree walk
	 * @throws IOException
	 */
	protected static TreeWalk withParents(final ObjectReader reader,
			final RevWalk rWalk, final RevCommit commit) throws IOException {
		final TreeWalk walk = new TreeWalk(reader);
		final int parentCount = commit.getParentCount();
		try {
			switch (parentCount) {
			case 0:
				walk.addTree(new EmptyTreeIterator());
				break;
			case 1:
				walk.addTree(getTree(rWalk, commit.getParent(0)));
				break;
			default:
				final RevCommit[] parents = commit.getParents();
				for (int i = 0; i < parentCount; i++)
					walk.addTree(getTree(rWalk, parents[i]));
			}
			walk.addTree(getTree(rWalk, commit));
		} catch (IOException e) {
			throw new GitException(e, null);
		}
		return walk;
	}

	/**
	 * Create a tree walk with all the trees from the given commit's parents.
	 *
	 * @param repository
	 * @param commitId
	 * @return tree walk
	 */
	public static TreeWalk withParents(final Repository repository,
			final AnyObjectId commitId) {
		if (repository == null)
			throw new IllegalArgumentException(
					Assert.formatNotNull("Repository"));
		if (commitId == null)
			throw new IllegalArgumentException(
					Assert.formatNotNull("Commit id"));

		final ObjectReader reader = repository.newObjectReader();
		final RevWalk walk = new RevWalk(reader);
		try {
			return withParents(reader, walk, walk.parseCommit(commitId));
		} catch (IOException e) {
			walk.release();
			throw new GitException(e, null);
		}
	}

	/**
	 * Create a tree walk with all the trees from the given revision's commit
	 * parents.
	 *
	 * @param repository
	 * @param revision
	 * @return tree walk
	 */
	public static TreeWalk withParents(final Repository repository,
			final String revision) {
		if (repository == null)
			throw new IllegalArgumentException(
					Assert.formatNotNull("Repository"));
		if (revision == null)
			throw new IllegalArgumentException(Assert.formatNotNull("Revision"));
		if (revision.length() == 0)
			throw new IllegalArgumentException(
					Assert.formatNotEmpty("Revision"));

		final ObjectId commit = CommitUtils.resolve(repository, revision);
		final ObjectReader reader = repository.newObjectReader();
		final RevWalk walk = new RevWalk(reader);
		try {
			return withParents(reader, walk, walk.parseCommit(commit));
		} catch (IOException e) {
			walk.release();
			throw new GitException(e, null);
		}
	}

	/**
	 * Create a tree walk configured to diff the given commit against all the
	 * parent commits.
	 *
	 * @param repository
	 * @param commitId
	 * @return tree walk
	 */
	public static TreeWalk diffWithParents(final Repository repository,
			final AnyObjectId commitId) {
		final TreeWalk walk = withParents(repository, commitId);
		walk.setFilter(ANY_DIFF);
		return walk;
	}

	/**
	 * Create a tree walk configured to diff the given revision against all the
	 * parent commits.
	 *
	 * @param repository
	 * @param revision
	 * @return tree walk
	 */
	public static TreeWalk diffWithParents(final Repository repository,
			final String revision) {
		final TreeWalk walk = withParents(repository, revision);
		walk.setFilter(ANY_DIFF);
		return walk;
	}
}
