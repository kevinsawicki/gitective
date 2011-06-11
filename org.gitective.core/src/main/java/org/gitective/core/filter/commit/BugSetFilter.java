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
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.revwalk.filter.RevFilter;

/**
 * Filter that collects all bugs referenced in commit messages.
 */
public class BugSetFilter extends CommitMessageFindFilter {

	private final Set<String> bugs = new HashSet<String>();

	/**
	 * Create new bug set filter
	 */
	public BugSetFilter() {
		super(BugFilter.BUG_REGEX, Pattern.MULTILINE);
	}

	/**
	 * Get bugs collected
	 * 
	 * @return non-null but possibly empty set of bug ids
	 */
	public Set<String> getBugs() {
		return bugs;
	}

	@Override
	public boolean include(final RevWalk walker, final RevCommit commit)
			throws IOException {
		matcher.reset(getText(commit));
		while (matcher.find())
			bugs.add(matcher.group(1));
		return true;
	}

	@Override
	public RevFilter clone() {
		return new BugSetFilter();
	}

	@Override
	public CommitFilter reset() {
		bugs.clear();
		return super.reset();
	}
}
