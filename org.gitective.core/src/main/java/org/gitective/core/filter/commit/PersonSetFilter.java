/******************************************************************************
 *  Copyright (c) 2011, Kevin Sawicki <kevinsawicki@gmail.com>
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *****************************************************************************/
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
		return this.persons;
	}

	/**
	 * Get the person from the commit to include in set
	 * 
	 * @param commit
	 * @return person
	 */
	protected abstract PersonIdent getPerson(RevCommit commit);

}
