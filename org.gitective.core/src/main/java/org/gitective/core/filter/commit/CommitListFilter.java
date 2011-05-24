/******************************************************************************
 *  Copyright (c) 2011 Kevin Sawicki <kevinsawicki@gmail.com>
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *****************************************************************************/
package org.gitective.core.filter.commit;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.revwalk.filter.RevFilter;

/**
 * Commit filter that adds each visited commit to a list that can be accessed.
 */
public class CommitListFilter extends CommitFilter {

	List<RevCommit> commits = new LinkedList<RevCommit>();

	@Override
	public boolean include(RevWalk walker, RevCommit commit) throws IOException {
		commits.add(commit);
		return true;
	}

	/**
	 * Get commits visited
	 * 
	 * @return non-null but possibly empty list of commits
	 */
	public List<RevCommit> getCommits() {
		return commits;
	}

	@Override
	public CommitFilter reset() {
		commits.clear();
		return super.reset();
	}

	@Override
	public RevFilter clone() {
		return new CommitListFilter();
	}

}
