/******************************************************************************
 *  Copyright (c) 2011, Kevin Sawicki <kevinsawicki@gmail.com>
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *****************************************************************************/
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
	 * @throws IllegalArgumentException
	 */
	public static void notNull(String message, Object object)
			throws IllegalArgumentException {
		if (object == null)
			throw new IllegalArgumentException(message != null
					&& message.length() > 0 ? message : "Object cannot be null");
	}

	/**
	 * Throw an {@link IllegalArgumentException} with a default message if the
	 * object is null.
	 * 
	 * @param object
	 * @throws IllegalArgumentException
	 */
	public static void notNull(Object object) throws IllegalArgumentException {
		notNull(null, object);
	}

	/**
	 * Throw an {@link IllegalArgumentException} with the message if the
	 * {@link Object} array has a length of zero.
	 * 
	 * @param message
	 * @param objects
	 * @throws IllegalArgumentException
	 */
	public static void notEmpty(String message, Object[] objects)
			throws IllegalArgumentException {
		if (objects.length == 0)
			throw new IllegalArgumentException(message != null
					&& message.length() > 0 ? message
					: "Objects cannot be empty");
	}

	/**
	 * Throw an {@link IllegalArgumentException} with a default message if the
	 * {@link Object} array has a length of zero.
	 * 
	 * @param objects
	 * @throws IllegalArgumentException
	 */
	public static void notEmpty(Object[] objects)
			throws IllegalArgumentException {
		notEmpty(null, objects);
	}

	/**
	 * Throw an {@link IllegalArgumentException} with the message if the string
	 * has a length of zero.
	 * 
	 * @param message
	 * @param string
	 * @throws IllegalArgumentException
	 */
	public static void notEmpty(String message, String string)
			throws IllegalArgumentException {
		if (string.length() == 0)
			throw new IllegalArgumentException(message != null
					&& message.length() > 0 ? message
					: "String cannot be empty");
	}

	/**
	 * Throw an {@link IllegalArgumentException} with a default message if the
	 * string has a length of zero.
	 * 
	 * @param string
	 * @throws IllegalArgumentException
	 */
	public static void notEmpty(String string) throws IllegalArgumentException {
		notEmpty(null, string);
	}

}
