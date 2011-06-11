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

import java.io.IOException;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

/**
 * Commit filter that stores a {@link TreeSet} of persons encountered while
 * visiting commits.
 */
public abstract class PersonSetFilter extends CommitFilter {

	/**
	 * Persons
	 */
	protected final Set<PersonIdent> persons;

	/**
	 * Create a person set filter using a {@link PersonComparator}
	 */
	public PersonSetFilter() {
		this(new PersonComparator());
	}

	/**
	 * Create a person set filter using the given comparator
	 * 
	 * @param comparator
	 */
	public PersonSetFilter(Comparator<PersonIdent> comparator) {
		persons = new TreeSet<PersonIdent>(comparator);
	}

	@Override
	public boolean include(RevWalk walker, RevCommit commit) throws IOException {
		PersonIdent person = getPerson(commit);
		if (person != null)
			persons.add(person);
		return true;
	}

	@Override
	public CommitFilter reset() {
		persons.clear();
		return super.reset();
	}

	/**
	 * Get persons encountered during commit visiting
	 * 
	 * @return non-null but possibly empty set of persons
	 */
	public Set<PersonIdent> getPersons() {
		return persons;
	}

	/**
	 * Get the person from the commit to include in set
	 * 
	 * @param commit
	 * @return person
	 */
	protected abstract PersonIdent getPerson(RevCommit commit);

}
