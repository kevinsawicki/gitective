/******************************************************************************
 *  Copyright (c) 2011, Kevin Sawicki <kevinsawicki@gmail.com>
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *****************************************************************************/
package org.gitective.tests;

import org.gitective.core.filter.commit.AndCommitFilter;
import org.gitective.core.filter.commit.CommitCountFilter;
import org.gitective.core.filter.commit.ParentCountFilter;
import org.gitective.core.service.CommitService;

/**
 * Unit tests of {@link ParentCountFilter}
 */
public class ParentTest extends GitTestCase {

	/**
	 * Test non-match
	 * 
	 * @throws Exception
	 */
	public void testNonMatch() throws Exception {
		add("file.txt", "abc");

		CommitCountFilter count = new CommitCountFilter();
		CommitService service = new CommitService(testRepo);
		service.search(new AndCommitFilter().add(new ParentCountFilter())
				.add(count));
		assertEquals(0, count.getCount());
	}
}
