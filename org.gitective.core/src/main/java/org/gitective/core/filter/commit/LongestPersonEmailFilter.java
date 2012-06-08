/*
 * Copyright (c) 2012 Kevin Sawicki <kevinsawicki@gmail.com>
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

import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

/**
 * Base filter to track the commit(s) with the longest email address for a
 * {@link PersonIdent} associated with each commit
 */
public abstract class LongestPersonEmailFilter extends CommitFieldLengthFilter {

	@Override
	public boolean include(final RevWalk walker, final RevCommit commit)
			throws IOException {
		final PersonIdent person = getPerson(walker, commit);
		if (person == null)
			return true;
		final String email = person.getEmailAddress();
		final int emailLength = email != null ? email.length() : 0;
		if (emailLength >= length)
			include(emailLength, commit);
		return true;
	}

	/**
	 * Get person from commit
	 *
	 * @param walker
	 * @param commit
	 * @return person
	 */
	protected abstract PersonIdent getPerson(final RevWalk walker,
			final RevCommit commit);
}
