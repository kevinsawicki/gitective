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

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.BaseRepositoryBuilder;
import org.eclipse.jgit.lib.RefDatabase;

/**
 * Repository that has a bad ref database
 */
public class BadRepository extends FileRepository {

	private final BadRefDatabase db;

	/**
	 * @param options
	 * @param exception
	 * @throws IOException
	 */
	public BadRepository(
			@SuppressWarnings("rawtypes") BaseRepositoryBuilder options,
			IOException exception) throws IOException {
		super(options);
		db = new BadRefDatabase(exception);
	}

	/**
	 * @param gitDir
	 * @param exception
	 * @throws IOException
	 */
	public BadRepository(File gitDir, IOException exception) throws IOException {
		super(gitDir);
		db = new BadRefDatabase(exception);
	}

	/**
	 * @param gitDir
	 * @param exception
	 * @throws IOException
	 */
	public BadRepository(String gitDir, IOException exception)
			throws IOException {
		super(gitDir);
		db = new BadRefDatabase(exception);
	}

	public RefDatabase getRefDatabase() {
		return db;
	}
}
