/******************************************************************************
 *  Copyright (c) 2011, Kevin Sawicki <kevinsawicki@gmail.com>
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *****************************************************************************/
package org.gitective.core.filter.commit;

import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.filter.RevFilter;

/**
 * Base filter that includes commits where a pattern can be found in a commit's
 * full message.
 */
public class CommitMessageFindFilter extends PatternFindCommitFilter {

	/**
	 * Create a commit message pattern filter
	 * 
	 * @param pattern
	 */
	public CommitMessageFindFilter(String pattern) {
		super(pattern);
	}

	/**
	 * Create a commit message pattern filter
	 * 
	 * @param pattern
	 * @param flags
	 */
	public CommitMessageFindFilter(String pattern, int flags) {
		super(pattern, flags);
	}

	@Override
	protected CharSequence getText(RevCommit commit) {
		return commit.getFullMessage();
	}

	@Override
	public RevFilter clone() {
		return new CommitMessageFindFilter(pattern, flags);
	}

}
