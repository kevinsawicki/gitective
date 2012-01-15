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
import org.eclipse.jgit.revwalk.filter.RevFilter;
import org.gitective.core.Check;

/**
 * Filter that includes commits where the committer name/e-mail address is
 * different than the author name/e-mail address.
 */
public class CommitterDiffFilter extends CommitFilter {

	@Override
	public boolean include(final RevWalk walker, final RevCommit commit)
			throws IOException {
		final PersonIdent author = commit.getAuthorIdent();
		final PersonIdent committer = commit.getCommitterIdent();
		if (author != null && committer != null)
			return isSamePerson(author, committer) ? include(false) : true;
		if (author == null && committer == null)
			return include(false);
		return true;
	}

	/**
	 * Are the two identities the same?
	 *
	 * @param author
	 * @param committer
	 * @return true is same, false otherwise
	 */
	protected boolean isSamePerson(final PersonIdent author,
			final PersonIdent committer) {
		return Check.equals(author.getName(), committer.getName())
				&& Check.equals(author.getEmailAddress(),
						committer.getEmailAddress());
	}

	@Override
	public RevFilter clone() {
		return new CommitterDiffFilter();
	}
}
