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
package org.gitective.core.filter.commit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.revwalk.filter.RevFilter;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.TreeFilter;
import org.gitective.core.filter.tree.BaseTreeFilter;

/**
 * Commit pipeline filter that includes commit that are included by each
 * configured pipe.
 */
public class CommitPipelineFilter extends CommitFilter {

	private interface Pipe {

		boolean include(RevWalk commitWalk, TreeWalk treeWalk, RevCommit commit)
				throws IOException;

		Pipe setRepository(Repository repository);
	}

	private static class CommitPipe implements Pipe {

		private final RevFilter filter;

		public CommitPipe(final RevFilter filter) {
			this.filter = filter;
		}

		public boolean include(final RevWalk commitWalk,
				final TreeWalk treeWalk, final RevCommit commit)
				throws IOException {
			return filter.include(commitWalk, commit);
		}

		public Pipe setRepository(final Repository repository) {
			if (filter instanceof CommitFilter)
				((CommitFilter) filter).setRepository(repository);
			return this;
		}
	}

	private static class TreePipe implements Pipe {

		private final TreeFilter filter;

		public TreePipe(final TreeFilter filter) {
			this.filter = filter;
		}

		public boolean include(final RevWalk commitWalk,
				final TreeWalk treeWalk, final RevCommit commit)
				throws IOException {
			treeWalk.reset();
			treeWalk.setFilter(filter);
			while (treeWalk.next())
				if (treeWalk.isSubtree())
					treeWalk.enterSubtree();
			return true;
		}

		public Pipe setRepository(final Repository repository) {
			if (filter instanceof BaseTreeFilter)
				((BaseTreeFilter) filter).setRepository(repository);
			return this;
		}
	}

	private static class CommitTreePipe extends TreePipe {

		private final RevFilter commitFilter;

		public CommitTreePipe(final RevFilter commitFilter,
				final TreeFilter treeFilter) {
			super(treeFilter);
			this.commitFilter = commitFilter;
		}

		public boolean include(final RevWalk commitWalk,
				final TreeWalk treeWalk, final RevCommit commit)
				throws IOException {
			if (!commitFilter.include(commitWalk, commit))
				return false;
			return super.include(commitWalk, treeWalk, commit);
		}

		public Pipe setRepository(final Repository repository) {
			if (commitFilter instanceof CommitFilter)
				((CommitFilter) commitFilter).setRepository(repository);
			return super.setRepository(repository);
		}
	}

	private static class NestedPipe implements Pipe {

		private final CommitPipelineFilter pipeline;

		public NestedPipe(final CommitPipelineFilter pipeline) {
			this.pipeline = pipeline;
		}

		public boolean include(final RevWalk commitWalk,
				final TreeWalk treeWalk, final RevCommit commit)
				throws IOException {
			return pipeline.include(commit, commitWalk, treeWalk);
		}

		public Pipe setRepository(final Repository repository) {
			pipeline.setRepository(repository);
			return this;
		}
	}

	private final List<Pipe> pipes = new ArrayList<Pipe>();

	/**
	 * Add nested pipeline
	 *
	 * @param pipeline
	 * @return this pipeline
	 */
	public CommitPipelineFilter add(final CommitPipelineFilter pipeline) {
		if (pipeline != this)
			pipes.add(new NestedPipe(pipeline));
		return this;
	}

	/**
	 * Add filters to pipeline
	 *
	 * @param commitFilter
	 * @param treeFilter
	 * @return this pipeline
	 */
	public CommitPipelineFilter add(final RevFilter commitFilter,
			final TreeFilter treeFilter) {
		if (commitFilter != null && treeFilter != null)
			pipes.add(new CommitTreePipe(commitFilter, treeFilter));
		else if (commitFilter != null)
			pipes.add(new CommitPipe(commitFilter));
		else if (treeFilter != null)
			pipes.add(new TreePipe(treeFilter));
		return this;
	}

	/**
	 * Add filter to pipeline
	 *
	 * @param treeFilter
	 * @return this pipeline
	 */
	public CommitPipelineFilter add(final TreeFilter treeFilter) {
		return add(null, treeFilter);
	}

	/**
	 * Add filter to pipeline
	 *
	 * @param commitFilter
	 * @return this pipeline
	 */
	public CommitPipelineFilter add(final RevFilter commitFilter) {
		return add(commitFilter, null);
	}

	@Override
	public CommitFilter setRepository(final Repository repository) {
		for (Pipe pipe : pipes)
			pipe.setRepository(repository);
		return super.setRepository(repository);
	}

	/**
	 * Include commit from commit walk and tree walk
	 *
	 * @param commit
	 * @param commitWalk
	 * @param treeWalk
	 * @return true to continue, false to abort
	 * @throws IOException
	 */
	protected boolean include(final RevCommit commit, final RevWalk commitWalk,
			final TreeWalk treeWalk) throws IOException {
		for (Pipe pipe : pipes)
			if (!pipe.include(commitWalk, treeWalk, commit))
				return include(false);
		return true;
	}

	@Override
	public boolean include(final RevWalk commitWalk, final RevCommit commit)
			throws IOException {
		return include(commit, commitWalk,
				new TreeWalk(commitWalk.getObjectReader()));
	}
}
