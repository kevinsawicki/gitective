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
package org.gitective.tests;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.RefDatabase;
import org.eclipse.jgit.lib.RefRename;
import org.eclipse.jgit.lib.RefUpdate;

/**
 * Ref database that throwns the same exception on every call
 */
public class BadRefDatabase extends RefDatabase {

	private final IOException exception;

	/**
	 * Create bad ref database
	 *
	 * @param exception
	 */
	public BadRefDatabase(IOException exception) {
		this.exception = exception;
	}

	public void create() throws IOException {
		throw exception;
	}

	public void close() {
	}

	public boolean isNameConflicting(String name) throws IOException {
		throw exception;
	}

	public RefUpdate newUpdate(String name, boolean detach) throws IOException {
		throw exception;
	}

	public RefRename newRename(String fromName, String toName)
			throws IOException {
		throw exception;
	}

	public Ref getRef(String name) throws IOException {
		throw exception;
	}

	public Map<String, Ref> getRefs(String prefix) throws IOException {
		throw exception;
	}

	public List<Ref> getAdditionalRefs() throws IOException {
		throw exception;
	}

	public Ref peel(Ref ref) throws IOException {
		throw exception;
	}
}
