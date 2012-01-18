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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jgit.lib.AnyObjectId;
import org.eclipse.jgit.revwalk.RevCommit;

/**
 * Container class for duplicates found in commit walks
 */
public class DuplicateContainer implements
		Iterable<Entry<AnyObjectId, List<String>>> {

	/**
	 * Commit
	 */
	protected final RevCommit commit;

	/**
	 * Duplicate objects
	 */
	protected final Map<AnyObjectId, List<String>> objects;

	/**
	 * Create duplicate container
	 *
	 * @param commit
	 */
	protected DuplicateContainer(final RevCommit commit) {
		this.commit = commit;
		objects = new HashMap<AnyObjectId, List<String>>();
	}

	/**
	 * Include id and path as possible duplicate
	 *
	 * @param id
	 * @param path
	 * @return this container
	 */
	protected DuplicateContainer include(final AnyObjectId id, final String path) {
		List<String> paths = objects.get(id);
		if (paths == null) {
			paths = new ArrayList<String>(2);
			objects.put(id, paths);
		}
		paths.add(path);
		return this;
	}

	/**
	 * Validate that this container contains at least one duplicate blob
	 *
	 * @return true if duplicates exist, false otherwise
	 */
	protected boolean validate() {
		final Iterator<Entry<AnyObjectId, List<String>>> entries = iterator();
		while (entries.hasNext())
			if (entries.next().getValue().size() < 2)
				entries.remove();
		return !objects.isEmpty();
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
	public Map<AnyObjectId, List<String>> getDuplicates() {
		return objects;
	}

	/**
	 * Get iterator over all duplicate blobs
	 *
	 * @return iterator over blobs and paths
	 */
	public Iterator<Entry<AnyObjectId, List<String>>> iterator() {
		return objects.entrySet().iterator();
	}

	/**
	 * Get total number of duplicates
	 *
	 * @return total
	 */
	public int getTotal() {
		int total = 0;
		for (List<String> dupe : objects.values())
			total += dupe.size();
		return total;
	}
}