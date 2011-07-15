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
import org.gitective.core.Assert;
import org.gitective.core.Check;
import org.gitective.core.PersonComparator;

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
	public PersonFilter(final String name, final String email) {
		if (name == null)
			throw new IllegalArgumentException(Assert.formatNotNull("Name"));
		if (email == null)
			throw new IllegalArgumentException(Assert.formatNotNull("E-mail"));
		person = new PersonIdent(name, email);
	}

	/**
	 * Create a person filter
	 * 
	 * @param person
	 */
	public PersonFilter(final PersonIdent person) {
		this.person = person;
	}

	/**
	 * Match the specified {@link PersonIdent} against the name and e-mail
	 * address of the configured {@link PersonIdent}.
	 * 
	 * @param ident
	 * @return true on matches, false otherwise
	 */
	protected boolean match(final PersonIdent ident) {
		if (Check.allNull(person, ident))
			return true;

		if (Check.anyNull(person, ident))
			return false;

		return PersonComparator.INSTANCE.equals(person, ident);
	}
}
