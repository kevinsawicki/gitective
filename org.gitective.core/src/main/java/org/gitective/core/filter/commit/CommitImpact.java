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

import java.util.Comparator;

import org.eclipse.jgit.revwalk.RevCommit;

/**
 * Class that tracks the impact of a specific commit
 */
public class CommitImpact {

	/**
	 * Compare impact in descending order
	 */
	public static class DescendingImpactComparator implements
			Comparator<CommitImpact> {

		public int compare(CommitImpact o1, CommitImpact o2) {
			return (o2.add + o2.edit + o2.delete)
					- (o1.add + o1.edit + o1.delete);
		}
	}

	private final int add;

	private final int edit;

	private final int delete;

	private final RevCommit commit;

	/**
	 * Create impact
	 *
	 * @param commit
	 * @param add
	 * @param edit
	 * @param delete
	 */
	public CommitImpact(RevCommit commit, int add, int edit, int delete) {
		this.commit = commit;
		this.add = add;
		this.edit = edit;
		this.delete = delete;
	}

	/**
	 * @return add
	 */
	public int getAdd() {
		return add;
	}

	/**
	 * @return edit
	 */
	public int getEdit() {
		return edit;
	}

	/**
	 * @return delete
	 */
	public int getDelete() {
		return delete;
	}

	/**
	 * @return commit
	 */
	public RevCommit getCommit() {
		return commit;
	}

	public String toString() {
		return commit.name() + " +" + add + "/" + edit + "/-" + delete;
	}

	public int hashCode() {
		return commit.hashCode();
	}

	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (!(obj instanceof CommitImpact))
			return false;
		CommitImpact other = (CommitImpact) obj;
		return other.add == add && other.edit == edit && other.delete == delete
				&& commit.equals(other.commit);
	}
}