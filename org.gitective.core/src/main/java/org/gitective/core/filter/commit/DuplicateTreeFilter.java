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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jgit.lib.MutableObjectId;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.revwalk.filter.RevFilter;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.gitective.core.TreeUtils;

/**
 * Filter that tracks any duplicate trees introduced in a visited commit.
 */
public class DuplicateTreeFilter extends CommitFilter {

	private final Map<RevCommit, DuplicateContainer> duplicates = new HashMap<RevCommit, DuplicateContainer>();

	public boolean include(final RevWalk walker, final RevCommit commit)
			throws IOException {
		final TreeWalk walk = TreeUtils.diffWithParents(walker, commit);
		final MutableObjectId id = new MutableObjectId();
		final ObjectId zero = ObjectId.zeroId();
		final DuplicateContainer dupes = new DuplicateContainer(commit);
		while (walk.next()) {
			if (!walk.isSubtree())
				continue;
			final String path = walk.getPathString();
			for (int i = 0; i < walk.getTreeCount(); i++) {
				walk.getObjectId(id, i);
				if (!zero.equals(id))
					dupes.include(id.toObjectId(), path);
			}
			walk.enterSubtree();
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
		return new DuplicateTreeFilter();
	}
}
