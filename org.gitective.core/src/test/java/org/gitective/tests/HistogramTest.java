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

import java.util.Date;
import java.util.TimeZone;

import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.filter.RevFilter;
import org.gitective.core.service.CommitFinder;
import org.gitective.core.stat.AuthorHistogramFilter;
import org.gitective.core.stat.CommitCountComparator;
import org.gitective.core.stat.CommitHistogram;
import org.gitective.core.stat.CommitHistogramFilter;
import org.gitective.core.stat.CommitterHistogramFilter;
import org.gitective.core.stat.EarliestComparator;
import org.gitective.core.stat.FileCommitActivity;
import org.gitective.core.stat.FileHistogram;
import org.gitective.core.stat.FileHistogramFilter;
import org.gitective.core.stat.LatestComparator;
import org.gitective.core.stat.RevisionCountComparator;
import org.gitective.core.stat.UserCommitActivity;
import org.junit.Test;

/**
 * Unit tests of {@link CommitHistogramFilter}
 */
public class HistogramTest extends GitTestCase {

	/**
	 * Test empty histogram
	 */
	@Test
	public void emptyHistogram() {
		CommitHistogram histogram = new CommitHistogram();
		assertNull(histogram.getActivity(null));
		assertNotNull(histogram.getUserActivity());
		assertEquals(0, histogram.getUserActivity().length);
	}

	/**
	 * Test empty user activity
	 */
	@Test
	public void emptyUserActivity() {
		UserCommitActivity activity = new UserCommitActivity("test", "test2");
		assertEquals("test", activity.getName());
		assertEquals("test2", activity.getEmail());
		assertEquals(0, activity.getEarliest());
		assertEquals(0, activity.getLatest());
		assertEquals(0, activity.getCount());
		assertNull(activity.getFirst());
		assertNull(activity.getLast());
	}

	/**
	 * Test histogram with single commit
	 * 
	 * @throws Exception
	 */
	@Test
	public void singleAuthorCommit() throws Exception {
		RevCommit commit = add("test.txt", "content");
		CommitFinder finder = new CommitFinder(testRepo);
		CommitHistogramFilter filter = new AuthorHistogramFilter();
		finder.setFilter(filter).find();

		CommitHistogram histogram = filter.getHistogram();
		assertNotNull(histogram);

		UserCommitActivity authorActivity = histogram.getActivity(author
				.getEmailAddress());
		assertNotNull(authorActivity);
		assertEquals(author.getName(), authorActivity.getName());
		assertEquals(author.getEmailAddress(), authorActivity.getEmail());
		assertEquals(authorActivity, histogram.getUserActivity()[0]);
		assertEquals(1, authorActivity.getCount());
		assertEquals(commit, authorActivity.getFirst());
		assertEquals(commit, authorActivity.getLast());
		assertEquals(commit.getAuthorIdent().getWhen().getTime(),
				authorActivity.getEarliest());
		assertEquals(commit.getAuthorIdent().getWhen().getTime(),
				authorActivity.getLatest());
		assertEquals(commit.getAuthorIdent().getWhen().getTime(),
				authorActivity.getTimes()[0]);
	}

	/**
	 * Test histogram with single commit
	 * 
	 * @throws Exception
	 */
	@Test
	public void singleCommitterCommit() throws Exception {
		RevCommit commit = add("test.txt", "content");
		CommitFinder finder = new CommitFinder(testRepo);
		CommitHistogramFilter filter = new CommitterHistogramFilter();
		finder.setFilter(filter).find();

		CommitHistogram histogram = filter.getHistogram();
		assertNotNull(histogram);

		UserCommitActivity committerActivity = histogram.getActivity(committer
				.getEmailAddress());
		assertNotNull(committerActivity);
		assertEquals(committer.getName(), committerActivity.getName());
		assertEquals(committer.getEmailAddress(), committerActivity.getEmail());
		assertEquals(committerActivity, histogram.getUserActivity()[0]);
		assertEquals(1, committerActivity.getCount());
		assertEquals(commit, committerActivity.getFirst());
		assertEquals(commit, committerActivity.getLast());
		assertEquals(commit.getAuthorIdent().getWhen().getTime(),
				committerActivity.getEarliest());
		assertEquals(commit.getAuthorIdent().getWhen().getTime(),
				committerActivity.getLatest());
		assertEquals(commit.getAuthorIdent().getWhen().getTime(),
				committerActivity.getTimes()[0]);
	}

