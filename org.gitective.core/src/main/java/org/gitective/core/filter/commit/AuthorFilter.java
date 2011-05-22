/******************************************************************************
 *  Copyright (c) 2011 Kevin Sawicki <kevinsawicki@gmail.com>
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *****************************************************************************/
package org.gitective.core.filter.commit;

import java.io.IOException;

import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.revwalk.filter.RevFilter;

/**
 * Person filter that includes commits where a {@link PersonIdent} matches the
 * name and e-mail address of a commit's author.
 */
public class AuthorFilter extends PersonFilter {

	/**
	 * @param person
	 */
	public AuthorFilter(PersonIdent person) {
		super(person);
	}

	/**
	 * @param name
	 * @param email
	 */
	public AuthorFilter(String name, String email) {
		super(name, email);
	}

	public boolean include(RevWalk walker, RevCommit commit) throws IOException {
		return match(commit.getAuthorIdent());
	}

	/**
	 * @see org.eclipse.jgit.revwalk.filter.RevFilter#clone()
	 */
	public RevFilter clone() {
		return new AuthorFilter(person);
	}

}
