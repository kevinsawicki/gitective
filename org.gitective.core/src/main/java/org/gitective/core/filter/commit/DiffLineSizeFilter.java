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

import java.util.Collection;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.lib.AbbreviatedObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.revwalk.filter.RevFilter;
import org.gitective.core.BlobUtils;

/**
 * Filter for including commits that introduced a configurable number of line
 * differences
 */
public class DiffLineSizeFilter extends CommitDiffEditFilter {

	private final int total;

	private int count;

	/**
	 * Create a filter that includes commits that introduced a minimum number of
	 * line differences
	 *
	 * @param detectRenames
	 * @param diffTotal
	 */
	public DiffLineSizeFilter(final boolean detectRenames, final int diffTotal) {
		super(detectRenames);
		total = diffTotal;
	}

	/**
	 * Create a filter that includes commits that introduced a minimum number of
	 * line differences
	 *
	 * @param diffTotal
	 */
	public DiffLineSizeFilter(final int diffTotal) {
		this(false, diffTotal);
	}

	/**
	 * Get the configured difference total
	 *
	 * @return total
	 */
	public int getTotal() {
		return total;
	}

	@Override
	public boolean include(RevWalk walker, RevCommit commit,
			Collection<DiffEntry> diffs) {
		count = 0;
		final ObjectReader reader = walker.getObjectReader();
		for (DiffEntry diff : diffs) {
			if (!isFileDiff(diff))
				continue;
			final AbbreviatedObjectId oldId = diff.getOldId();
			if (oldId == null)
				continue;
			include(commit, diff, BlobUtils.diff(reader, oldId.toObjectId(),
					diff.getNewId().toObjectId()));
			if (count >= total)
				break;
		}
		return count >= total ? true : include(false);
	}

	@Override
	protected boolean include(final RevCommit commit, final DiffEntry diff,
			final Collection<Edit> edits) {
		for (Edit edit : edits) {
			include(commit, diff, edit);
			if (count >= total)
				break;
		}
		return true;
	}

	@Override
	protected boolean include(final RevCommit commit, final DiffEntry diff,
			final Edit edit) {
		switch (edit.getType()) {
		case DELETE:
			count += edit.getLengthA();
			break;
		case INSERT:
		case REPLACE:
			count += edit.getLengthB();
			break;
		}
		return true;
	}

	@Override
	public RevFilter clone() {
		return new DiffLineSizeFilter(detectRenames, total);
	}
}
