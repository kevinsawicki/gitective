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

import org.gitective.core.PathUtils;
import org.gitective.core.filter.commit.CommitCountFilter;
import org.gitective.core.service.CommitFinder;

/**
 * Unit tests of path filtering
 */
public class PathTest extends GitTestCase {

	/**
	 * Test filtering commits matching either of two paths
	 * 
	 * @throws Exception
	 */
	public void testTwoPaths() throws Exception {
		add("file1.txt", "a");
		add("file2.txt", "b");
		add("file2.txt", "c");
		add("file3.txt", "d");
		add("file3.txt", "e");
		add("file3.txt", "f");

		CommitCountFilter count = new CommitCountFilter();
		CommitFinder finder = new CommitFinder(testRepo);
		finder.setMatcher(count);
		finder.setFilter(PathUtils.createPathFilter("file0.txt"));
		finder.find();
		assertEquals(0, count.getCount());
		finder.setFilter(PathUtils.createPathFilter("file1.txt"));
		finder.find();
		assertEquals(1, count.getCount());
		count.reset();
		finder.setFilter(PathUtils.createPathFilter("file2.txt"));
		finder.find();
		assertEquals(2, count.getCount());
		count.reset();
		finder.setFilter(PathUtils.createPathFilter("file3.txt"));
		finder.find();
		assertEquals(3, count.getCount());
	}
}
