/******************************************************************************
 *  Copyright (c) 2011 Kevin Sawicki <kevinsawicki@gmail.com
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *****************************************************************************/
package org.gitective.tests;

import org.gitective.core.GitException;

import junit.framework.TestCase;

/**
 * Unit tests of {@link GitException}
 */
public class GitExceptionTest extends TestCase {

	/**
	 * Test constructors
	 */
	public void testConstructors() {
		String message = "test";
		NullPointerException cause = new NullPointerException();

		GitException exception = new GitException(message);
		assertEquals(message, exception.getMessage());

		exception = new GitException(cause);
		assertEquals(cause, exception.getCause());

		exception = new GitException(message, cause);
		assertEquals(message, exception.getMessage());
		assertEquals(cause, exception.getCause());
	}

}
