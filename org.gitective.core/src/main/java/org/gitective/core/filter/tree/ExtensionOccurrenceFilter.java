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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.TreeFilter;

/**
 * Filter that collects the occurrence count of every file extension of each
 * tree visited.
 */
public class ExtensionOccurrenceFilter extends BaseTreeFilter implements
		Iterable<ExtensionOccurrence> {

	private final Map<String, ExtensionOccurrence> extensions = new HashMap<String, ExtensionOccurrence>();

	/**
	 * Get occurrences of given extension.
	 * <p>
	 * The given extension should not contain a leading '.' character.
	 *
	 * @param extension
	 * @return occurrence count
	 */
	public int getCount(final String extension) {
		if (extension == null || extension.length() == 0)
			return 0;
		final ExtensionOccurrence count = extensions.get(extension);
		return count != null ? count.count : 0;
	}

	/**
	 * Get file extension encountered. The returned string will not contain a
	 * leading '.' character
	 *
	 * @return non-null but possibly empty array of file extensions
	 */
	public String[] getExtensions() {
		return extensions.keySet().toArray(new String[extensions.size()]);
	}

	/**
	 * Get map of extensions to occurrence counts. The extensions contained in
	 * the returned map will not include a leading '.' character.
	 *
	 * @return non-null but possibly empty map
	 */
	public Map<String, ExtensionOccurrence> getOccurrences() {
		return extensions;
	}

	@Override
	public BaseTreeFilter reset() {
		extensions.clear();
		return super.reset();
	}

	@Override
	public boolean include(final TreeWalk walker) throws IOException {
		if (walker.isSubtree())
			return true;
		String name = walker.getNameString();
		final int extensionStart = name.lastIndexOf('.') + 1;
		// Ignore names that don't contain a '.' or end with a '.'
		if (extensionStart == 0 || extensionStart == name.length())
			return true;
		name = name.substring(extensionStart);
		final ExtensionOccurrence occurrence = extensions.get(name);
		if (occurrence != null)
			occurrence.count++;
		else
			extensions.put(name, new ExtensionOccurrence(name));
		return true;
	}

	@Override
	public TreeFilter clone() {
		return new ExtensionOccurrenceFilter();
	}

	public Iterator<ExtensionOccurrence> iterator() {
		return extensions.values().iterator();
	}
}
