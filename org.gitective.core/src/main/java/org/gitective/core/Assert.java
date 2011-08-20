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
 * Assert utilities that all throw {@link IllegalArgumentException} that will
 * have a non-null/non-empty message.
 */
public abstract class Assert {

	/**
	 * Throw an {@link IllegalArgumentException} with the message if the object
	 * is null.
	 *
	 * @param message
	 * @param object
	 * @return object
	 * @throws IllegalArgumentException
	 */
	public static <V> V notNull(final String message, final V object)
			throws IllegalArgumentException {
		if (object == null)
			throw new IllegalArgumentException(message != null
					&& message.length() > 0 ? message : "Object cannot be null");
		return object;
	}

	/**
	 * Throw an {@link IllegalArgumentException} with a default message if the
	 * object is null.
	 *
	 * @param object
	 * @return object
	 * @throws IllegalArgumentException
	 */
	public static <V> V notNull(final V object) throws IllegalArgumentException {
		return notNull(null, object);
	}

	/**
	 * Throw an {@link IllegalArgumentException} with the message if the
	 * {@link Object} array has a length of zero.
	 *
	 * @param message
	 * @param objects
	 * @return objects
	 * @throws IllegalArgumentException
	 */
	public static <V> V[] notEmpty(final String message, final V[] objects)
			throws IllegalArgumentException {
		if (objects.length == 0)
			throw new IllegalArgumentException(message != null
					&& message.length() > 0 ? message
					: "Objects cannot be empty");
		return objects;
	}

	/**
	 * Throw an {@link IllegalArgumentException} with a default message if the
	 * {@link Object} array has a length of zero.
	 *
	 * @param objects
	 * @return objects
	 * @throws IllegalArgumentException
	 */
	public static <V> V[] notEmpty(final V[] objects)
			throws IllegalArgumentException {
		return notEmpty(null, objects);
	}

	/**
	 * Throw an {@link IllegalArgumentException} with the message if the string
	 * has a length of zero.
	 *
	 * @param message
	 * @param string
	 * @return string
	 * @throws IllegalArgumentException
	 */
	public static String notEmpty(final String message, final String string)
			throws IllegalArgumentException {
		if (string.length() == 0)
			throw new IllegalArgumentException(message != null
					&& message.length() > 0 ? message
					: "String cannot be empty");
		return string;
	}

	/**
	 * Throw an {@link IllegalArgumentException} with a default message if the
	 * string has a length of zero.
	 *
	 * @param string
	 * @return string
	 * @throws IllegalArgumentException
	 */
	public static String notEmpty(final String string)
			throws IllegalArgumentException {
		return notEmpty(null, string);
	}

	/**
	 * Return formatted not null message with given prefix
	 *
	 * @param prefix
	 * @return non-null message
	 */
	public static String formatNotNull(final String prefix) {
		return prefix + " cannot be null";
	}

	/**
	 * Return formatted not empty message with given prefix
	 *
	 * @param prefix
	 * @return non-empty message
	 */
	public static String formatNotEmpty(final String prefix) {
		return prefix + " cannot be empty";
	}
}
