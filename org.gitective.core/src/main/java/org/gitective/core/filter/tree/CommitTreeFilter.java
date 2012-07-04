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

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.TreeFilter;
import org.gitective.core.filter.commit.CommitFilter;

/**
 * Filter that wraps a {@link TreeFilter} and includes commits included by the
 * tree filter for all paths in the tree for the current commit visited
 */
public class CommitTreeFilter extends CommitFilter {

	/**
	 * Class that wraps a {@link TreeFilter} in a {@link BaseTreeFilter} so that
	 * {@link BaseTreeFilter#include(RevWalk, RevCommit, TreeWalk)} can always
	 * be called from {@link CommitTreeFilter#include(RevWalk, RevCommit)}
	 */
	protected static class FilterWrapper extends BaseTreeFilter {

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

	private final BaseTreeFilter filter;

	/**
	 * Create commit filter for given tree filter
	 *
	 * @param filter
	 */
	public CommitTreeFilter(final TreeFilter filter) {
		if (filter == null)
			throw new IllegalArgumentException("Filter cannot be null");

		if (filter instanceof BaseTreeFilter)
			this.filter = (BaseTreeFilter) filter;
		else
			this.filter = new FilterWrapper(filter);
	}

	@Override
	public CommitFilter setRepository(final Repository repository) {
		filter.setRepository(repository);
		return super.setRepository(repository);
	}

	@Override
	public CommitFilter reset() {
		filter.reset();
		return super.reset();
	}

	@Override
	public boolean include(final RevWalk walker, final RevCommit commit)
			throws IOException {
		final TreeWalk walk = new TreeWalk(walker.getObjectReader());
		walk.addTree(commit.getTree());
		while (walk.next()) {
			if (!filter.include(walker, commit, walk))
				return include(false);
			if (walk.isSubtree())
				walk.enterSubtree();
		}
		return true;
	}
}
