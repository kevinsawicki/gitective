/******************************************************************************
 *  Copyright (c) 2011, Kevin Sawicki <kevinsawicki@gmail.com>
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *****************************************************************************/
package org.gitective.core.filter.commit;

import java.io.IOException;

import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.revwalk.filter.RevFilter;

/**
 * Commit filter that includes commits that match the range of number of
 * parents.
 */
public class ParentCountFilter extends CommitFilter {

	private int min;
	private int max;

	/**
	 * Create a parent count filter that has at least 2 parents.
	 */
	public ParentCountFilter() {
		this(2);
	}

	/**
	 * Create a filter that includes commits that have a parent commit count of
	 * at least the number specified.
	 * 
	 * @param min
	 *            minimum number of parent commits (inclusive)
	 */
	public ParentCountFilter(int min) {
		this(min, Integer.MAX_VALUE);
	}

	/**
	 * Create a filter that includes commits that have a parent commit count
	 * that falls inclusively in the specified range.
	 * 
	 * @param min
	 *            minimum number of parent commits (inclusive)
	 * @param max
	 *            maximum number of parent commits (inclusive)
	 */
	public ParentCountFilter(int min, int max) {
		this.min = min;
		this.max = max;
	}

	@Override
	public boolean include(RevWalk walker, RevCommit commit) throws IOException {
		int parents = commit.getParentCount();
		return parents >= min && parents <= max;
	}

	@Override
	public RevFilter clone() {
		return new ParentCountFilter(min, max);
	}

}
