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

import org.eclipse.jgit.errors.StopWalkException;
import org.eclipse.jgit.revwalk.filter.RevFilter;

/**
 * Base commit filter class with utility methods to be used by sub-classes.
 */
public abstract class CommitFilter extends RevFilter implements Cloneable {

	private boolean stop;

	/**
	 * Set whether the search should be stopped when a commit visited is not
	 * included.
	 * 
	 * @param stop
	 * @return this filter
	 */
	public CommitFilter setStop(boolean stop) {
		this.stop = stop;
		return this;
	}

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

	/**
	 * Return the include value given unless include is false and this filter is
	 * configured to stop the search when a commit is not included.
	 * 
	 * @param include
	 * @return include parameter value
	 */
	protected boolean include(boolean include) {
		if (!include && stop)
			throw StopWalkException.INSTANCE;
		return include;
	}
}
