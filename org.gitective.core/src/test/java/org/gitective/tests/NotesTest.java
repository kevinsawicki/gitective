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
package org.gitective.tests;

import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.jgit.notes.Note;
import org.eclipse.jgit.revwalk.RevCommit;
import org.gitective.core.filter.commit.CommitNotesFilter;
import org.gitective.core.service.CommitFinder;

/**
 * Unit tests of {@link CommitNotesFilter}
 */
public class NotesTest extends GitTestCase {

	/**
	 * Test getting content of note
	 * 
	 * @throws Exception
	 */
	public void testNoteContentCallback() throws Exception {
		add("test.txt", "abc");
		final String note = "this is a note";
		note(note);

		final AtomicReference<String> found = new AtomicReference<String>();

		CommitFinder finder = new CommitFinder(testRepo);
		finder.setFilter(new CommitNotesFilter() {

			protected boolean include(RevCommit commit, Note note,
					String content) {
				found.set(content);
				return super.include(commit, note, content);
			}

		});
		finder.find();
		assertEquals(note, found.get());
	}

}
