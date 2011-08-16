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

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ShowNoteCommand;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.revwalk.filter.RevFilter;
import org.gitective.core.RepositoryUtils;

/**
 * Filter that only includes commits that have at least one note attached
 */
public class NoteFilter extends CommitFilter {

	private ShowNoteCommand show;

	private String[] noteRefs;

	public CommitFilter setRepository(final Repository repository) {
		super.setRepository(repository);
		if (repository != null) {
			show = Git.wrap(repository).notesShow();
			noteRefs = getNoteRefs(repository);
		} else {
			show = null;
			noteRefs = null;
		}
		return this;
	}

	/**
	 * Get note refs from repository to use during commit walks.
	 * 
	 * @param repository
	 *            non-null
	 * @return non-null array of ref names
	 */
	protected String[] getNoteRefs(final Repository repository) {
		return RepositoryUtils.getNoteRefs(repository);
	}

	@Override
	public boolean include(final RevWalk walker, final RevCommit commit)
			throws IOException {
		show.setObjectId(commit);
		final int refLength = noteRefs.length;
		for (int i = 0; i < refLength; i++)
			if (show.setNotesRef(noteRefs[i]).call() != null)
				return true;
		return include(false);
	}

	@Override
	public RevFilter clone() {
		return new NoteFilter();
	}
}