	/**
	 * Unit test of {@link LatestComparator}
	 * 
	 * @throws Exception
	 */
	@Test
	public void sortLatestFirst() throws Exception {
		RevCommit commit = add("test.txt", "content");
		author = new PersonIdent("abcd", "a@b.d", new Date(
				System.currentTimeMillis() + 5000), TimeZone.getDefault());
		RevCommit commit2 = add("test.txt", "content2");
		CommitFinder finder = new CommitFinder(testRepo);
		CommitHistogramFilter filter = new AuthorHistogramFilter();
		finder.setFilter(filter).find();

		CommitHistogram histogram = filter.getHistogram();
		assertNotNull(histogram);

		UserCommitActivity[] authors = histogram
				.getUserActivity(new LatestComparator());
		assertEquals(commit2, authors[0].getLast());
		assertEquals(commit, authors[1].getLast());
	}

	/**
	 * Unit test of {@link EarliestComparator}
	 * 
	 * @throws Exception
	 */
	@Test
	public void sortEarliestFirst() throws Exception {
		RevCommit commit = add("test.txt", "content");
		author = new PersonIdent("abcd", "a@b.d", new Date(
				System.currentTimeMillis() + 5000), TimeZone.getDefault());
		RevCommit commit2 = add("test.txt", "content2");
		CommitFinder finder = new CommitFinder(testRepo);
		CommitHistogramFilter filter = new AuthorHistogramFilter();
		finder.setFilter(filter).find();

		CommitHistogram histogram = filter.getHistogram();
		assertNotNull(histogram);

		UserCommitActivity[] authors = histogram
				.getUserActivity(new EarliestComparator());
		assertEquals(commit, authors[0].getLast());
		assertEquals(commit2, authors[1].getLast());
	}

	/**
	 * Unit test of {@link CommitCountComparator}
	 * 
	 * @throws Exception
	 */
	@Test
	public void sortMostFirst() throws Exception {
		RevCommit commit = add("test.txt", "content");
		author = new PersonIdent(author, new Date(
				System.currentTimeMillis() + 5000));
		add("a", "b.txt");
		author = new PersonIdent("abcd", "a@b.d", new Date(
				System.currentTimeMillis() + 5000), TimeZone.getDefault());
		RevCommit commit2 = add("test.txt", "content2");
		CommitFinder finder = new CommitFinder(testRepo);
		CommitHistogramFilter filter = new AuthorHistogramFilter();
		finder.setFilter(filter).find();

		CommitHistogram histogram = filter.getHistogram();
		assertNotNull(histogram);

		UserCommitActivity[] authors = histogram
				.getUserActivity(new CommitCountComparator());
		assertEquals(commit, authors[0].getFirst());
		assertEquals(commit2, authors[1].getLast());
	}

	/**
	 * Unit test of {@link CommitHistogramFilter#reset()}
	 * 
	 * @throws Exception
	 */
	@Test
	public void resetAuthorFilter() throws Exception {
		add("test.txt", "content");
		CommitHistogramFilter filter = new AuthorHistogramFilter();
		CommitHistogram histogram = filter.getHistogram();
		assertNotNull(histogram);
		new CommitFinder(testRepo).setFilter(filter).find();
		assertSame(histogram, filter.getHistogram());
		filter.reset();
		assertNotNull(filter.getHistogram());
		assertNotSame(histogram, filter.getHistogram());
	}

	/**
	 * Unit test of {@link FileHistogramFilter#reset()}
	 * 
	 * @throws Exception
	 */
	@Test
	public void resetFileFilter() throws Exception {
		add("test.txt", "content");
		FileHistogramFilter filter = new FileHistogramFilter();
		FileHistogram histogram = filter.getHistogram();
		assertNotNull(histogram);
		new CommitFinder(testRepo).setFilter(filter).find();
		assertSame(histogram, filter.getHistogram());
		filter.reset();
		assertNotNull(filter.getHistogram());
		assertNotSame(histogram, filter.getHistogram());
	}

	/**
	 * Unit test of {@link AuthorHistogramFilter#clone()}
	 * 
	 * @throws Exception
	 */
	@Test
	public void authorCloneFilter() throws Exception {
		CommitHistogramFilter filter = new AuthorHistogramFilter();
		RevFilter clone = filter.clone();
		assertNotNull(clone);
		assertNotSame(filter, clone);
		assertTrue(clone instanceof AuthorHistogramFilter);
		AuthorHistogramFilter clonedFilter = (AuthorHistogramFilter) clone;
		assertNotNull(clonedFilter.getHistogram());
		assertNotSame(filter.getHistogram(), clonedFilter.getHistogram());
	}

