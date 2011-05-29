/******************************************************************************
 *  Copyright (c) 2011, Kevin Sawicki <kevinsawicki@gmail.com>
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *****************************************************************************/
package org.gitective.tests;

import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepository;
import org.gitective.core.CommitUtils;
import org.gitective.core.filter.commit.CommitListFilter;
import org.gitective.core.service.CommitFinder;

/**
 * Tests of
 * {@link CommitUtils#getBase(Repository, org.eclipse.jgit.lib.ObjectId...)} and
 * {@link CommitUtils#getBase(Repository, String...)}
 */
public class CommitBaseTest extends GitTestCase {

	/**
	 * Test getting the base commit of a branch and master and then walking
	 * between the tip of the branch and its branch point.
	 * 
	 * @throws Exception
	 */
	public void testBaseCommits() throws Exception {
		RevCommit commit1 = add("file.txt", "content");
		branch("release1");
		RevCommit commit2 = add("file.txt", "edit 1");
		RevCommit commit3 = add("file.txt", "edit 2");

		Repository repo = new FileRepository(testRepo);
		RevCommit base = CommitUtils
				.getBase(repo, Constants.MASTER, "release1");
		assertEquals(commit1, base);

		RevCommit base2 = CommitUtils.getBase(repo,
				CommitUtils.getCommit(repo, Constants.MASTER),
				CommitUtils.getCommit(repo, "release1"));
		assertEquals(base, base2);

		CommitListFilter filter = new CommitListFilter();
		new CommitFinder(testRepo).findBetween(commit3, base, filter);
		assertEquals(2, filter.getCommits().size());
		assertEquals(commit3, filter.getCommits().get(0));
		assertEquals(commit2, filter.getCommits().get(1));
	}
}
