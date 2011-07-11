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

import java.util.Locale;

/**
 * Months with number of days and indices
 */
public enum Month {

	/** JANUARY */
	JANUARY(0, 31),

	/** FEBRUARY */
	FEBRUARY(1, 29),

	/** MARCH */
	MARCH(2, 31),

	/** APRIL */
	APRIL(3, 30),

	/** MAY */
	MAY(4, 31),

	/** JUNE */
	JUNE(5, 30),

	/** JULY */
	JULY(6, 31),

	/** AUGUST */
	AUGUST(7, 31),

	/** SEPTEMBER */
	SEPTEMBER(8, 30),

	/** OCTOBER */
	OCTOBER(9, 31),

	/** NOVEMBER */
	NOVEMBER(10, 30),

	/** DECEMBER */
	DECEMBER(11, 31);

	/**
	 * Month number
	 */
	public final int number;

	/**
	 * Days in month
	 */
	public final int days;

	/**
	 * Get month for given number
	 * 
	 * @param number
	 * @return month
	 */
	public static Month month(final int number) {
		switch (number) {
		case 0:
			return JANUARY;
		case 1:
			return FEBRUARY;
		case 2:
			return MARCH;
		case 3:
			return APRIL;
		case 4:
			return MAY;
		case 5:
			return JUNE;
		case 6:
			return JULY;
		case 7:
			return AUGUST;
		case 8:
			return SEPTEMBER;
		case 9:
			return OCTOBER;
		case 10:
			return NOVEMBER;
		case 11:
			return DECEMBER;
		default:
			return null;
		}
	}

	Month(final int number, final int days) {
		this.number = number;
		this.days = days;
	}

	public String toString() {
		final String name = name();
		return name.substring(0, 1) + name.substring(1).toLowerCase(Locale.US);
	}
}
