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
 * Composite filter that only includes commits that are included by every child
 * filter that has been added to this filter.
 */
public class AndCommitFilter extends CompositeCommitFilter {

	/**
	 * Create empty and commit filter
	 */
	public AndCommitFilter() {
		super();
	}

	/**
	 * Create and commit filter with given child filters
	 * 
	 * @param filters
	 */
	public AndCommitFilter(RevFilter... filters) {
		super(filters);
	}

	@Override
	public boolean include(RevWalk walker, RevCommit commit) throws IOException {
		for (RevFilter filter : filters)
			if (!filter.include(walker, commit))
				return include(false);
		return true;
	}

	@Override
	public RevFilter clone() {
		AndCommitFilter clone = new AndCommitFilter();
		cloneFilters(clone.filters);
		return clone;
	}

}
