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

import java.util.Calendar;

/**
 * Commit activity in a given year
 */
public class YearCommitActivity {

	/**
	 * Number of months in a year
	 */
	public static final int MONTHS = 12;

	/**
	 * Number of hours in a day
	 */
	public static final int HOURS = 24;

	/**
	 * Maximum number of days in a month
	 */
	public static final int DAYS = 31;

	private final int year;

	private final int[][][] commits;

	private int total;

	/**
	 * Create activity for given year
	 *
	 * @param year
	 */
	public YearCommitActivity(final int year) {
		this.year = year;
		commits = new int[MONTHS][][];
		for (Month month : Month.values())
			commits[month.number] = new int[month.days][HOURS];
	}

	/**
	 * Add time of calendar to this year
	 *
	 * @param calendar
	 * @return this year
	 */
	public YearCommitActivity add(final Calendar calendar) {
		final int month = calendar.get(Calendar.MONTH);
		final int day = calendar.get(Calendar.DAY_OF_MONTH) - 1;
		final int hour = calendar.get(Calendar.HOUR_OF_DAY);
		commits[month][day][hour]++;
		total++;
		return this;
	}

	/**
	 * Get commit activity in each month
	 *
	 * @return array of monthly activity
	 */
	public int[] getMonths() {
		return getMonths(new int[MONTHS]);
	}

	/**
	 * Get commit activity in each month
	 *
	 * @param months
	 * @return specified months array
	 */
	public int[] getMonths(final int[] months) {
		final int[][][] commits = this.commits;
		for (int i = 0; i < MONTHS; i++)
			for (int j = 0; j < commits[i].length; j++)
				for (int k = 0; k < HOURS; k++)
					months[i] += commits[i][j][k];
		return months;
	}

	/**
	 * Get commit activity in each day of the month
	 *
	 * @return array of daily activity
	 */
	public int[] getDays() {
		return getDays(new int[DAYS]);
	}

	/**
	 * Get commit activity in each day of the month
	 *
	 * @param days
	 * @return specified array of days
	 */
	public int[] getDays(final int[] days) {
		final int[][][] commits = this.commits;
		for (int i = 0; i < MONTHS; i++)
			for (int j = 0; j < commits[i].length; j++)
				for (int k = 0; k < HOURS; k++)
					days[j] += commits[i][j][k];
		return days;
	}

	/**
	 * Get commit activity in each hour of the day
	 *
	 * @return array of hourly activity
	 */
	public int[] getHours() {
		return getHours(new int[HOURS]);
	}

	/**
	 * Get commit activity in each hour of the day
	 *
	 * @param hours
	 * @return specified array of hours
	 */
	public int[] getHours(final int[] hours) {
		final int[][][] commits = this.commits;
		for (int i = 0; i < MONTHS; i++)
			for (int j = 0; j < commits[i].length; j++)
				for (int k = 0; k < HOURS; k++)
					hours[k] += commits[i][j][k];
		return hours;
	}

	/**
	 * Get number of commits in given month
	 *
	 * @param month
	 * @return number of commits
	 */
	public int getMonthCount(final Month month) {
		return getMonthCount(month.number);
	}

	/**
	 * Get number of commits in given month
	 *
	 * @param month
	 * @return number of commits
	 */
	public int getMonthCount(final int month) {
		int total = 0;
		final int[][] monthly = commits[month];
		for (int i = 0; i < monthly.length; i++)
			for (int j = 0; j < HOURS; j++)
				total += monthly[i][j];
		return total;
	}

	/**
	 * Get number of commits in given hour of the day
	 *
	 * @param hour
	 * @return number of commits
	 */
	public int getHourCount(final int hour) {
		final int[][][] commits = this.commits;
		int total = 0;
		for (int i = 0; i < MONTHS; i++)
			for (int j = 0; j < commits[i].length; j++)
				total += commits[i][j][hour];
		return total;
	}

	/**
	 * Get number commits in given day
	 *
	 * @param dayOfMonth
	 * @return number of commits
	 */
	public int getDayCount(final int dayOfMonth) {
		final int[][][] commits = this.commits;
		int total = 0;
		for (int i = 0; i < MONTHS; i++) {
			final int days = commits[i].length;
			if (dayOfMonth >= days)
				continue;
			final int[] hours = commits[i][dayOfMonth];
			for (int j = 0; j < HOURS; j++)
				total += hours[j];
		}
		return total;
	}

	/**
	 * Get year
	 *
	 * @return year
	 */
	public int getYear() {
		return year;
	}

	/**
	 * Get total number of commits in this year
	 *
	 * @return total commit count
	 */
	public int getCount() {
		return total;
	}
}
