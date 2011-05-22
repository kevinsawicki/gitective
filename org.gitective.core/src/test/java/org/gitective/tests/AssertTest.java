/******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *****************************************************************************/
package org.gitective.tests;

import org.gitective.core.Assert;

import junit.framework.TestCase;

/**
 * Units tests of the {@link Assert} class
 */
public class AssertTest extends TestCase {

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
