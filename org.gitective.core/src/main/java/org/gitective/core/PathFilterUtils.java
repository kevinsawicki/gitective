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
import org.eclipse.jgit.treewalk.filter.OrTreeFilter;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.eclipse.jgit.treewalk.filter.PathFilterGroup;
import org.eclipse.jgit.treewalk.filter.PathSuffixFilter;
import org.eclipse.jgit.treewalk.filter.TreeFilter;

/**
 * Utilities for dealing with paths when looking for commits
 */
public abstract class PathFilterUtils {

	private static TreeFilter[] paths(String... paths) {
		final int length = paths.length;
		final TreeFilter[] filters = new TreeFilter[length];
		for (int i = 0; i < length; i++)
			filters[i] = PathFilter.create(paths[i]);
		return filters;
	}

	private static TreeFilter[] suffix(String... paths) {
		final int length = paths.length;
		final TreeFilter[] filters = new TreeFilter[length];
		for (int i = 0; i < length; i++)
			filters[i] = PathSuffixFilter.create(paths[i]);
		return filters;
	}

	private static TreeFilter group(final String... paths) {
		final List<PathFilter> filters = new ArrayList<PathFilter>(paths.length);
		for (String path : paths)
			filters.add(PathFilter.create(path));
		return PathFilterGroup.create(filters);
	}

	private static TreeFilter andDiff(TreeFilter[] filters) {
		if (filters.length > 1)
			return AndTreeFilter.create(AndTreeFilter.create(filters),
					TreeFilter.ANY_DIFF);
		else
			return AndTreeFilter.create(filters[0], TreeFilter.ANY_DIFF);
	}

	private static TreeFilter orDiff(TreeFilter[] filters) {
		if (filters.length > 1)
			return AndTreeFilter.create(OrTreeFilter.create(filters),
					TreeFilter.ANY_DIFF);
		else
			return AndTreeFilter.create(filters[0], TreeFilter.ANY_DIFF);
	}

	/**
	 * Create diff filter for paths
	 * 
	 * @param paths
	 * @return tree filter for diffs affecting given paths
	 */
	public static TreeFilter and(final String... paths) {
		Assert.notNull("Paths cannot be null", paths);
		Assert.notEmpty("Paths cannot be empty", paths);
		return andDiff(paths(paths));
	}

	/**
	 * Create diff filter for paths
	 * 
	 * @param paths
	 * @return tree filter for diffs affecting given paths
	 */
	public static TreeFilter or(final String... paths) {
		Assert.notNull("Paths cannot be null", paths);
		Assert.notEmpty("Paths cannot be empty", paths);
		return AndTreeFilter.create(group(paths), TreeFilter.ANY_DIFF);
	}

	/**
	 * Create diff filter for suffixes
	 * 
	 * @param suffixes
	 * @return tree filter for diffs affecting given suffixes
	 */
	public static TreeFilter andSuffix(final String... suffixes) {
		Assert.notNull("Suffixes cannot be null", suffixes);
		Assert.notEmpty("Suffixes cannot be empty", suffixes);
		return andDiff(suffix(suffixes));
	}

	/**
	 * Create diff filter for suffixes
	 * 
	 * @param suffixes
	 * @return tree filter for diffs affecting given suffixes
	 */
	public static TreeFilter orSuffix(final String... suffixes) {
		Assert.notNull("Suffixes cannot be null", suffixes);
		Assert.notEmpty("Suffixes cannot be empty", suffixes);
		return orDiff(suffix(suffixes));
	}
}
