/******************************************************************************
 *  Copyright (c) 2011, Kevin Sawicki <kevinsawicki@gmail.com>
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *****************************************************************************/
package org.gitective.tests;

import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.PersonIdent;
import org.gitective.core.filter.commit.AndCommitFilter;
import org.gitective.core.filter.commit.CommitCountFilter;
import org.gitective.core.filter.commit.SignedOffByFilter;
import org.gitective.core.service.CommitFinder;

/**
 * Unit tests of {@link SignedOffByFilter}
 */
public class SignedOffByTest extends GitTestCase {

	/**
	 * Test match
	 * 
	 * @throws Exception
	 */
	public void testMatch() throws Exception {
		PersonIdent person = new PersonIdent("Test user", "test@user.com");
		add("file.txt", "patch", Constants.SIGNED_OFF_BY_TAG + person.getName()
				+ " <" + person.getEmailAddress() + ">");

		CommitFinder service = new CommitFinder(testRepo);
		CommitCountFilter count = new CommitCountFilter();
		service.setFilter(new AndCommitFilter(new SignedOffByFilter(person),
				count));
		service.find();
		assertEquals(1, count.getCount());

		service.setFilter(new AndCommitFilter(new SignedOffByFilter(person)
				.clone(), count));
		service.find();
		assertEquals(2, count.getCount());
	}

	/**
	 * Test non-match
	 * 
	 * @throws Exception
	 */
	public void testNonMatch() throws Exception {
		PersonIdent person = new PersonIdent("Test user", "test@user.com");
		add("file.txt", "patch", Constants.SIGNED_OFF_BY_TAG + "person");

		CommitFinder service = new CommitFinder(testRepo);
		CommitCountFilter count = new CommitCountFilter();
		service.setFilter(new AndCommitFilter(new SignedOffByFilter(person),
				count));
		service.find();
		assertEquals(0, count.getCount());
	}

}
