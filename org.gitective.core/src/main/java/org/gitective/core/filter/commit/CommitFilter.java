/******************************************************************************
 *  Copyright (c) 2011 Kevin Sawicki <kevinsawicki@gmail.com>
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *****************************************************************************/
package org.gitective.core.filter.commit;

import org.eclipse.jgit.revwalk.filter.RevFilter;

/**
 * Base commit filter class with utility methods to be used by sub-classes.
 */
public abstract class CommitFilter extends RevFilter {

	/**
	 * Check if the two objects specified are either both null or both equal
	 * according the {@link Object#equals(Object)} method of object1.
	 * 
	 * @param object1
	 * @param object2
	 * @return true if equal, false otherwise
	 */
	protected boolean equals(Object object1, Object object2) {
		return equalsNull(object1, object2) || equalsNonNull(object1, object2);
	}

	/**
	 * Check if the two objects specified are both null.
	 * 
	 * @param object1
	 * @param object2
	 * @return true if both are null, false otherwise
	 */
	protected boolean equalsNull(Object object1, Object object2) {
		return object1 == null && object2 == null;
	}

	/**
	 * Check if the two objects specified are both non-null and equal according
	 * to the {@link Object#equals(Object)} method of object1.
	 * 
	 * @param object1
	 * @param object2
	 * @return true if non-null and equal, false otherwise
	 */
	protected boolean equalsNonNull(Object object1, Object object2) {
		return object1 != null && object2 != null && object1.equals(object2);
	}

	/**
	 * Resets the filter state
	 * 
	 * @return this filter
	 */
	public CommitFilter reset() {
		// Does nothing by default, sub-classes should override
		return this;
	}
}
