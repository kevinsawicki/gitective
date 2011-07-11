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
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

/**
 * Commit calendar class
 */
public class CommitCalendar implements Serializable {

	/** serialVersionUID */
	private static final long serialVersionUID = -3909996432477594787L;

	private final Map<Integer, YearCommitActivity> years;

	/**
	 * Create calendar from user activity
	 * 
	 * @param activity
	 */
	public CommitCalendar(final UserCommitActivity[] activity) {
		years = new TreeMap<Integer, YearCommitActivity>();
		final GregorianCalendar calendar = new GregorianCalendar(Locale.US);
		final int length = activity.length;
		for (int i = 0; i < length; i++)
			for (long time : activity[i].times()) {
				calendar.setTimeInMillis(time);
				final int year = calendar.get(Calendar.YEAR);
				YearCommitActivity yearly = years.get(year);
				if (yearly == null) {
					yearly = new YearCommitActivity(year);
					years.put(year, yearly);
				}
				yearly.add(calendar);
			}
	}

	/**
	 * Get yearly activity
	 * 
	 * @return non-null but possibly empty array
	 */
	public YearCommitActivity[] years() {
		return years.values().toArray(new YearCommitActivity[years.size()]);
	}

	/**
	 * Get commit counts for each month
	 * 
	 * @return monthly counts
	 */
	public int[] months() {
		final int[] months = new int[YearCommitActivity.MONTHS];
		for (YearCommitActivity year : years.values())
			year.months(months);
		return months;
	}

	/**
	 * Get commit counts for each day of the month
	 * 
	 * @return day of month counts
	 */
	public int[] days() {
		final int[] days = new int[YearCommitActivity.DAYS];
		for (YearCommitActivity year : years.values())
			year.days(days);
		return days;
	}

	/**
	 * Get commit counts for each hour of the day
	 * 
	 * @return hourly counts
	 */
	public int[] hours() {
		final int[] hours = new int[YearCommitActivity.HOURS];
		for (YearCommitActivity year : years.values())
			year.hours(hours);
		return hours;
	}

	/**
	 * Get number of commits in given month
	 * 
	 * @param month
	 * @return monthly count
	 */
	public int countMonth(final int month) {
		int total = 0;
		for (YearCommitActivity year : years.values())
			total += year.monthCount(month);
		return total;
	}

	/**
	 * Get number of commits in given hour
	 * 
	 * @param hour
	 * @return hourly count
	 */
	public int countHour(final int hour) {
		int total = 0;
		for (YearCommitActivity year : years.values())
			total += year.hourCount(hour);
		return total;
	}

	/**
	 * Get number of commits in given day of month
	 * 
	 * @param dayOfMonth
	 * @return day of month count
	 */
	public int countDay(final int dayOfMonth) {
		int total = 0;
		for (YearCommitActivity year : years.values())
			total += year.dayCount(dayOfMonth);
		return total;
	}
}
