/******************************************************************************
 *  Copyright (c) 2011, Kevin Sawicki <kevinsawicki@gmail.com>
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *****************************************************************************/
package org.gitective.tests;

import org.gitective.core.CommitUtils;

import junit.framework.TestCase;

/**
 * Unit tests of {@link CommitUtils}
 */
public class CommitUtilsTest extends TestCase {

	/**
	 * Test constructor
	 */
	public void testConstructor() {
		assertNotNull(new CommitUtils() {
		});
	}
}
