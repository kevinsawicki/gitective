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
package org.gitective.core.stat;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffEntry.ChangeType;
import org.eclipse.jgit.revwalk.RevCommit;

/**
 * File histogram storing the number of revisions of each file in a repository
 */
public class FileHistogram implements Serializable {

	/** serialVersionUID */
	private static final long serialVersionUID = -3756081515294090375L;

	private final Map<String, FileCommitActivity> files = new HashMap<String, FileCommitActivity>();

	/**
	 * Get file activity for given path.
	 * 
	 * @param path
	 * @return activity or null if none for path
	 */
	public FileCommitActivity getActivity(final String path) {
		return path != null ? files.get(path) : null;
	}

	/**
	 * Get all file activity
	 * 
	 * @param comparator
	 * @return non-null but possibly empty array
	 */
	public FileCommitActivity[] getFileActivity(
			final Comparator<FileCommitActivity> comparator) {
		final FileCommitActivity[] activity = files.values().toArray(
				new FileCommitActivity[files.size()]);
		if (comparator != null)
			Arrays.sort(activity, comparator);
		return activity;
	}

	/**
	 * Get all file activity
	 * 
	 * @see #getFileActivity(Comparator)
	 * @return non-null but possibly empty array
	 */
	public FileCommitActivity[] getFileActivity() {
		return getFileActivity(null);
	}

	/**
	 * Include diff entry introduced by given commit in the histogram
	 * 
	 * @param commit
	 * @param entry
	 * @return this histogram
	 */
	public FileHistogram include(final RevCommit commit, final DiffEntry entry) {
		final String path;
		if (entry.getChangeType() != ChangeType.DELETE)
			path = entry.getNewPath();
		else
			path = entry.getOldPath();
		FileCommitActivity activity = files.get(path);
		if (activity == null) {
			activity = new FileCommitActivity(path);
			files.put(path, activity);
		}
		activity.include(commit, entry);
		return this;
	}
}
