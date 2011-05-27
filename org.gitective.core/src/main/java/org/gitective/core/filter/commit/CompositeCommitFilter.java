/******************************************************************************
 *  Copyright (c) 2011, Kevin Sawicki <kevinsawicki@gmail.com>
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *****************************************************************************/
package org.gitective.core.filter.commit;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jgit.revwalk.filter.RevFilter;
import org.gitective.core.Assert;

/**
 * Filter container other filters
 */
public abstract class CompositeCommitFilter extends CommitFilter {

	/**
	 * Child filters
	 */
	protected final List<RevFilter> filters = new LinkedList<RevFilter>();

	/**
	 * Create an empty composite filter
	 */
	public CompositeCommitFilter() {

	}

	/**
	 * Create a composite filter with given child filters
	 * 
	 * @param filters
	 */
	public CompositeCommitFilter(RevFilter... filters) {
		Assert.notNull("Filter cannot be null", filters);
		for (RevFilter filter : filters)
			add(filter);
	}

	/**
	 * Add a non-null child filter to this filter.
	 * 
	 * @param filter
	 * @return this filter
	 */
	public CompositeCommitFilter add(RevFilter filter) {
		if (filter != null && filter != this)
			this.filters.add(filter);
		return this;
	}

	public CommitFilter reset() {
		for (RevFilter filter : filters)
			if (filter instanceof CommitFilter)
				((CommitFilter) filter).reset();
		return super.reset();
	}

	/**
	 * Clone each filter and add to the specified list.
	 * 
	 * @param cloned
	 * @return non-null but possibly empty list of child filters
	 */
	protected List<RevFilter> cloneFilters(List<RevFilter> cloned) {
		for (RevFilter filter : filters)
			cloned.add(filter.clone());
		return cloned;
	}

}
