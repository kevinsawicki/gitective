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
import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.revwalk.RevCommit;

/**
 * Activity container for file revisions
 */
public class FileCommitActivity implements Serializable {

	/** serialVersionUID */
	private static final long serialVersionUID = -6252001423443439822L;

	private int adds;
	private int copies;
	private int deletes;
	private int edits;
	private int renames;

	private final String path;
	private final Set<String> previousPaths = new LinkedHashSet<String>(4);

	/**
	 * Create file activity for file at given path
	 *
	 * @param path
	 */
	public FileCommitActivity(final String path) {
		this.path = path;
	}

	/**
	 * Get current path of this file
	 *
	 * @return path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * Get previous paths this file was known as
	 *
	 * @return non-null but possibly empty ordered set
	 */
	public Set<String> getPreviousPaths() {
		return previousPaths;
	}

	/**
	 * Get total number of revisions for this file
	 *
	 * @return total revisions
	 */
	public int getRevisions() {
		return deletes + adds + edits + renames + copies;
	}

	/**
	 * Get number of adds
	 *
	 * @return adds
	 */
	public int getAdds() {
		return adds;
	}

	/**
	 * Get number of copies
	 *
	 * @return copies
	 */
	public int getCopies() {
		return copies;
	}

	/**
	 * Get number of deletes
	 *
	 * @return deletes
	 */
	public int getDeletes() {
		return deletes;
	}

	/**
	 * Get number of edits
	 *
	 * @return edits
	 */
	public int getEdits() {
		return edits;
	}

	/**
	 * Get number of renames
	 *
	 * @return renames
	 */
	public int getRenames() {
		return renames;
	}

	/**
	 * Include diff introduced by given commit
	 *
	 * @param commit
	 * @param entry
	 * @return this activity
	 */
	public FileCommitActivity include(final RevCommit commit,
			final DiffEntry entry) {
		switch (entry.getChangeType()) {
		case ADD:
			adds++;
			break;
		case MODIFY:
			edits++;
			break;
		case DELETE:
			deletes++;
			break;
		case RENAME:
			renames++;
			previousPaths.add(entry.getOldPath());
			break;
		case COPY:
			copies++;
			break;
		}
		return this;
	}
}
