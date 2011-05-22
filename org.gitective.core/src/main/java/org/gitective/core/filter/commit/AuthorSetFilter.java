/******************************************************************************
 *  Copyright (c) 2011 Kevin Sawicki <kevinsawicki@gmail.com>
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *****************************************************************************/
package org.gitective.core.filter.commit;

import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.filter.RevFilter;

/**
 * Filter that collects author {@link PersonIdent} objects for each commit
 * visited.
 */
public class AuthorSetFilter extends PersonSetFilter {

	@Override
	protected PersonIdent getPerson(RevCommit commit) {
		return commit.getAuthorIdent();
	}

	@Override
	public RevFilter clone() {
		return new AuthorSetFilter();
	}

}
