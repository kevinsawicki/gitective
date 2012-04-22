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

import static org.eclipse.jgit.lib.FileMode.TYPE_MASK;
import static org.eclipse.jgit.lib.FileMode.TYPE_TREE;
import static org.eclipse.jgit.treewalk.filter.TreeFilter.ANY_DIFF;

import java.io.IOException;

import org.eclipse.jgit.lib.AnyObjectId;
import org.eclipse.jgit.lib.FileMode;
import org.eclipse.jgit.lib.MutableObjectId;
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
	 * Interface for visiting elements in a tree
	 */
	public static interface ITreeVisitor {

		/**
		 * Visit the given element
		 *
		 * @param mode
		 *            mode of current entry
		 * @param path
		 *            parent path of entry, null for root entries
		 * @param name
		 *            name of current entry
		 * @param id
		 *            id of current entry
		 * @return true to continue, false to abort
		 */
		boolean accept(FileMode mode, String path, String name, AnyObjectId id);
	}

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
			throw new GitException(e, repository);
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

		final ObjectId commit = CommitUtils.strictResolve(repository, revision);
		final ObjectReader reader = repository.newObjectReader();
		final RevWalk walk = new RevWalk(reader);
		try {
			return withParents(reader, walk, walk.parseCommit(commit));
		} catch (IOException e) {
			walk.release();
			throw new GitException(e, repository);
		}
	}

	/**
	 * Create a tree walk with all the trees from the given commit's parents.
	 *
	 * @param walk
	 * @param commit
	 * @return tree walk
	 */
	public static TreeWalk withParents(final RevWalk walk,
			final RevCommit commit) {
		if (walk == null)
			throw new IllegalArgumentException(Assert.formatNotNull("Walk"));
		if (commit == null)
			throw new IllegalArgumentException(Assert.formatNotNull("Commit"));

		try {
			return withParents(walk.getObjectReader(), walk, commit);
		} catch (IOException e) {
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
	 * Create a tree walk configured to diff the given commit against all the
	 * parent commits.
	 *
	 * @param walk
	 * @param commit
	 * @return tree walk
	 */
	public static TreeWalk diffWithParents(final RevWalk walk,
			final RevCommit commit) {
		final TreeWalk treeWalk = withParents(walk, commit);
		treeWalk.setFilter(ANY_DIFF);
		return treeWalk;
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

	/**
	 * Create a tree walk configured with the given commit revisions
	 *
	 * @param repository
	 * @param revisions
	 * @return tree walk
	 */
	public static TreeWalk withCommits(final Repository repository,
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

		final TreeWalk walk = new TreeWalk(repository);
		try {
			for (String revision : revisions)
				walk.addTree(CommitUtils.getCommit(repository, revision)
						.getTree());
		} catch (IOException e) {
			throw new GitException(e, repository);
		}
		return walk;
	}

	/**
	 * Create a tree walk configured with the given commits
	 *
	 * @param repository
	 * @param commits
	 * @return tree walk
	 */
	public static TreeWalk withCommits(final Repository repository,
			final ObjectId... commits) {
		if (repository == null)
			throw new IllegalArgumentException(
					Assert.formatNotNull("Repository"));
		if (commits == null)
			throw new IllegalArgumentException(Assert.formatNotNull("Commits"));
		if (commits.length == 0)
			throw new IllegalArgumentException(Assert.formatNotEmpty("Commits"));

		final TreeWalk walk = new TreeWalk(repository);
		try {
			for (ObjectId commit : commits)
				walk.addTree(CommitUtils.getCommit(repository, commit)
						.getTree());
		} catch (IOException e) {
			throw new GitException(e, repository);
		}
		return walk;
	}

	/**
	 * Create a tree walk configured to diff the given commits
	 *
	 * @param repository
	 * @param commits
	 * @return tree walk
	 */
	public static TreeWalk diffWithCommits(final Repository repository,
			final ObjectId... commits) {
		final TreeWalk walk = withCommits(repository, commits);
		walk.setFilter(ANY_DIFF);
		return walk;
	}

	/**
	 * Create a tree walk configured to diff the given commit revisions
	 *
	 * @param repository
	 * @param revisions
	 * @return tree walk
	 */
	public static TreeWalk diffWithCommits(final Repository repository,
			final String... revisions) {
		final TreeWalk walk = withCommits(repository, revisions);
		walk.setFilter(ANY_DIFF);
		return walk;
	}

	/**
	 * Get the id of the tree at the path in the given commit.
	 *
	 * @param repository
	 * @param commit
	 * @param path
	 * @return tree id, null if not present
	 */
	protected static ObjectId lookupId(final Repository repository,
			final RevCommit commit, final String path) {
		final TreeWalk walk;
		try {
			walk = TreeWalk.forPath(repository, path, commit.getTree());
		} catch (IOException e) {
			throw new GitException(e, repository);
		}
		if (walk == null)
			return null;
		if ((walk.getRawMode(0) & TYPE_MASK) != TYPE_TREE)
			return null;
		return walk.getObjectId(0);
	}

	/**
	 * Get the id of the tree at the path in the given commit
	 *
	 * @param repository
	 * @param commitId
	 * @param path
	 * @return tree id or null if no tree id at path
	 */
	public static ObjectId getId(final Repository repository,
			final ObjectId commitId, final String path) {
		if (repository == null)
			throw new IllegalArgumentException(
					Assert.formatNotNull("Repository"));
		if (commitId == null)
			throw new IllegalArgumentException(
					Assert.formatNotNull("Commit Id"));
		if (path == null)
			throw new IllegalArgumentException(Assert.formatNotNull("Path"));
		if (path.length() == 0)
			throw new IllegalArgumentException(Assert.formatNotNull("Path"));

		final RevCommit commit = CommitUtils.parse(repository, commitId);
		return lookupId(repository, commit, path);
	}

	/**
	 * Get the id of the tree at the path in the given revision
	 *
	 * @param repository
	 * @param revision
	 * @param path
	 * @return tree id or null if no tree id at path
	 */
	public static ObjectId getId(final Repository repository,
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
			throw new IllegalArgumentException(Assert.formatNotNull("Path"));

		final RevCommit commit = CommitUtils.parse(repository,
				CommitUtils.strictResolve(repository, revision));
		return lookupId(repository, commit, path);
	}

	/**
	 * Visit entries in the given tree
	 *
	 * @param repository
	 * @param treeId
	 * @param visitor
	 * @return true if fully completed, false if terminated early
	 */
	public static boolean visit(final Repository repository,
			final ObjectId treeId, final ITreeVisitor visitor) {
		if (repository == null)
			throw new IllegalArgumentException(
					Assert.formatNotNull("Repository"));
		if (treeId == null)
			throw new IllegalArgumentException(Assert.formatNotNull("Tree Id"));
		if (visitor == null)
			throw new IllegalArgumentException(Assert.formatNotNull("Visitor"));

		final TreeWalk walk = new TreeWalk(repository);
		walk.setPostOrderTraversal(true);
		final MutableObjectId id = new MutableObjectId();
		try {
			walk.addTree(treeId);
			if (!visit(repository, walk, id, null, visitor))
				return false;
		} catch (IOException e) {
			throw new GitException(e, repository);
		} finally {
			walk.release();
		}
		return true;
	}

	private static boolean visit(final Repository repository,
			final TreeWalk walk, final MutableObjectId id, final String path,
			final ITreeVisitor visitor) throws IOException {
		while (walk.next()) {
			if (walk.isPostChildren())
				break;

			if (walk.isSubtree()) {
				final String subTreePath = walk.getPathString();
				walk.enterSubtree();
				if (!visit(repository, walk, id, subTreePath, visitor))
					return false;
			}

			walk.getObjectId(id, 0);
			if (!visitor.accept(walk.getFileMode(0), path,
					walk.getNameString(), id))
				return false;
		}
		return true;
	}
}
