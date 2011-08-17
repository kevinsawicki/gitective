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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffEntry.ChangeType;
import org.eclipse.jgit.lib.AbbreviatedObjectId;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.filter.RevFilter;

/**
 * Filter that tracks commits where files with the same content were modified.
 * This filter traverses all the diff entries in each commit visited and tracks
 * any instances where the different paths reference the same blob object id.
 */
public class DuplicateBlobFilter extends CommitDiffFilter {

	/**
	 * Container class for duplicate blobs
	 */
	public static class DuplicateContainer implements
			Iterable<Entry<AbbreviatedObjectId, List<String>>> {

		private final RevCommit commit;

		private final Map<AbbreviatedObjectId, List<String>> blobs;

		private DuplicateContainer(final RevCommit commit) {
			this.commit = commit;
			blobs = new HashMap<AbbreviatedObjectId, List<String>>();
		}

		/**
		 * Include id and path as possible duplicate
		 *
		 * @param id
		 * @param path
		 * @return this container
		 */
		private DuplicateContainer include(final AbbreviatedObjectId id,
				final String path) {
			List<String> paths = blobs.get(id);
			if (paths == null) {
				paths = new ArrayList<String>(2);
				blobs.put(id, paths);
			}
			paths.add(path);
			return this;
		}

		/**
		 * Validate that this container contains at least one duplicate blob
		 *
		 * @return true if duplicates exist, false otherwise
		 */
		private boolean validate() {
			final Iterator<Entry<AbbreviatedObjectId, List<String>>> entries = iterator();
			while (entries.hasNext())
				if (entries.next().getValue().size() < 2)
					entries.remove();
			return !blobs.isEmpty();
		}

		/**
		 * Get commits that these duplicates occurred in
		 *
		 * @return non-null commit
		 */
		public RevCommit getCommit() {
			return commit;
		}

		/**
		 * Get duplicate blobs ids mapped paths
		 *
		 * @return non-null and non-empty map of duplicates
		 */
		public Map<AbbreviatedObjectId, List<String>> getDuplicates() {
			return blobs;
		}

		/**
		 * Get iterator over all duplicate blobs
		 *
		 * @return iterator over blobs and paths
		 */
		public Iterator<Entry<AbbreviatedObjectId, List<String>>> iterator() {
			return blobs.entrySet().iterator();
		}
	}

	private final Map<RevCommit, DuplicateContainer> duplicates = new HashMap<RevCommit, DuplicateContainer>();

	@Override
	protected boolean include(final RevCommit commit,
			final Collection<DiffEntry> diffs) {
		final DuplicateContainer dupes = new DuplicateContainer(commit);
		for (DiffEntry diff : diffs) {
			if (diff.getChangeType() == ChangeType.DELETE)
				continue;
			dupes.include(diff.getNewId(), diff.getNewPath());
		}
		if (dupes.validate())
			duplicates.put(commit, dupes);
		return true;
	}

	/**
	 * Get duplicates
	 *
	 * @return non-null but possibly empty map
	 */
	public Map<RevCommit, DuplicateContainer> getDuplicates() {
		return duplicates;
	}

	@Override
	public CommitFilter reset() {
		duplicates.clear();
		return super.reset();
	}

	@Override
	public RevFilter clone() {
		return new DuplicateBlobFilter();
	}
}
