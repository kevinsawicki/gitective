/******************************************************************************
 *  Copyright (c) 2011, Kevin Sawicki <kevinsawicki@gmail.com>
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *****************************************************************************/
package org.gitective.core.filter.commit;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.gitective.core.Assert;

/**
 * Base commit filter class that includes commits where a configured pattern can
 * be found.
 */
public abstract class PatternFindCommitFilter extends CommitFilter {

	/**
	 * Pattern
	 */
	protected final String pattern;

	/**
	 * Pattern matcher
	 */
	protected Matcher matcher;

	/**
	 * Create a pattern find commit filter
	 * 
	 * @param pattern
	 */
	public PatternFindCommitFilter(String pattern) {
		this(pattern, 0);
	}

	/**
	 * Create a pattern find commit filter
	 * 
	 * @param pattern
	 * @param flags
	 */
	public PatternFindCommitFilter(String pattern, int flags) {
		Assert.notNull("Pattern cannot be null", pattern);
		Assert.notEmpty("Pattern cannot be empty", pattern);
		this.pattern = pattern;
		this.matcher = Pattern.compile(pattern, flags).matcher("");
	}

	@Override
	public boolean include(final RevWalk walker, final RevCommit commit)
			throws IOException {
		return matcher.reset(getText(commit)).find();
	}

	/**
	 * Get the text from the commit to find the pattern in
	 * 
	 * @param commit
	 * @return text
	 */
	protected abstract CharSequence getText(RevCommit commit);

}
