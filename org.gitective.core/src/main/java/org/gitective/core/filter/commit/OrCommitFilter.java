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
 * Composite filter that only includes commits that are included by at least one
 * child filter that has been added to this filter. This filter stops matching
 * against child filters when the first child filter matches the current commit.
 */
public class OrCommitFilter extends CompositeCommitFilter {

	/**
	 * Create an empty or commit filter
	 */
	public OrCommitFilter() {
		super();
	}

	/**
	 * Create an or commit filter with given child filters
	 * 
	 * @param filters
	 */
	public OrCommitFilter(RevFilter... filters) {
		super(filters);
	}

	@Override
	public boolean include(RevWalk walker, RevCommit commit) throws IOException {
		final int length = filters.length;
		for (int i = 0; i < length; i++)
			if (filters[i].include(walker, commit))
				return true;
		return include(false);
	}

	@Override
	public RevFilter clone() {
		return new OrCommitFilter(cloneFilters());
	}
}
