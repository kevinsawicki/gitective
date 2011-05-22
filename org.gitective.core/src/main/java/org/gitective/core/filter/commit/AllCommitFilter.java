/*******************************************************************************
 *  Copyright (c) 2011 Kevin Sawicki <kevinsawicki@gmail.com>
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.gitective.core.filter.commit;

import java.io.IOException;

import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.revwalk.filter.RevFilter;

/**
 * Composite filter that will always include every commit but still calls the
 * {@link RevFilter#include(RevWalk, RevCommit)} method on each filter that has
 * been added to this filter. Most often used when you want to ensure that a
 * collection of filters are each always called on every commit in a
 * {@link RevWalk}.
 */
public class AllCommitFilter extends CompositeCommitFilter {

	@Override
	public boolean include(RevWalk walker, RevCommit commit)
			throws MissingObjectException, IncorrectObjectTypeException,
			IOException {
		for (RevFilter filter : filters)
			filter.include(walker, commit);
		return true;
	}

	@Override
	public RevFilter clone() {
		AllCommitFilter filter = new AllCommitFilter();
		cloneFilters(filter.filters);
		return filter;
	}

}
