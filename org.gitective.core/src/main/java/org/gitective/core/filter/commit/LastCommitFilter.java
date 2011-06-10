/******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *****************************************************************************/
package org.gitective.core.filter.commit;

import java.io.IOException;

import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.revwalk.filter.RevFilter;

/**
 * 
 */
public class LastCommitFilter extends CommitFilter {

	private RevCommit last;

	@Override
	public boolean include(RevWalk walker, RevCommit commit) throws IOException {
		last = commit;
		return true;
	}

	/**
	 * Get last commit seen
	 * 
	 * @return commit or null if none seen sine creation or last reset
	 */
	public RevCommit getLast() {
		return last;
	}

	public CommitFilter reset() {
		last = null;
		return super.reset();
	}

	@Override
	public RevFilter clone() {
		return new LastCommitFilter();
	}

}
