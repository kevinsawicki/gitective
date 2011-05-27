/******************************************************************************
 *  Copyright (c) 2011, Kevin Sawicki <kevinsawicki@gmail.com>
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *****************************************************************************/
package org.gitective.core.filter.commit;

import java.io.IOException;

import org.eclipse.jgit.errors.StopWalkException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.revwalk.filter.RevFilter;
import org.gitective.core.Assert;

/**
 * Cursor filter that retains the latest commit if not included by the wrapped
 * filter. This can be useful for retaining the commit to start subsequent walks
 * when walking commits in blocks.
 */
public class CommitCursorFilter extends CommitFilter {

	private RevCommit last = null;

	private RevFilter filter;

	/**
	 * Create cursor filter that retains last commit when not included by the
	 * given filter.
	 * 
	 * @param filter
	 */
	public CommitCursorFilter(RevFilter filter) {
		Assert.notNull("Filter cannot be null", filter);
		this.filter = filter;
	}

	public boolean include(RevWalk walker, RevCommit commit) throws IOException {
		try {
			boolean include = filter.include(walker, commit);
			last = include ? null : commit;
			return include;
		} catch (IOException e) {
			last = commit;
			throw e;
		} catch (StopWalkException e) {
			last = commit;
			throw e;
		}
	}

	/**
	 * Get last commit visited.
	 * 
	 * @return commit
	 */
	public RevCommit getLast() {
		return this.last;
	}

	@Override
	public CommitFilter reset() {
		last = null;
		if (filter instanceof CommitFilter)
			((CommitFilter) filter).reset();
		return super.reset();
	}

	public RevFilter clone() {
		return new CommitCursorFilter(filter);
	}

}
