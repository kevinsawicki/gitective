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

import java.util.regex.Pattern;

import org.eclipse.jgit.revwalk.filter.RevFilter;

/**
 * Commit message pattern filter that includes commits that contain a valid
 * Gerrit {@link #CHANGE_ID_REGEX}.
 */
public class ChangeIdFilter extends CommitMessageFindFilter {

	/**
	 * CHANGE_ID_REGEX
	 */
	public static final String CHANGE_ID_REGEX = "Change-Id: I[0-9a-f]{40}"; //$NON-NLS-1$

	/**
	 * Create change id filter
	 */
	public ChangeIdFilter() {
		super(CHANGE_ID_REGEX, Pattern.MULTILINE);
	}

	@Override
	public RevFilter clone() {
		return this;
	}
}
