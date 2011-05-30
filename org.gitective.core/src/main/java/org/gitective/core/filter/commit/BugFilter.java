/******************************************************************************
 *  Copyright (c) 2011, Kevin Sawicki <kevinsawicki@gmail.com>
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *****************************************************************************/
package org.gitective.core.filter.commit;

import java.util.regex.Pattern;

import org.eclipse.jgit.revwalk.filter.RevFilter;

/**
 * Filter that matches bugs referenced in commit messages.
 */
public class BugFilter extends CommitMessageFindFilter {

	/**
	 * BUG_REGEX
	 */
	public static final String BUG_REGEX = "^Bug: (\\w+)$"; //$NON-NLS-1$;

	/**
	 * Create bug filter
	 */
	public BugFilter() {
		super(BUG_REGEX, Pattern.MULTILINE);
	}

	@Override
	public RevFilter clone() {
		return new BugFilter();
	}

}
