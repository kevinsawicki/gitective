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

import org.gitective.core.stat.Month;
import org.junit.Test;

import junit.framework.Assert;

/**
 * Unit test of {@link Month} enum
 */
public class MonthTest extends Assert {

	/**
	 * Test month values
	 */
	@Test
	public void monthValues() {
		assertNull(Month.month(-1));
		for (Month month : Month.values()) {
			assertNotNull(month);
			assertTrue(month.number >= 0);
			assertTrue(month.days > 0);
			assertEquals(month, Month.month(month.number));
			assertNotNull(month.toString());
			assertTrue(month.toString().length() > 0);
			assertEquals(month, Month.valueOf(month.name()));
		}
	}
}
