/*
 * Copyright (c) 2011 Kevin Sawicki <kevinsawicki@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to
 * deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 */
package org.gitective.core.filter.commit;

import org.eclipse.jgit.lib.PersonIdent;

/**
 * Base commit filter that contains utility methods for matching the configured
 * {@link PersonIdent}.
 */
public abstract class PersonFilter extends CommitFilter {

	/**
	 * Name matching against
	 */
	protected final String name;

	/**
	 * E-mail matching against
	 */
	protected final String email;

	/**
	 * Create a person filter
	 *
	 * @param name
	 * @param email
	 */
	public PersonFilter(final String name, final String email) {
		this.name = name;
		this.email = email;
	}

	/**
	 * Create a person filter
	 *
	 * @param person
	 */
	public PersonFilter(final PersonIdent person) {
		if (person != null) {
			name = person.getName();
			email = person.getEmailAddress();
		} else {
			name = null;
			email = null;
		}
	}

	/**
	 * Match the specified {@link PersonIdent} against the name and e-mail
	 * address of the configured {@link PersonIdent}.
	 *
	 * @param ident
	 * @return true on matches, false otherwise
	 */
	protected boolean match(final PersonIdent ident) {
		if (ident == null)
			return name == null && email == null;

		if (name != null && !name.equals(ident.getName()))
			return false;

		if (email != null && !email.equals(ident.getEmailAddress()))
			return false;

		return true;
	}
}
