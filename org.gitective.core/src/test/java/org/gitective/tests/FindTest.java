/******************************************************************************
 *  Copyright (c) 2011, Kevin Sawicki <kevinsawicki@gmail.com>
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *****************************************************************************/
package org.gitective.tests;

import org.eclipse.jgit.revwalk.filter.RevFilter;
import org.gitective.core.filter.commit.AndCommitFilter;
import org.gitective.core.filter.commit.CommitCountFilter;
import org.gitective.core.filter.commit.CommitMessageFindFilter;
import org.gitective.core.filter.commit.PatternFindCommitFilter;
import org.gitective.core.service.CommitFinder;

/**
 * Unit tests of {@link PatternFindCommitFilter}
 */
public class FindTest extends GitTestCase {

	/**
	 * Test match
	 * 
	 * @throws Exception
	 */
	public void testMatch() throws Exception {
		add("file.txt", "content", "matchmiddlehere");
		CommitMessageFindFilter find = new CommitMessageFindFilter("middle");
		CommitCountFilter count = new CommitCountFilter();
		CommitFinder service = new CommitFinder(testRepo);
		service.find(new AndCommitFilter(find, count));
		assertEquals(1, count.getCount());
	}

	/**
	 * Test non-match
	 * 
	 * @throws Exception
	 */
	public void testNonMatch() throws Exception {
		add("file.txt", "content", "matchmiddlehere");
		CommitMessageFindFilter find = new CommitMessageFindFilter("nomatch");
		CommitCountFilter count = new CommitCountFilter();
		CommitFinder service = new CommitFinder(testRepo);
		service.find(new AndCommitFilter(find, count));
		assertEquals(0, count.getCount());
	}

	/**
	 * Test {@link CommitMessageFindFilter#clone()}
	 * 
	 * @throws Exception
	 */
	public void testClone() throws Exception {
		CommitMessageFindFilter find = new CommitMessageFindFilter("content");
		RevFilter clone = find.clone();
		assertNotNull(clone);
		assertNotSame(find, clone);
		assertTrue(clone instanceof CommitMessageFindFilter);
	}

}
