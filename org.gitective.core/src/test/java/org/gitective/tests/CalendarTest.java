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

import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevCommit;
import org.gitective.core.service.CommitFinder;
import org.gitective.core.stat.AuthorHistogramFilter;
import org.gitective.core.stat.CommitCalendar;
import org.gitective.core.stat.CommitHistogramFilter;
import org.gitective.core.stat.Month;
import org.gitective.core.stat.UserCommitActivity;
import org.gitective.core.stat.YearCommitActivity;
import org.junit.Test;

/**
 * Unit tests of {@link CommitCalendar}
 */
public class CalendarTest extends GitTestCase {

	/**
	 * Test an empty calendar
	 */
	@Test
	public void emptyCalendar() {
		CommitCalendar calendar = new CommitCalendar(new UserCommitActivity[0]);

		assertEquals(0, calendar.getDayCount(0));
		assertEquals(0, calendar.getHourCount(0));
		assertEquals(0, calendar.getMonthCount(0));

		assertNotNull(calendar.years());
		assertEquals(0, calendar.years().length);

		assertNotNull(calendar.getDays());
		assertEquals(YearCommitActivity.DAYS, calendar.getDays().length);
		for (int day : calendar.getDays())
			assertEquals(0, day);

		assertNotNull(calendar.getMonths());
		assertEquals(YearCommitActivity.MONTHS, calendar.getMonths().length);
		for (int month : calendar.getMonths())
			assertEquals(0, month);

		assertNotNull(calendar.getHours());
		assertEquals(YearCommitActivity.HOURS, calendar.getHours().length);
		for (int hour : calendar.getHours())
			assertEquals(0, hour);
	}

	/**
	 * Unit test of empty {@link YearCommitActivity}
	 */
	@Test
	public void emptyYear() {
		YearCommitActivity year = new YearCommitActivity(2000);
		assertEquals(0, year.getCount());
		for (int i = 0; i < YearCommitActivity.MONTHS; i++)
			assertEquals(0, year.getMonthCount(i));
		for (int i = 0; i < YearCommitActivity.DAYS; i++)
			assertEquals(0, year.getDayCount(i));
		for (int i = 0; i < YearCommitActivity.HOURS; i++)
			assertEquals(0, year.getHourCount(i));
	}

	/**
	 * Test proper resize of internal arrays of {@link UserCommitActivity}
	 * 
	 * @throws Exception
	 */
	@Test
	public void resizeBySizeAndGrowth() throws Exception {
		RevCommit commit = add("test.txt", "content");
		UserCommitActivity user = new UserCommitActivity("a", "b");
		final int size = (UserCommitActivity.SIZE * UserCommitActivity.GROWTH) + 2;
		for (int i = 0; i < size; i++)
			user.include(commit, author);
		assertEquals(size, user.getCount());
		byte[] commitId = new byte[Constants.OBJECT_ID_LENGTH];
		commit.copyRawTo(commitId, 0);
		for (byte[] id : user.getRawIds())
			assertTrue(Arrays.equals(commitId, id));
		for (ObjectId id : user.getIds())
			assertEquals(commit, id);
	}

	/**
	 * Test calendar with single commit
	 * 
	 * @throws Exception
	 */
	@Test
	public void singleCommit() throws Exception {
		RevCommit commit = add("test", "test");

		CommitHistogramFilter filter = new AuthorHistogramFilter();
		CommitFinder finder = new CommitFinder(testRepo);
		finder.setFilter(filter).find();

		CommitCalendar cal = new CommitCalendar(filter.getHistogram().getUserActivity());

		GregorianCalendar commitTime = new GregorianCalendar();
		int month = commitTime.get(Calendar.MONTH);
		int day = commitTime.get(Calendar.DAY_OF_MONTH) - 1;
		int hour = commitTime.get(Calendar.HOUR_OF_DAY);
		commitTime.setTime(commit.getAuthorIdent().getWhen());
		assertEquals(1, cal.getDayCount(day));
		assertEquals(1, cal.getMonthCount(month));
		assertEquals(1, cal.getHourCount(hour));

		assertEquals(1, cal.getDays()[day]);
		assertEquals(1, cal.getMonths()[month]);
		assertEquals(1, cal.getHours()[hour]);

		assertEquals(1, cal.years().length);
		YearCommitActivity year = cal.years()[0];
		assertEquals(1, year.getCount());
		assertEquals(commitTime.get(Calendar.YEAR), year.getYear());
		assertNotNull(year.getMonths());
		assertEquals(1, year.getMonths()[month]);
		assertEquals(1, year.getMonthCount(Month.month(month)));
		assertNotNull(year.getDays());
		assertEquals(1, year.getDays()[day]);
		assertNotNull(year.getHours());
		assertEquals(1, year.getHours()[hour]);
	}
}
