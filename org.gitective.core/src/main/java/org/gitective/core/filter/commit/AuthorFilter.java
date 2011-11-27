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

import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.revwalk.filter.RevFilter;

/**
 * Person filter that includes commits where a {@link PersonIdent} matches the
 * name and e-mail address of a commit's author.
 */
public class AuthorFilter extends PersonFilter {

	/**
	 * Create an author filter for the given person
	 *
	 * @param person
	 */
	public AuthorFilter(final PersonIdent person) {
		super(person);
	}

	/**
	 * Create an author filter for the given name and e-mail address
	 *
	 * @param name
	 * @param email
	 */
	public AuthorFilter(final String name, final String email) {
		super(name, email);
	}

	@Override
	public boolean include(final RevWalk walker, final RevCommit commit)
			throws IOException {
		return match(commit.getAuthorIdent()) ? true : include(false);
	}

	@Override
	public RevFilter clone() {
		return new AuthorFilter(person);
	}
}
