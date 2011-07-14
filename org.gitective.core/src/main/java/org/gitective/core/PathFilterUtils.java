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

	private static TreeFilter[] paths(final String... paths) {
		final int length = paths.length;
		final TreeFilter[] filters = new TreeFilter[length];
		for (int i = 0; i < length; i++)
			filters[i] = PathFilter.create(paths[i]);
		return filters;
	}

	private static TreeFilter[] suffix(final String... paths) {
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

	private static TreeFilter andDiff(final TreeFilter[] filters) {
		if (filters.length > 1)
			return AndTreeFilter.create(AndTreeFilter.create(filters),
					TreeFilter.ANY_DIFF);
		else
			return AndTreeFilter.create(filters[0], TreeFilter.ANY_DIFF);
	}

	private static TreeFilter orDiff(final TreeFilter[] filters) {
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
		if (paths == null)
			throw new IllegalArgumentException(Assert.formatNotNull("Paths"));
		if (paths.length == 0)
			throw new IllegalArgumentException(Assert.formatNotEmpty("Paths"));
		return andDiff(paths(paths));
	}

	/**
	 * Create diff filter for paths
	 * 
	 * @param paths
	 * @return tree filter for diffs affecting given paths
	 */
	public static TreeFilter or(final String... paths) {
		if (paths == null)
			throw new IllegalArgumentException(Assert.formatNotNull("Paths"));
		if (paths.length == 0)
			throw new IllegalArgumentException(Assert.formatNotEmpty("Paths"));
		return AndTreeFilter.create(group(paths), TreeFilter.ANY_DIFF);
	}

	/**
	 * Create diff filter for suffixes
	 * 
	 * @param suffixes
	 * @return tree filter for diffs affecting given suffixes
	 */
	public static TreeFilter andSuffix(final String... suffixes) {
		if (suffixes == null)
			throw new IllegalArgumentException(Assert.formatNotNull("Suffixes"));
		if (suffixes.length == 0)
			throw new IllegalArgumentException(
					Assert.formatNotEmpty("Suffixes"));
		return andDiff(suffix(suffixes));
	}

	/**
	 * Create diff filter for suffixes
	 * 
	 * @param suffixes
	 * @return tree filter for diffs affecting given suffixes
	 */
	public static TreeFilter orSuffix(final String... suffixes) {
		if (suffixes == null)
			throw new IllegalArgumentException(Assert.formatNotNull("Suffixes"));
		if (suffixes.length == 0)
			throw new IllegalArgumentException(
					Assert.formatNotEmpty("Suffixes"));
		return orDiff(suffix(suffixes));
	}
}
