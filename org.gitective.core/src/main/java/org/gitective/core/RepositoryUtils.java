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
package org.gitective.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;

/**
 * Repository utilities
 */
public abstract class RepositoryUtils {

	/**
	 * Get note references
	 * 
	 * @param repository
	 * @return non-null but possibly empty array of note references
	 */
	public static String[] getNoteRefs(final Repository repository) {
		if (repository == null)
			throw new IllegalArgumentException(
					Assert.formatNotNull("Repository"));
		Collection<Ref> refs;
		try {
			refs = repository.getRefDatabase().getRefs(Constants.R_NOTES)
					.values();
		} catch (IOException e) {
			throw new GitException(e);
		}
		final List<String> notes = new ArrayList<String>(refs.size() + 1);
		notes.add(Constants.R_NOTES_COMMITS);
		for (Ref ref : refs) {
			final String name = ref.getName();
			if (!Constants.R_NOTES_COMMITS.equals(name))
				notes.add(name);
		}
		return notes.toArray(new String[notes.size()]);
	}
}
