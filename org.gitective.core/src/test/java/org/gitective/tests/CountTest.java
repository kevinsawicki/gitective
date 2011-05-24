/******************************************************************************
 *  Copyright (c) 2011, Kevin Sawicki <kevinsawicki@gmail.com>
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *****************************************************************************/
package org.gitective.tests;

import org.eclipse.jgit.revwalk.filter.RevFilter;
import org.gitective.core.filter.commit.CommitCountFilter;
import org.gitective.core.filter.commit.CommitListFilter;

/**
 * Unit tests of {@link CommitCountFilter}
 */
public class CountTest extends GitTestCase {

	/**
	 * Unit test of {@link CommitListFilter#clone()}
	 */
	public void testClone() {
		CommitCountFilter filter = new CommitCountFilter();
		assertEquals(0, filter.getCount());
		RevFilter clone = filter.clone();
		assertNotNull(clone);
		assertNotSame(filter, clone);
		assertTrue(clone instanceof CommitCountFilter);
		assertEquals(0, ((CommitCountFilter) clone).getCount());
	}

}
