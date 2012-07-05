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
package org.gitective.core.filter.tree;

import java.io.IOException;

import org.eclipse.jgit.errors.StopWalkException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.TreeFilter;

/**
 * Base tree filter to be extended by sub-classes.
 */
public abstract class BaseTreeFilter extends TreeFilter {

	/**
	 * Wrap the given non-null tree filter in a {@link BaseTreeFilter}
	 * <p>
	 * This will simply return the given filter if it is already a
	 * {@link BaseTreeFilter}
	 *
	 * @param filter
	 *            must be non-null
	 * @return base tree filter
	 */
	public static BaseTreeFilter wrap(final TreeFilter filter) {
		if (filter == null)
			throw new IllegalArgumentException("Filter cannot be null");

		if (filter instanceof BaseTreeFilter)
			return (BaseTreeFilter) filter;
		else
			return new FilterWrapper(filter);
	}

	/**
	 * Class that wraps a {@link TreeFilter} in a {@link BaseTreeFilter}
	 */
	private static class FilterWrapper extends BaseTreeFilter {

		private final TreeFilter filter;

		/**
		 * Wrap tree filter in a {@link BaseTreeFilter}
		 *
		 * @param filter
		 */
		protected FilterWrapper(final TreeFilter filter) {
			this.filter = filter;
		}

		public boolean include(final TreeWalk walker) throws IOException {
			return filter.include(walker);
		}
	}

	private boolean stop;

	/**
	 * Repository for current walk
	 */
	protected Repository repository;

	/**
	 * Set whether the search should be stopped when a commit visited is not
	 * included.
	 *
	 * @param stop
	 * @return this filter
	 */
	public BaseTreeFilter setStop(final boolean stop) {
		this.stop = stop;
		return this;
	}

	/**
	 * Set the repository for the walk that is about to begin.
	 *
	 * @param repository
	 * @return this filter
	 */
	public BaseTreeFilter setRepository(final Repository repository) {
		this.repository = repository;
		return this;
	}

	/**
	 * Include tree walk from commit and rev walk
	 *
	 * @see TreeFilter#include(TreeWalk)
	 * @param commitWalk
	 * @param commit
	 * @param treeWalk
	 * @return true to include, false to abort
	 * @throws IOException
	 */
	public boolean include(final RevWalk commitWalk, final RevCommit commit,
			final TreeWalk treeWalk) throws IOException {
		return include(treeWalk);
	}

	/**
	 * Return the include value given unless include is false and this filter is
	 * configured to stop the search when a commit is not included.
	 *
	 * @param include
	 * @return include parameter value
	 */
	protected boolean include(final boolean include) {
		if (!include && stop)
			throw StopWalkException.INSTANCE;
		return include;
	}

	/**
	 * Reset the filter. Sub-classes may override.
	 *
	 * @return this tree filter
	 */
	public BaseTreeFilter reset() {
		return this;
	}

	@Override
	public boolean shouldBeRecursive() {
		return true;
	}

	/**
	 * Clones the tree filter.
	 * <p>
	 * The default implementation throws a {@link UnsupportedOperationException}
	 * and sub-classes should override if filter cloning is supported.
	 *
	 * @see TreeFilter#clone()
	 */
	@Override
	public TreeFilter clone() {
		throw new UnsupportedOperationException("Clone not supported");
	}
}
