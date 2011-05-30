/******************************************************************************
 *  Copyright (c) 2011, Kevin Sawicki <kevinsawicki@gmail.com>
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *****************************************************************************/
package org.gitective.tests;

import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.filter.RevFilter;
import org.gitective.core.filter.commit.AndCommitFilter;
import org.gitective.core.filter.commit.CommitCountFilter;
import org.gitective.core.filter.commit.CommitListFilter;
import org.gitective.core.filter.commit.ParentCountFilter;
import org.gitective.core.service.CommitFinder;

/**
 * Unit tests of {@link ParentCountFilter}
 */
public class ParentTest extends GitTestCase {

	/**
	 * Test match
	 * 
	 * @throws Exception
	 */
	public void testMatch() throws Exception {
		add("file.txt", "abc");
		add("file.txt", "abcd");

		CommitCountFilter count = new CommitCountFilter();
		CommitFinder service = new CommitFinder(testRepo);
		service.setRevFilter(new AndCommitFilter(new ParentCountFilter(1),
				count));
		service.find();
		assertEquals(1, count.getCount());
	}

	/**
	 * Test non-match
	 * 
	 * @throws Exception
	 */
	public void testNonMatch() throws Exception {
		add("file.txt", "abc");

		CommitCountFilter count = new CommitCountFilter();
		CommitFinder service = new CommitFinder(testRepo);
		service.setRevFilter(new AndCommitFilter(new ParentCountFilter(), count));
		service.find();
		assertEquals(0, count.getCount());
	}

	/**
	 * Test of {@link ParentCountFilter#clone()}
	 * 
	 * @throws Exception
	 */
	public void testClone() throws Exception {
		add("file.txt", "abc");
		RevCommit commit2 = add("file.txt", "abcd");

		CommitListFilter commits = new CommitListFilter();
		CommitFinder service = new CommitFinder(testRepo);
		ParentCountFilter parents = new ParentCountFilter(1);
		service.setRevFilter(new AndCommitFilter(parents, commits));
		service.find();
		assertEquals(commit2, commits.getCommits().get(0));

		RevFilter clone = parents.clone();
		assertNotNull(clone);
		assertNotSame(parents, clone);

		commits = new CommitListFilter();
		service.setRevFilter(new AndCommitFilter(clone, commits));
		service.find();
		assertEquals(commit2, commits.getCommits().get(0));

	}
}
