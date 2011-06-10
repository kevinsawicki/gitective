/******************************************************************************
 *  Copyright (c) 2011, Kevin Sawicki <kevinsawicki@gmail.com>
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *****************************************************************************/
package org.gitective.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.treewalk.filter.AndTreeFilter;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.eclipse.jgit.treewalk.filter.PathFilterGroup;
import org.eclipse.jgit.treewalk.filter.PathSuffixFilter;
import org.eclipse.jgit.treewalk.filter.TreeFilter;

/**
 * Utilities for dealing with paths when looking for commits
 */
public abstract class PathUtils {

	/**
	 * Create diff filter for paths
	 * 
	 * @param paths
	 * @return tree filter for diffs affecting given paths
	 */
	public static TreeFilter createPathFilter(final String... paths) {
		Assert.notNull("Paths cannot be null", paths);
		Assert.notEmpty("Paths cannot be empty", paths);
		final List<PathFilter> filters = new ArrayList<PathFilter>(paths.length);
		for (String path : paths)
			filters.add(PathFilter.create(path));
		TreeFilter groupFilter = PathFilterGroup.create(filters);
		return AndTreeFilter.create(groupFilter, TreeFilter.ANY_DIFF);
	}

	/**
	 * Create diff filter for suffixes
	 * 
	 * @param suffixes
	 * @return tree filter for diffs affecting given suffixes
	 */
	public static TreeFilter createSuffixFilter(final String... suffixes) {
		Assert.notNull("Suffixes cannot be null", suffixes);
		Assert.notEmpty("Suffixes cannot be empty", suffixes);
		final int length = suffixes.length;
		final TreeFilter[] filters = new TreeFilter[length + 1];
		for (int i = 0; i < length; i++)
			filters[i] = PathSuffixFilter.create(suffixes[i]);
		filters[length] = TreeFilter.ANY_DIFF;
		return AndTreeFilter.create(filters);
	}
}
