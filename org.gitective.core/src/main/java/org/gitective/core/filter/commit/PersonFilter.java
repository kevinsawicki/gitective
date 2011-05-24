/******************************************************************************
 *  Copyright (c) 2011, Kevin Sawicki <kevinsawicki@gmail.com>
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *****************************************************************************/
package org.gitective.core.filter.commit;

import org.eclipse.jgit.lib.PersonIdent;

/**
 * Base commit filter that contains utility methods for matching the configured
 * {@link PersonIdent}.
 */
public abstract class PersonFilter extends CommitFilter {

	/**
	 * Person matching against
	 */
	protected final PersonIdent person;

	/**
	 * Create a person filter
	 * 
	 * @param name
	 * @param email
	 */
	public PersonFilter(String name, String email) {
		this(new PersonIdent(name, email));
	}

	/**
	 * Create a person filter
	 * 
	 * @param person
	 */
	public PersonFilter(PersonIdent person) {
		this.person = person;
	}

	/**
	 * Match the specified {@link PersonIdent} against the name and e-mail
	 * address of the configured {@link PersonIdent}.
	 * 
	 * @param ident
	 * @return true on matches, false otherwise
	 */
	protected boolean match(PersonIdent ident) {
		if (equalsNull(person, ident))
			return true;

		return person != null && ident != null
				&& equals(person.getName(), ident.getName())
				&& equals(person.getEmailAddress(), ident.getEmailAddress());
	}
}
