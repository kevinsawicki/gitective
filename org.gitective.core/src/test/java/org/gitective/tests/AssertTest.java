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
import org.junit.Test;

/**
 * Units tests of the {@link Assert} class
 */
public class AssertTest extends org.junit.Assert {

	/**
	 * Test constructor
	 */
	@Test
	public void constructor() {
		assertNotNull(new Assert() {
		});
	}

	/**
	 * Test {@link Assert#notNull(String, Object)}
	 */
	@Test
	public void nullWithMessage() {
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
	@Test
	public void nonNullWithoutMessage() {
		try {
			Assert.notNull(new Object());
		} catch (IllegalArgumentException iae) {
			fail("Illegal argument exception thrown");
		}
	}

	/**
	 * Test {@link Assert#notNull(Object)}
	 */
	@Test
	public void nullWithoutMessage() {
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
	@Test
	public void nullWithNullMessage() {
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
	@Test
	public void nullWithEmptyMessage() {
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
	@Test
	public void emptyStringWithMessage() {
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
	@Test
	public void emptyStringWithoutMessage() {
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
	@Test
	public void emptyStringWithNullMessage() {
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
	@Test
	public void emptyStringWithEmptyMessage() {
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
	@Test
	public void nonEmptyString() {
		try {
			Assert.notEmpty("content");
		} catch (IllegalArgumentException iae) {
			fail("Illegal argument exception thrown");
		}
	}

	/**
	 * Test {@link Assert#notEmpty(String, Object[])}
	 */
	@Test
	public void emptyArrayWithMessage() {
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
	@Test
	public void emptyArrayWithoutMessage() {
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
	@Test
	public void emptyArrayWithNullMessage() {
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
	@Test
	public void emptyArrayWithEmptyMessage() {
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
	@Test
	public void nonEmptyArray() {
		try {
			Assert.notEmpty(new Object[10]);
		} catch (IllegalArgumentException iae) {
			fail("Illegal argument exception not thrown");
		}
	}
}
