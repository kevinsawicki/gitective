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

import org.gitective.core.Assert;

import junit.framework.TestCase;

/**
 * Units tests of the {@link Assert} class
 */
public class AssertTest extends TestCase {

	/**
	 * Test constructor
	 */
	public void testConstructor() {
		assertNotNull(new Assert() {
		});
	}

	/**
	 * Test {@link Assert#notNull(String, Object)}
	 */
	public void testNullWithMessage() {
		String message = "this is null";
		try {
			Assert.notNull(message, null);
			fail("Illegal argument exception not thrown");
		} catch (IllegalArgumentException iae) {
			assertEquals(message, iae.getMessage());
		}
	}

	/**
	 * Test {@link Assert#notNull(Object)}
	 */
	public void testNonNull() {
		try {
			Assert.notNull(new Object());
		} catch (IllegalArgumentException iae) {
			fail("Illegal argument exception thrown");
		}
	}

	/**
	 * Test {@link Assert#notNull(Object)}
	 */
	public void testNullWithoutMessage() {
		try {
			Assert.notNull(null);
			fail("Illegal argument exception not thrown");
		} catch (IllegalArgumentException iae) {
			assertNotNull(iae.getMessage());
			assertTrue(iae.getMessage().length() > 0);
		}
	}

	/**
	 * Test {@link Assert#notNull(String, Object)}
	 */
	public void testNullWithNullMessage() {
		try {
			Assert.notNull(null, null);
			fail("Illegal argument exception not thrown");
		} catch (IllegalArgumentException iae) {
			assertNotNull(iae.getMessage());
			assertTrue(iae.getMessage().length() > 0);
		}
	}

	/**
	 * Test {@link Assert#notNull(String, Object)}
	 */
	public void testNullWithEmptyMessage() {
		try {
			Assert.notNull("", null);
			fail("Illegal argument exception not thrown");
		} catch (IllegalArgumentException iae) {
			assertNotNull(iae.getMessage());
			assertTrue(iae.getMessage().length() > 0);
		}
	}

	/**
	 * Test {@link Assert#notEmpty(String, String)}
	 */
	public void testEmptyStringWithMessage() {
		String message = "string is empty";
		try {
			Assert.notEmpty(message, "");
			fail("Illegal argument exception not thrown");
		} catch (IllegalArgumentException iae) {
			assertEquals(message, iae.getMessage());
		}
	}

	/**
	 * Test {@link Assert#notEmpty(String)}
	 */
	public void testEmptyStringWithoutMessage() {
		try {
			Assert.notEmpty("");
			fail("Illegal argument exception not thrown");
		} catch (IllegalArgumentException iae) {
			assertNotNull(iae.getMessage());
			assertTrue(iae.getMessage().length() > 0);
		}
	}

	/**
	 * Test {@link Assert#notEmpty(String, String)}
	 */
	public void testEmptyStringWithNullMessage() {
		try {
			Assert.notEmpty(null, "");
			fail("Illegal argument exception not thrown");
		} catch (IllegalArgumentException iae) {
			assertNotNull(iae.getMessage());
			assertTrue(iae.getMessage().length() > 0);
		}
	}

	/**
	 * Test {@link Assert#notEmpty(String, String)}
	 */
	public void testEmptyStringWithEmptyMessage() {
		try {
			Assert.notEmpty("", "");
			fail("Illegal argument exception not thrown");
		} catch (IllegalArgumentException iae) {
			assertNotNull(iae.getMessage());
			assertTrue(iae.getMessage().length() > 0);
		}
	}

	/**
	 * Test {@link Assert#notEmpty(String)}
	 */
	public void testNonEmptyString() {
		try {
			Assert.notEmpty("content");
		} catch (IllegalArgumentException iae) {
			fail("Illegal argument exception thrown");
		}
	}

	/**
	 * Test {@link Assert#notEmpty(String, Object[])}
	 */
	public void testEmptyArrayWithMessage() {
		String message = "array is empty";
		try {
			Assert.notEmpty(message, new Object[0]);
			fail("Illegal argument exception not thrown");
		} catch (IllegalArgumentException iae) {
			assertEquals(message, iae.getMessage());
		}
	}

	/**
	 * Test {@link Assert#notEmpty(String, Object[])}
	 */
	public void testEmptyArrayWithoutMessage() {
		try {
			Assert.notEmpty(new Object[0]);
			fail("Illegal argument exception not thrown");
		} catch (IllegalArgumentException iae) {
			assertNotNull(iae.getMessage());
			assertTrue(iae.getMessage().length() > 0);
		}
	}

	/**
	 * Test {@link Assert#notEmpty(String, Object[])}
	 */
	public void testEmptyArrayWithNullMessage() {
		try {
			Assert.notEmpty(null, new Object[0]);
			fail("Illegal argument exception not thrown");
		} catch (IllegalArgumentException iae) {
			assertNotNull(iae.getMessage());
			assertTrue(iae.getMessage().length() > 0);
		}
	}

	/**
	 * Test {@link Assert#notEmpty(String, Object[])}
	 */
	public void testEmptyArrayWithEmptyMessage() {
		try {
			Assert.notEmpty("", new Object[0]);
			fail("Illegal argument exception not thrown");
		} catch (IllegalArgumentException iae) {
			assertNotNull(iae.getMessage());
			assertTrue(iae.getMessage().length() > 0);
		}
	}

	/**
	 * Test {@link Assert#notEmpty(Object[])}
	 */
	public void testNonEmptyArray() {
		try {
			Assert.notEmpty(new Object[10]);
		} catch (IllegalArgumentException iae) {
			fail("Illegal argument exception not thrown");
		}
	}
}
