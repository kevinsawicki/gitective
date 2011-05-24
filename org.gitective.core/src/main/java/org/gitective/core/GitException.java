/******************************************************************************
 *  Copyright (c) 2011, Kevin Sawicki <kevinsawicki@gmail.com>
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *****************************************************************************/
package org.gitective.core;

/**
 * Git exception class.
 */
public class GitException extends RuntimeException {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -9119190921390161715L;

	/**
	 * Create a Git exception with a message and cause
	 * 
	 * @param message
	 * @param cause
	 */
	public GitException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Create a Git exception with a cause
	 * 
	 * @param cause
	 */
	public GitException(Throwable cause) {
		super(cause);
	}

	/**
	 * Create a Git exception with a message
	 * 
	 * @param message
	 */
	public GitException(String message) {
		super(message);
	}

}
