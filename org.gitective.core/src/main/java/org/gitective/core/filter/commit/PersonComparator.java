/******************************************************************************
 *  Copyright (c) 2011, Kevin Sawicki <kevinsawicki@gmail.com>
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *****************************************************************************/
package org.gitective.core.filter.commit;

import java.util.Comparator;

import org.eclipse.jgit.lib.PersonIdent;

/**
 * {@link PersonIdent} comparator that compares the name and email address.
 * 
 * This class compares {@link PersonIdent#getName()	} first and if they are
 * identical it will compare {@link PersonIdent#getEmailAddress()} next.
 */
public class PersonComparator implements Comparator<PersonIdent> {

	public int compare(final PersonIdent p1, final PersonIdent p2) {
		int compare = p1.getName().compareTo(p2.getName());
		if (compare == 0)
			compare = p1.getEmailAddress().compareTo(p2.getEmailAddress());
		return compare;
	}

}
