/******************************************************************************
 *  Copyright (c) 2011, Kevin Sawicki <kevinsawicki@gmail.com>
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *****************************************************************************/
package org.gitective.tests;

import org.gitective.core.filter.commit.AndCommitFilter;
import org.gitective.core.filter.commit.ChangeIdFilter;
import org.gitective.core.filter.commit.CommitCountFilter;
import org.gitective.core.service.CommitFinder;

/**
 * Unit tests of {@link ChangeIdFilter}
 */
public class ChangeIdTest extends GitTestCase {

	/**
	 * Test match
	 * 
	 * @throws Exception
	 */
	public void testMatch() throws Exception {
		add("file.txt", "patch",
				"fixes a bug\nChange-Id: I12345abcde12345abcde12345abcde12345abcde");

		CommitFinder service = new CommitFinder(testRepo);
		CommitCountFilter count = new CommitCountFilter();
		service.setRevFilter(new AndCommitFilter(new ChangeIdFilter(), count));
		service.find();
		assertEquals(1, count.getCount());

		service.setRevFilter(new AndCommitFilter(new ChangeIdFilter().clone(),
				count));
		service.find();
		assertEquals(2, count.getCount());
	}

	/**
	 * Test non-match
	 * 
	 * @throws Exception
	 */
	public void testNonMatch() throws Exception {
		add("file.txt", "patch",
				"fixes a bug\nChange-Id: I12345abcde12345abxyz12345abcde12345abcde");

		CommitFinder service = new CommitFinder(testRepo);
		CommitCountFilter count = new CommitCountFilter();
		service.setRevFilter(new AndCommitFilter(new ChangeIdFilter(), count));
		service.find();
		assertEquals(0, count.getCount());
	}

}
