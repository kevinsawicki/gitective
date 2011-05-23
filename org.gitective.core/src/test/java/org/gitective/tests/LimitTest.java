/******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *****************************************************************************/
package org.gitective.tests;

import org.gitective.core.filter.commit.AndCommitFilter;
import org.gitective.core.filter.commit.CommitCountFilter;
import org.gitective.core.filter.commit.CommitLimitFilter;
import org.gitective.core.service.CommitService;

/**
 * Unit tests of {@link CommitLimitFilter}
 */
public class LimitTest extends GitTestCase {

	/**
	 * Test limiting number of commits in walk
	 * 
	 * @throws Exception
	 */
	public void testLimit() throws Exception {
		add("file1.txt", "a");
		add("file2.txt", "b");
		add("file3.txt", "c");
		CommitService service = new CommitService(testRepo);
		CommitCountFilter count = new CommitCountFilter();
		service.walkFromHead(new AndCommitFilter()
				.add(new CommitLimitFilter(1)).add(count));
		assertEquals(1, count.getCount());
		count.reset();
		service.walkFromHead(new AndCommitFilter()
				.add(new CommitLimitFilter(3)).add(count));
		assertEquals(3, count.getCount());
	}

}
