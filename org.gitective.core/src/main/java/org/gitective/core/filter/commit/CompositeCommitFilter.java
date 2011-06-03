/******************************************************************************
 *  Copyright (c) 2011, Kevin Sawicki <kevinsawicki@gmail.com>
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *****************************************************************************/
package org.gitective.core.filter.commit;

import org.eclipse.jgit.revwalk.filter.RevFilter;

/**
 * Filter container other filters
 */
public abstract class CompositeCommitFilter extends CommitFilter {

	/**
	 * Child filters
	 */
	protected RevFilter[] filters;

	/**
	 * Create a composite filter with given child filters
	 * 
	 * @param filters
	 */
	public CompositeCommitFilter(RevFilter... filters) {
		if (filters != null && filters.length > 0) {
			this.filters = new RevFilter[filters.length];
			System.arraycopy(filters, 0, this.filters, 0, filters.length);
		} else
			this.filters = new RevFilter[0];
	}

	/**
	 * Add child filters to this filter. This method resizes an internal array
	 * on each call so it should be called with as many child filters at once
	 * instead of once per child filter.
	 * 
	 * @param addedFilters
	 * @return this filter
	 */
	public CompositeCommitFilter add(RevFilter... addedFilters) {
		if (addedFilters == null || addedFilters.length == 0)
			return this;
		RevFilter[] resized = new RevFilter[addedFilters.length
				+ filters.length];
		System.arraycopy(filters, 0, resized, 0, filters.length);
		System.arraycopy(addedFilters, 0, resized, filters.length,
				addedFilters.length);
		filters = resized;
		return this;
	}

	@Override
	public CommitFilter reset() {
		for (RevFilter filter : filters)
			if (filter instanceof CommitFilter)
				((CommitFilter) filter).reset();
		return super.reset();
	}

	/**
	 * Clone each filter into a new array.
	 * 
	 * @return non-null but possibly empty array of child filters
	 */
	protected RevFilter[] cloneFilters() {
		RevFilter[] clone = new RevFilter[filters.length];
		System.arraycopy(filters, 0, clone, 0, clone.length);
		return clone;
	}
}
