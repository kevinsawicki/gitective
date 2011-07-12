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
package org.gitective.core.stat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;

import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.revwalk.RevCommit;
import org.gitective.core.GitException;

/**
 * Commit user activity class
 */
public class UserCommitActivity implements Serializable {

	/** serialVersionUID */
	private static final long serialVersionUID = 3567351283712945310L;

	/**
	 * Default size of expected commits
	 */
	public static final int SIZE = 16;

	/**
	 * Default growth percentage of expected commits
	 */
	public static final int GROWTH = 10;

	private final String name;
	private final String email;
	private int index;
	private byte[][] commits;
	private long[] times;
	private long first = Long.MAX_VALUE;
	private long last = Long.MIN_VALUE;

	private final transient ByteArrayOutputStream stream = new ByteArrayOutputStream(
			Constants.OBJECT_ID_LENGTH);

	/**
	 * Create user activity for given name and email
	 * 
	 * @param name
	 * @param email
	 */
	public UserCommitActivity(final String name, final String email) {
		this.name = name;
		this.email = email;
		commits = new byte[SIZE][];
		times = new long[SIZE];
	}

	/**
	 * Get name of user
	 * 
	 * @return name
	 */
	public String name() {
		return name;
	}

	/**
	 * Get email address of user
	 * 
	 * @return email
	 */
	public String email() {
		return email;
	}

	/**
	 * Include given commit in activity
	 * 
	 * @param commit
	 * @param user
	 * @return this activity
	 */
	public UserCommitActivity include(final RevCommit commit,
			final PersonIdent user) {
		final long when = user.getWhen().getTime();
		stream.reset();
		try {
			commit.copyRawTo(stream);
		} catch (IOException e) {
			throw new GitException(e);
		}
		if (index == commits.length) {
			int newSize = commits.length;
			// Grow arrays by either GROWTH percentage or SIZE value, whichever
			// is higher
			newSize += Math.max(SIZE, (int) (newSize / GROWTH));
			commits = Arrays.copyOf(commits, newSize);
			times = Arrays.copyOf(times, newSize);
		}
		commits[index] = stream.toByteArray();
		times[index] = when;
		index++;

		if (when < first)
			first = when;
		if (when > last)
			last = when;
		return this;
	}

	/**
	 * Get commit times
	 * 
	 * @return non-null but possibly empty array
	 */
	public long[] times() {
		return Arrays.copyOf(times, index);
	}

	/**
	 * Get number of commits
	 * 
	 * @return commit count
	 */
	public int count() {
		return index;
	}

	/**
	 * Get id of first commit
	 * 
	 * @return commit id
	 */
	public ObjectId first() {
		return index > 0 ? ObjectId.fromRaw(commits[index - 1]) : null;
	}

	/**
	 * Get id of latest commit
	 * 
	 * @return commit id
	 */
	public ObjectId last() {
		return index > 0 ? ObjectId.fromRaw(commits[0]) : null;
	}

	/**
	 * Get time of earliest commit
	 * 
	 * @return time
	 */
	public long earliest() {
		return index > 0 ? first : 0;
	}

	/**
	 * Get time of latest commit
	 * 
	 * @return time
	 */
	public long latest() {
		return index > 0 ? last : 0;
	}
}
