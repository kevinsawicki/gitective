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
package org.gitective.tests;

import java.util.Date;
import java.util.TimeZone;

import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.revwalk.RevCommit;
import org.gitective.core.stat.EarliestComparator;
import org.gitective.core.stat.UserCommitActivity;
import org.junit.Test;

/**
 * Unit tests of {@link EarliestComparatorTest}
 */
public class EarliestComparatorTest extends GitTestCase {

	/**
	 * Test comparing user activity
	 * 
	 * @throws Exception
	 */
	@Test
	public void compare() throws Exception {
		RevCommit commit = add("test.txt", "content");

		UserCommitActivity user1 = new UserCommitActivity("a", "b");
		UserCommitActivity user2 = new UserCommitActivity("c", "d");

		user1.include(commit, new PersonIdent("a", "b", new Date(1000),
				TimeZone.getDefault()));

		user2.include(commit, new PersonIdent("b", "c", new Date(2000),
				TimeZone.getDefault()));

		EarliestComparator comparator = new EarliestComparator();
		assertEquals(0, comparator.compare(user1, user1));
		assertEquals(0, comparator.compare(user2, user2));
		assertTrue(comparator.compare(user1, user2) < 0);
		assertTrue(comparator.compare(user2, user1) > 0);
	}
}
