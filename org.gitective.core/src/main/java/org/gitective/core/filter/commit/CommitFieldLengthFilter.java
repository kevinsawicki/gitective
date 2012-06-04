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

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jgit.revwalk.RevCommit;

/**
 * Base filter that tracks commits that have a field with the same length
 */
public abstract class CommitFieldLengthFilter extends CommitFilter {

	/**
	 * Commits tracked
	 */
	protected final Set<RevCommit> commits = new HashSet<RevCommit>();

	/**
	 * Field length
	 */
	protected int length = -1;

	/**
	 * Include commit with given field length
	 *
	 * @param fieldLength
	 * @param commit
	 */
	protected void include(int fieldLength, RevCommit commit) {
		if (fieldLength != length) {
			commits.clear();
			commits.add(commit);
			length = fieldLength;
		} else
			commits.add(commit);
	}

	@Override
	public CommitFilter reset() {
		commits.clear();
		length = -1;
		return super.reset();
	}
}
