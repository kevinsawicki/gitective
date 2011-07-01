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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.gitective.core.Assert;

/**
 * Base commit filter class that includes commits where a configured pattern can
 * be found.
 */
public abstract class PatternFindCommitFilter extends CommitFilter {

	/**
	 * Pattern
	 */
	protected final String pattern;

	/**
	 * Flags
	 */
	protected final int flags;

	/**
	 * Pattern matcher
	 */
	protected final Matcher matcher;

	/**
	 * Create a pattern find commit filter
	 * 
	 * @param pattern
	 */
	public PatternFindCommitFilter(final String pattern) {
		this(pattern, 0);
	}

	/**
	 * Create a pattern find commit filter
	 * 
	 * @param pattern
	 * @param flags
	 */
	public PatternFindCommitFilter(final String pattern, final int flags) {
		Assert.notNull("Pattern cannot be null", pattern);
		Assert.notEmpty("Pattern cannot be empty", pattern);
		this.pattern = pattern;
		this.flags = flags;
		matcher = Pattern.compile(pattern, flags).matcher("");
	}

	@Override
	public boolean include(final RevWalk walker, final RevCommit commit)
			throws IOException {
		return include(matcher.reset(getText(commit)).find());
	}

	/**
	 * Get the text from the commit to find the pattern in
	 * 
	 * @param commit
	 * @return text
	 */
	protected abstract CharSequence getText(RevCommit commit);

}
