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
 * Commit message pattern filter that includes commits that contain a valid
 * Gerrit {@link #CHANGE_ID_REGEX}.
 */
public class ChangeIdFilter extends CommitMessageFindFilter {

	/**
	 * CHANGE_ID_REGEX
	 */
	public static final String CHANGE_ID_REGEX = "Change-Id: I[0-9a-f]{40}"; //$NON-NLS-1$

	/**
	 * Create change id filter
	 */
	public ChangeIdFilter() {
		super(CHANGE_ID_REGEX, Pattern.MULTILINE);
	}

	@Override
	public RevFilter clone() {
		return this;
	}

}
