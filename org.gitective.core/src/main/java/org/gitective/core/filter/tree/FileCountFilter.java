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

import java.io.IOException;

import org.eclipse.jgit.lib.FileMode;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.TreeFilter;

/**
 * Tree filter that counts all files visited
 */
public class FileCountFilter extends BaseTreeFilter {

	private long count;

	/**
	 * Get number of files visited
	 *
	 * @return file count
	 */
	public long getCount() {
		return count;
	}

	public BaseTreeFilter reset() {
		count = 0L;
		return super.reset();
	}

	@Override
	public boolean include(final TreeWalk walker) throws IOException {
		if (FileMode.TYPE_FILE == (walker.getRawMode(0) & FileMode.TYPE_MASK))
			count++;
		return true;
	}

	@Override
	public TreeFilter clone() {
		return new FileCountFilter();
	}
}
