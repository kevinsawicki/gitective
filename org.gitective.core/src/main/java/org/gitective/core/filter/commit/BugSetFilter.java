/******************************************************************************
 *  Copyright (c) 2011, Kevin Sawicki <kevinsawicki@gmail.com>
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *****************************************************************************/
package org.gitective.core.filter.commit;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.revwalk.filter.RevFilter;

/**
 * Filter that collects all bugs referenced in commit messages.
 */
public class BugSetFilter extends CommitMessageFindFilter {

	private final Set<String> bugs = new HashSet<String>();

	/**
	 * Create new bug set filter
	 */
	public BugSetFilter() {
		super(BugFilter.BUG_REGEX, Pattern.MULTILINE);
	}

	/**
	 * Get bugs collected
	 * 
	 * @return non-null but possibly empty set of bug ids
	 */
	public Set<String> getBugs() {
		return bugs;
	}

	@Override
	public boolean include(final RevWalk walker, final RevCommit commit)
			throws IOException {
		matcher.reset(getText(commit));
		while (matcher.find())
			bugs.add(matcher.group(1));
		return true;
	}

	@Override
	public RevFilter clone() {
		return new BugSetFilter();
	}

	@Override
	public CommitFilter reset() {
		bugs.clear();
		return super.reset();
	}
}
