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

import org.eclipse.jgit.lib.Repository;

/**
 * Git exception class.
 */
public class GitException extends RuntimeException {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -9119190921390161715L;

	private final Repository repository;

	/**
	 * Create a Git exception with a message and cause
	 *
	 * @param message
	 * @param cause
	 * @param repository
	 */
	public GitException(final String message, final Throwable cause,
			final Repository repository) {
		super(message, cause);
		this.repository = repository;
	}

	/**
	 * Create a Git exception with a cause
	 *
	 * @param cause
	 * @param repository
	 */
	public GitException(final Throwable cause, final Repository repository) {
		this(null, cause, repository);
	}

	/**
	 * Create a Git exception with a message
	 *
	 * @param message
	 * @param repository
	 */
	public GitException(final String message, final Repository repository) {
		this(message, null, repository);
	}

	/**
	 * Create a Git exception
	 *
	 * @param repository
	 */
	public GitException(final Repository repository) {
		this(null, null, repository);
	}

	/**
	 * Get repository where exception occurred
	 *
	 * @return repository
	 */
	public Repository getRepository() {
		return repository;
	}
}
