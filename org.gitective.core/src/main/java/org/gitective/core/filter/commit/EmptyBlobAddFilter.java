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

import static org.eclipse.jgit.lib.Constants.EMPTY_BLOB_ID;

import java.io.IOException;
import java.util.Collection;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.AbbreviatedObjectId;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.filter.RevFilter;

/**
 * Filter that includes commits that add an empty blob.
 * <p>
 * The commits included will either add a new file that is empty or edit an
 * existing file that was previously not empty but edited to be empty in the
 * given commit.
 */
public class EmptyBlobAddFilter extends CommitDiffFilter {

	/**
	 * Create empty blob add filter
	 */
	public EmptyBlobAddFilter() {
		super();
	}

	/**
	 * Create empty blob add filter
	 *
	 * @param detectRenames
	 */
	public EmptyBlobAddFilter(final boolean detectRenames) {
		super(detectRenames);
	}

	@Override
	public boolean include(final RevCommit commit,
			final Collection<DiffEntry> diffs) throws IOException {
		for (DiffEntry diff : diffs) {
			final AbbreviatedObjectId oldId = diff.getOldId();
			if (oldId == null)
				continue;
			if (!EMPTY_BLOB_ID.equals(oldId.toObjectId())
					&& EMPTY_BLOB_ID.equals(diff.getNewId().toObjectId()))
				return true;
		}
		return false;
	}

	@Override
	public RevFilter clone() {
		return new EmptyBlobAddFilter(detectRenames);
	}
}
