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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.notes.Note;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.filter.RevFilter;
import org.gitective.core.GitException;
import org.gitective.core.filter.commit.AndCommitFilter;
import org.gitective.core.filter.commit.CommitCountFilter;
import org.gitective.core.filter.commit.NoteContentFilter;
import org.gitective.core.filter.commit.NoteFilter;
import org.gitective.core.service.CommitFinder;
import org.junit.Test;

/**
 * Unit tests of {@link NoteContentFilter}
 */
public class NotesTest extends GitTestCase {

	/**
	 * Test getting content of note
	 * 
	 * @throws Exception
	 */
	@Test
	public void noteContentCallback() throws Exception {
		add("test.txt", "abc");
		final String note = "this is a note";
		note(note);

		final AtomicReference<String> found = new AtomicReference<String>();

		CommitFinder finder = new CommitFinder(testRepo);
		finder.setFilter(new NoteContentFilter() {

			protected boolean include(RevCommit commit, Note note,
					String content) {
				found.set(content);
				return super.include(commit, note, content);
			}

		});
		finder.find();
		assertEquals(note, found.get());
	}

	/**
	 * Test not including a commit based on the note found
	 * 
	 * @throws Exception
	 */
	@Test
	public void noteNotIncluded() throws Exception {
		add("test.txt", "abc");
		note("this is a note");

		CommitCountFilter count = new CommitCountFilter();
		CommitFinder finder = new CommitFinder(testRepo);
		finder.setFilter(new AndCommitFilter(new NoteContentFilter() {

			protected boolean include(RevCommit commit, Note note,
					String content) {
				return false;
			}

		}, count)).find();
		assertEquals(0, count.getCount());
	}

	/**
	 * Test repository with no notes
	 * 
	 * @throws Exception
	 */
	@Test
	public void noNotesInRepository() throws Exception {
		add("test.txt", "abc");

		CommitCountFilter count = new CommitCountFilter();
		CommitFinder finder = new CommitFinder(testRepo);
		finder.setFilter(new AndCommitFilter(new NoteFilter(), count)).find();
		assertEquals(0, count.getCount());
	}

	/**
	 * Test repository with a note
	 * 
	 * @throws Exception
	 */
	@Test
	public void noteInRepository() throws Exception {
		add("test.txt", "abc");
		note("a note");
		add("test.txt", "abcd");

		CommitCountFilter count = new CommitCountFilter();
		CommitFinder finder = new CommitFinder(testRepo);
		finder.setFilter(new AndCommitFilter(new NoteFilter(), count)).find();
		assertEquals(1, count.getCount());
	}

	/**
	 * Unit test of
	 * {@link NoteContentFilter#setRepository(org.eclipse.jgit.lib.Repository)}
	 */
	@Test
	public void setNoRepository() {
		NoteFilter noteFilter = new NoteFilter();
		assertSame(noteFilter, noteFilter.setRepository(null));
		NoteContentFilter noteContentFilter = new NoteContentFilter();
		assertSame(noteContentFilter, noteContentFilter.setRepository(null));
	}

	/**
	 * Set invalid repository on notes filter
	 * 
	 * @throws Exception
	 */
	@Test
	public void setRepositoryThrowsIOException() throws Exception {
		NoteContentFilter filter = new NoteContentFilter();
		final IOException exception = new IOException("message");
		Repository repo = new BadRepository(testRepo, exception);
		try {
			filter.setRepository(repo);
			fail("Exception not thrown when reading bad refs");
		} catch (GitException e) {
			assertNotNull(e);
			assertEquals(exception, e.getCause());
		}
	}

	/**
	 * Unit test of {@link NoteFilter#clone()}
	 */
	@Test
	public void cloneFilter() {
		NoteFilter filter = new NoteFilter();
		RevFilter clone = filter.clone();
		assertNotNull(clone);
		assertNotSame(filter, clone);
		assertTrue(clone instanceof NoteFilter);
	}

	/**
	 * Unit test of {@link NoteContentFilter#clone()}
	 * 
	 * @throws Exception
	 */
	@Test
	public void cloneContentFilter() throws Exception {
		NoteContentFilter filter = new NoteContentFilter();
		RevFilter clone = filter.clone();
		assertNotNull(clone);
		assertNotSame(filter, clone);
		assertTrue(clone instanceof NoteContentFilter);
	}

	/**
	 * Test filter with multiple note refs
	 * 
	 * @throws Exception
	 */
	@Test
	public void multipleNoteRefs() throws Exception {
		add("test.txt", "abc");
		String note1 = "note1";
		String note2 = "note2";
		note(note1);
		note(note2, "commit2");

		final List<String> notes = new ArrayList<String>();

		CommitFinder finder = new CommitFinder(testRepo);
		finder.setFilter(new NoteContentFilter() {

			protected boolean include(RevCommit commit, Note note,
					String content) {
				notes.add(content);
				return super.include(commit, note, content);
			}

		});
		finder.find();
		assertEquals(2, notes.size());
		assertTrue(notes.contains(note1));
		assertTrue(notes.contains(note2));
	}
}
