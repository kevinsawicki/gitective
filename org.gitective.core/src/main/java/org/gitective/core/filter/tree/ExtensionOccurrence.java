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

/**
 * Class representing the occurrences of a file extension
 */
public class ExtensionOccurrence {

	/**
	 * Number of occurrences
	 */
	protected int count;

	private final String extension;

	/**
	 * Create occurrence for given extension
	 *
	 * @param extension
	 *            must be non-null
	 */
	protected ExtensionOccurrence(final String extension) {
		this.extension = extension;
		count = 1;
	}

	/**
	 * Get number of occurrences
	 *
	 * @return count
	 */
	public int getCount() {
		return count;
	}

	/**
	 * Get extension
	 *
	 * @return extension
	 */
	public String getExtension() {
		return extension;
	}

	public int hashCode() {
		return toString().hashCode();
	}

	public boolean equals(final Object obj) {
		if (obj == this)
			return true;
		if (!(obj instanceof ExtensionOccurrence))
			return false;
		final ExtensionOccurrence other = (ExtensionOccurrence) obj;
		return count == other.count && extension.equals(other.extension);
	}

	public String toString() {
		return extension + "=" + count;
	}
}
