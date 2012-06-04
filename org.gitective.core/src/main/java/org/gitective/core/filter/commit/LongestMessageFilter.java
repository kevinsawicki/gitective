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
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

/**
 * Filter to track the commit(s) with the longest message
 */
public class LongestMessageFilter extends CommitFilter {

	private final Set<RevCommit> commits = new HashSet<RevCommit>();

	private int length = -1;

	@Override
	public boolean include(final RevWalk walker, final RevCommit commit)
			throws IOException {
		final String message = commit.getFullMessage();
		final int messageLength = message.length();
		if (messageLength < length)
			return true;

		if (messageLength != length) {
			commits.clear();
			commits.add(commit);
			length = messageLength;
		} else
			commits.add(commit);

		return true;
	}

	/**
	 * Get the commits with the longest message length
	 *
	 * @return non-null but possibly empty set of commits
	 */
	public Set<RevCommit> getCommits() {
		return commits;
	}

	/**
	 * Get the length of the longest commit message visited
	 *
	 * @return length or -1 if no commits visited
	 */
	public int getLength() {
		return length;
	}

	@Override
	public CommitFilter reset() {
		commits.clear();
		length = -1;
		return super.reset();
	}

	@Override
	public LongestMessageFilter clone() {
		return new LongestMessageFilter();
	}
}
