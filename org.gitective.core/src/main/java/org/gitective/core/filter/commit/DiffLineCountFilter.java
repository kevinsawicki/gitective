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
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.filter.RevFilter;
import org.gitective.core.BlobUtils;

/**
 * Filter that tracks the cumulative amount of lines added, edited, and deleted
 */
public class DiffLineCountFilter extends CommitDiffFilter {

	private long added;

	private long edited;

	private long deleted;

	/**
	 * Create diff line count filter
	 */
	public DiffLineCountFilter() {
		super();
	}

	/**
	 * Create diff line count filter
	 *
	 * @param detectRenames
	 */
	public DiffLineCountFilter(final boolean detectRenames) {
		super(detectRenames);
	}

	/**
	 * @return added
	 */
	public long getAdded() {
		return added;
	}

	/**
	 * @return edited
	 */
	public long getEdited() {
		return edited;
	}

	/**
	 * @return deleted
	 */
	public long getDeleted() {
		return deleted;
	}

	@Override
	public boolean include(final RevCommit commit,
			final Collection<DiffEntry> diffs) {
		for (DiffEntry diff : diffs) {
			if (!isFileDiff(diff))
				continue;
			for (Edit hunk : BlobUtils.diff(repository, diff.getOldId()
					.toObjectId(), diff.getNewId().toObjectId()))
				switch (hunk.getType()) {
				case DELETE:
					deleted += hunk.getLengthA();
					break;
				case INSERT:
					added += hunk.getLengthB();
					break;
				case REPLACE:
					edited += hunk.getLengthB();
					break;
				}
		}
		return true;
	}

	@Override
	public CommitFilter reset() {
		added = 0;
		edited = 0;
		deleted = 0;
		return super.reset();
	}

	@Override
	public RevFilter clone() {
		return new DiffLineCountFilter();
	}
}
