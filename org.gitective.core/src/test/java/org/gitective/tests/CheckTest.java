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

import junit.framework.TestCase;

import org.gitective.core.Check;

/**
 * Unit tests of {@link Check} class
 */
public class CheckTest extends TestCase {

	/**
	 * Test {@link Check} class constructor
	 */
	public void testContructor() {
		assertNotNull(new Check() {
		});
	}

	/**
	 * Unit test of {@link Check#anyNull(Object...)}
	 */
	public void testAnyNull() {
		assertTrue(Check.anyNull((Object) null));
		assertFalse(Check.anyNull(this));
		assertTrue(Check.anyNull("", (Object) null));
	}

	/**
	 * Unit test of {@link Check#allNull(Object...)}
	 */
	public void testAllNull() {
		assertTrue(Check.allNull((Object) null));
		assertFalse(Check.allNull(this));
		assertFalse(Check.allNull("", (Object) null));
	}

	/**
	 * Unit test of {@link Check#equals(Object)}
	 */
	public void testEquals() {
		assertTrue(Check.equals(null, null));
		assertFalse(Check.equals("a", null));
		assertFalse(Check.equals(null, "b"));
		assertFalse(Check.equals("a", "b"));
		assertTrue(Check.equals("a", "a"));
	}

	/**
	 * Unit test of {@link Check#equalsNonNull(Object, Object)}
	 */
	public void testEqualsNonNull() {
		assertFalse(Check.equalsNonNull(null, null));
		assertFalse(Check.equalsNonNull("a", "b"));
		assertTrue(Check.equalsNonNull("a", "a"));
	}
}
