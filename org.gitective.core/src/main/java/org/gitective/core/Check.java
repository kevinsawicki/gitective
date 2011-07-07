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
package org.gitective.core;

/**
 * Utility methods for various generic checks such as equality.
 */
public abstract class Check {

	/**
	 * Check if the two given objects are either both null or both equal
	 * according the {@link Object#equals(Object)} method of object1.
	 * 
	 * @param object1
	 * @param object2
	 * @return true if equal, false otherwise
	 */
	public static boolean equals(final Object object1, final Object object2) {
		return allNull(object1, object2) || equalsNonNull(object1, object2);
	}

	/**
	 * Check if the all given objects are null.
	 * 
	 * @param objects
	 * @return true if both are null, false otherwise
	 */
	public static boolean allNull(final Object... objects) {
		Assert.notNull("Objects cannot be null", objects);
		final int length = objects.length;
		for (int i = 0; i < length; i++)
			if (objects[i] != null)
				return false;
		return true;
	}

	/**
	 * Check if any of the given objects are null
	 * 
	 * @param objects
	 * @return true if any object is null, false otherwise
	 */
	public static boolean anyNull(final Object... objects) {
		Assert.notNull("Objects cannot be null", objects);
		final int length = objects.length;
		for (int i = 0; i < length; i++)
			if (objects[i] == null)
				return true;
		return false;
	}

	/**
	 * Check if the two given objects are both non-null and equal according to
	 * the {@link Object#equals(Object)} method of object1.
	 * 
	 * @param object1
	 * @param object2
	 * @return true if non-null and equal, false otherwise
	 */
	public static boolean equalsNonNull(final Object object1,
			final Object object2) {
		return !anyNull(object1, object2) && object1.equals(object2);
	}

}
