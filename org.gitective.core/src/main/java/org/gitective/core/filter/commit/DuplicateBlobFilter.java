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
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.filter.RevFilter;

/**
 * Filter that tracks commits where files with the same content were modified.
 * <p>
 * This filter traverses all the diff entries in each commit visited and tracks
 * any instances where the different paths reference the same blob object id.
 */
public class DuplicateBlobFilter extends CommitDiffFilter {

	private final Map<RevCommit, DuplicateContainer> duplicates = new LinkedHashMap<RevCommit, DuplicateContainer>();

	/**
	 * Create duplicate blob filter
	 */
	public DuplicateBlobFilter() {
		super();
	}

	/**
	 * Create duplicate blob filter
	 *
	 * @param detectRenames
	 */
	public DuplicateBlobFilter(final boolean detectRenames) {
		super(detectRenames);
	}

	@Override
	public boolean include(final RevCommit commit,
			final Collection<DiffEntry> diffs) {
		final DuplicateContainer dupes = new DuplicateContainer(commit);
		for (DiffEntry diff : diffs) {
			switch (diff.getChangeType()) {
			case DELETE:
				continue;
			case COPY:
			case MODIFY:
			case RENAME:
				if (diff.getOldMode() != diff.getNewMode()
						&& diff.getOldId().equals(diff.getNewId()))
					continue;
			default:
				dupes.include(diff.getNewId().toObjectId(), diff.getNewPath());
				break;
			}
		}
		if (dupes.validate())
			duplicates.put(commit, dupes);
		return true;
	}

	/**
	 * Get the duplicates
	 *
	 * @return non-null but possibly empty map
	 */
	public Map<RevCommit, DuplicateContainer> getDuplicates() {
		return duplicates;
	}

	/**
	 * Were any duplicate blobs detected for all commits visited?
	 *
	 * @return true if duplicates exist, false otherwise
	 */
	public boolean hasDuplicates() {
		return !duplicates.isEmpty();
	}

	@Override
	public CommitFilter reset() {
		duplicates.clear();
		return super.reset();
	}

	@Override
	public RevFilter clone() {
		return new DuplicateBlobFilter(detectRenames);
	}
}
