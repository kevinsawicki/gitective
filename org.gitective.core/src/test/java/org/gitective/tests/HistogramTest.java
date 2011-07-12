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
import org.gitective.core.stat.CommitHistogram;
import org.gitective.core.stat.CommitHistogramFilter;
import org.gitective.core.stat.CountComparator;
import org.gitective.core.stat.EarliestComparator;
import org.gitective.core.stat.LatestComparator;
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
		assertNull(histogram.author(null));
		assertNull(histogram.committer(null));
		assertNotNull(histogram.authors());
		assertEquals(0, histogram.authors().length);
		assertNotNull(histogram.committers());
		assertEquals(0, histogram.committers().length);
	}

	/**
	 * Test empty user activity
	 */
	@Test
	public void emptyUserActivity() {
		UserCommitActivity activity = new UserCommitActivity("test", "test2");
		assertEquals("test", activity.name());
		assertEquals("test2", activity.email());
		assertEquals(0, activity.earliest());
		assertEquals(0, activity.latest());
		assertEquals(0, activity.count());
		assertNull(activity.first());
		assertNull(activity.last());
	}

	/**
	 * Test histogram with single commit
	 * 
	 * @throws Exception
	 */
	@Test
	public void singleCommit() throws Exception {
		RevCommit commit = add("test.txt", "content");
		CommitFinder finder = new CommitFinder(testRepo);
		CommitHistogramFilter filter = new CommitHistogramFilter();
		finder.setFilter(filter).find();

		CommitHistogram histogram = filter.getHistogram();
		assertNotNull(histogram);

		UserCommitActivity authorActivity = histogram.author(author
				.getEmailAddress());
		assertNotNull(authorActivity);
		assertEquals(author.getName(), authorActivity.name());
		assertEquals(author.getEmailAddress(), authorActivity.email());
		assertEquals(authorActivity, histogram.authors()[0]);
		assertEquals(1, authorActivity.count());
		assertEquals(commit, authorActivity.first());
		assertEquals(commit, authorActivity.last());
		assertEquals(commit.getAuthorIdent().getWhen().getTime(),
				authorActivity.earliest());
		assertEquals(commit.getAuthorIdent().getWhen().getTime(),
				authorActivity.latest());
		assertEquals(commit.getAuthorIdent().getWhen().getTime(),
				authorActivity.times()[0]);

		UserCommitActivity committerActivity = histogram.committer(committer
				.getEmailAddress());
		assertNotNull(committerActivity);
		assertEquals(committer.getName(), committerActivity.name());
		assertEquals(committer.getEmailAddress(), committerActivity.email());
		assertEquals(committerActivity, histogram.committers()[0]);
		assertEquals(1, committerActivity.count());
		assertEquals(commit, committerActivity.first());
		assertEquals(commit, committerActivity.last());
		assertEquals(commit.getCommitterIdent().getWhen().getTime(),
				committerActivity.earliest());
		assertEquals(commit.getCommitterIdent().getWhen().getTime(),
				committerActivity.latest());
		assertEquals(commit.getCommitterIdent().getWhen().getTime(),
				committerActivity.times()[0]);
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
		CommitHistogramFilter filter = new CommitHistogramFilter();
		finder.setFilter(filter).find();

		CommitHistogram histogram = filter.getHistogram();
		assertNotNull(histogram);

		UserCommitActivity[] authors = histogram
				.authors(new LatestComparator());
		assertEquals(commit2, authors[0].last());
		assertEquals(commit, authors[1].last());
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
		CommitHistogramFilter filter = new CommitHistogramFilter();
		finder.setFilter(filter).find();

		CommitHistogram histogram = filter.getHistogram();
		assertNotNull(histogram);

		UserCommitActivity[] authors = histogram
				.authors(new EarliestComparator());
		assertEquals(commit, authors[0].last());
		assertEquals(commit2, authors[1].last());
	}

	/**
	 * Unit test of {@link CountComparator}
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
		CommitHistogramFilter filter = new CommitHistogramFilter();
		finder.setFilter(filter).find();

		CommitHistogram histogram = filter.getHistogram();
		assertNotNull(histogram);

		UserCommitActivity[] authors = histogram.authors(new CountComparator());
		assertEquals(commit, authors[0].first());
		assertEquals(commit2, authors[1].last());
	}

	/**
	 * Unit test of {@link CommitHistogramFilter#reset()}
	 * 
	 * @throws Exception
	 */
	@Test
	public void resetFilter() throws Exception {
		add("test.txt", "content");
		CommitHistogramFilter filter = new CommitHistogramFilter();
		CommitHistogram histogram = filter.getHistogram();
		assertNotNull(histogram);
		new CommitFinder(testRepo).setFilter(filter).find();
		assertSame(histogram, filter.getHistogram());
		filter.reset();
		assertNotNull(filter.getHistogram());
		assertNotSame(histogram, filter.getHistogram());
	}

	/**
	 * Unit test of {@link CommitHistogramFilter#clone()}
	 * 
	 * @throws Exception
	 */
	@Test
	public void cloneFilter() throws Exception {
		CommitHistogramFilter filter = new CommitHistogramFilter();
		RevFilter clone = filter.clone();
		assertNotNull(clone);
		assertNotSame(filter, clone);
		assertTrue(clone instanceof CommitHistogramFilter);
		CommitHistogramFilter clonedFilter = (CommitHistogramFilter) clone;
		assertNotNull(clonedFilter.getHistogram());
		assertNotSame(filter.getHistogram(), clonedFilter.getHistogram());
	}
}
