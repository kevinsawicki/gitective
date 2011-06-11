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