	/**
	 * Unit test of {@link CommitterHistogramFilter#clone()}
	 * 
	 * @throws Exception
	 */
	@Test
	public void committerCloneFilter() throws Exception {
		CommitHistogramFilter filter = new CommitterHistogramFilter();
		RevFilter clone = filter.clone();
		assertNotNull(clone);
		assertNotSame(filter, clone);
		assertTrue(clone instanceof CommitterHistogramFilter);
		CommitterHistogramFilter clonedFilter = (CommitterHistogramFilter) clone;
		assertNotNull(clonedFilter.getHistogram());
		assertNotSame(filter.getHistogram(), clonedFilter.getHistogram());
	}

	/**
	 * Unit test of {@link FileHistogramFilter#clone()}
	 * 
	 * @throws Exception
	 */
	@Test
	public void fileCloneFilter() throws Exception {
		FileHistogramFilter filter = new FileHistogramFilter();
		RevFilter clone = filter.clone();
		assertNotNull(clone);
		assertNotSame(filter, clone);
		assertTrue(clone instanceof FileHistogramFilter);
		FileHistogramFilter clonedFilter = (FileHistogramFilter) clone;
		assertNotNull(clonedFilter.getHistogram());
		assertNotSame(filter.getHistogram(), clonedFilter.getHistogram());
	}

	/**
	 * Test {@link FileHistogramFilter} with single commit
	 * 
	 * @throws Exception
	 */
	@Test
	public void singleCommitFileActivity() throws Exception {
		add("file.txt", "abcd");
		FileHistogramFilter filter = new FileHistogramFilter();
		CommitFinder finder = new CommitFinder(testRepo);
		finder.setFilter(filter).find();

		FileHistogram histogram = filter.getHistogram();
		assertNotNull(histogram);
		assertNull(histogram.getActivity(null));
		FileCommitActivity activity = histogram.getActivity("file.txt");
		assertNotNull(activity);
		assertEquals(1, activity.getRevisions());
		assertEquals(1, activity.getAdds());
		assertEquals(0, activity.getCopies());
		assertEquals(0, activity.getEdits());
		assertEquals(0, activity.getDeletes());
		assertEquals(0, activity.getRenames());
		assertEquals("file.txt", activity.getPath());
		assertNotNull(activity.getPreviousPaths());
		assertTrue(activity.getPreviousPaths().isEmpty());
		assertNotNull(histogram.getFileActivity());
		assertEquals(1, histogram.getFileActivity().length);
		assertEquals(activity, histogram.getFileActivity()[0]);
	}

	/**
	 * Test {@link FileHistogramFilter} with three commits
	 * 
	 * @throws Exception
	 */
	@Test
	public void addEditDeleteFileActivity() throws Exception {
		add("file.txt", "abcd");
		add("file.txt", "abce");
		delete("file.txt");

		FileHistogramFilter filter = new FileHistogramFilter();
		CommitFinder finder = new CommitFinder(testRepo);
		finder.setFilter(filter).find();

		FileHistogram histogram = filter.getHistogram();
		assertNotNull(histogram);
		FileCommitActivity activity = histogram.getActivity("file.txt");
		assertNotNull(activity);
		assertEquals(3, activity.getRevisions());
		assertEquals(1, activity.getAdds());
		assertEquals(1, activity.getEdits());
		assertEquals(1, activity.getDeletes());
		assertTrue(activity.getPreviousPaths().isEmpty());
	}

	/**
	 * Test of {@link RevisionCountComparator}
	 * 
	 * @throws Exception
	 */
	@Test
	public void sortFileActivity() throws Exception {
		// File 1 has three revisions
		add("file1.txt", "1");
		add("file1.txt", "2");
		add("file1.txt", "3");
		// File 2 has one revision
		add("file2.txt", "1");
		// File 3 has 2 revsions
		add("file3.txt", "1");
		add("file3.txt", "2");

		FileHistogramFilter filter = new FileHistogramFilter();
		CommitFinder finder = new CommitFinder(testRepo);
		finder.setFilter(filter).find();
		FileHistogram histogram = filter.getHistogram();
		assertNotNull(histogram);
		FileCommitActivity[] files = histogram
				.getFileActivity(new RevisionCountComparator());
		assertNotNull(files);
		assertEquals(3, files.length);
		assertEquals("file1.txt", files[0].getPath());
		assertEquals("file3.txt", files[1].getPath());
		assertEquals("file2.txt", files[2].getPath());
	}
}
