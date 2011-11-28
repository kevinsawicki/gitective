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
package org.gitective.core.filter.tree;

import static org.eclipse.jgit.lib.FileMode.TYPE_FILE;
import static org.eclipse.jgit.lib.FileMode.TYPE_GITLINK;
import static org.eclipse.jgit.lib.FileMode.TYPE_MASK;
import static org.eclipse.jgit.lib.FileMode.TYPE_SYMLINK;
import static org.eclipse.jgit.lib.FileMode.TYPE_TREE;

import java.io.IOException;

import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.TreeFilter;

/**
 * Tree filter that counts entries visited of a configured type
 */
public class TypeCountFilter extends BaseTreeFilter {

	/**
	 * Count filter that counts submodules
	 *
	 * @return submodule type filter
	 */
	public static TypeCountFilter submodule() {
		return new TypeCountFilter(TYPE_GITLINK);
	}

	/**
	 * Create filter that counts files
	 *
	 * @return file type filter
	 */
	public static TypeCountFilter file() {
		return new TypeCountFilter(TYPE_FILE);
	}

	/**
	 * Create filter that counts symbolic links
	 *
	 * @return symbolic link type filter
	 */
	public static TypeCountFilter symlink() {
		return new TypeCountFilter(TYPE_SYMLINK);
	}

	/**
	 * Create filter that counts trees
	 *
	 * @return tree type filter
	 */
	public static TypeCountFilter tree() {
		return new TypeCountFilter(TYPE_TREE);
	}

	private final int type;

	private long count;

	/**
	 * Create filter that counts the configured type
	 *
	 * @param type
	 */
	public TypeCountFilter(final int type) {
		this.type = type;
	}

	/**
	 * Get entry type count
	 *
	 * @return count
	 */
	public long getCount() {
		return count;
	}

	/**
	 * Get type being counted
	 *
	 * @return type
	 */
	public int getType() {
		return type;
	}

	@Override
	public BaseTreeFilter reset() {
		count = 0L;
		return super.reset();
	}

	@Override
	public boolean include(final TreeWalk walker) throws IOException {
		if (type == (walker.getRawMode(0) & TYPE_MASK))
			count++;
		return true;
	}

	@Override
	public TreeFilter clone() {
		return new TypeCountFilter(type);
	}
}
