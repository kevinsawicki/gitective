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

import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.filter.RevFilter;
import org.gitective.core.filter.commit.CommitImpact.DescendingImpactComparator;

/**
 * Filter that tracks the impact of commits measure in terms of lines changed
 */
public class CommitLineImpactFilter extends CommitDiffEditFilter implements
		Iterable<CommitImpact> {

	private final int limit;

	private final SortedSet<CommitImpact> commits = new TreeSet<CommitImpact>(
			new DescendingImpactComparator());

	private int add;

	private int edit;

	private int delete;

	/**
	 * Create an impact filter that retains the given number of most impacting
	 * commits
	 *
	 * @param detectRenames
	 * @param limit
	 */
	public CommitLineImpactFilter(final boolean detectRenames, final int limit) {
		super(detectRenames);
		this.limit = limit;
	}

	/**
	 * Create an impact filter that tracks the 10 most impacting commits
	 *
	 * @param detectRenames
	 */
	public CommitLineImpactFilter(final boolean detectRenames) {
		this(detectRenames, 10);
	}

	/**
	 * Create an impact filter that tracks the 10 most impacting commits
	 */
	public CommitLineImpactFilter() {
		this(false);
	}

	/**
	 * Create an impact filter that retains the given number of most impacting
	 * commits
	 *
	 * @param limit
	 */
	public CommitLineImpactFilter(final int limit) {
		this(false, limit);
	}

	/**
	 * @return limit
	 */
	public int getLimit() {
		return limit;
	}

	/**
	 * @return commits
	 */
	public SortedSet<CommitImpact> getCommits() {
		return commits;
	}

	@Override
	protected CommitDiffEditFilter markStart(final RevCommit commit) {
		add = 0;
		edit = 0;
		delete = 0;
		return super.markStart(commit);
	}

	@Override
	protected CommitDiffEditFilter markEnd(final RevCommit commit) {
		commits.add(new CommitImpact(commit, add, edit, delete));
		if (commits.size() > limit)
			commits.remove(commits.last());
		return super.markEnd(commit);
	}

	@Override
	protected boolean include(RevCommit commit, DiffEntry diff, Edit hunk) {
		switch (hunk.getType()) {
		case DELETE:
			delete += hunk.getLengthA();
			break;
		case INSERT:
			add += hunk.getLengthB();
			break;
		case REPLACE:
			edit += hunk.getLengthB();
			break;
		}
		return true;
	}

	@Override
	public RevFilter clone() {
		return new CommitLineImpactFilter(detectRenames, limit);
	}

	@Override
	public CommitFilter reset() {
		commits.clear();
		return super.reset();
	}

	public Iterator<CommitImpact> iterator() {
		return commits.iterator();
	}
}
