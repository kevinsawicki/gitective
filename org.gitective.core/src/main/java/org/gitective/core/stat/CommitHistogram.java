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

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.revwalk.RevCommit;

/**
 * Commit histogram class that stores and provides commit activity by user
 * e-mail address.
 */
public class CommitHistogram implements Serializable {

	/** serialVersionUID */
	private static final long serialVersionUID = 5626774395562827167L;

	/**
	 * User activity
	 */
	protected final Map<String, UserCommitActivity> users = new HashMap<String, UserCommitActivity>();

	/**
	 * Register commit under given user
	 * 
	 * @param commit
	 * @param user
	 * @return this histogram
	 */
	public CommitHistogram include(final RevCommit commit,
			final PersonIdent user) {
		final String email = user.getEmailAddress();
		UserCommitActivity activity = users.get(email);
		if (activity == null) {
			activity = new UserCommitActivity(user.getName(), email);
			users.put(email, activity);
		}
		activity.include(commit, user);
		return this;
	}

	/**
	 * Get user activity by given email address
	 * 
	 * @param email
	 * @return activity or null if none
	 */
	public UserCommitActivity getActivity(final String email) {
		return email != null ? users.get(email) : null;
	}

	/**
	 * Get all user activity sorted by optional comparator
	 * 
	 * @param comparator
	 * @return non-null but possibly empty array of user activity
	 */
	public UserCommitActivity[] getUserActivity(
			final Comparator<UserCommitActivity> comparator) {
		final UserCommitActivity[] activity = users.values().toArray(
				new UserCommitActivity[users.size()]);
		if (comparator != null)
			Arrays.sort(activity, comparator);
		return activity;
	}

	/**
	 * Get all user activity
	 * 
	 * @see #getUserActivity(Comparator)
	 * @return non-null but possibly empty array of user activity
	 */
	public UserCommitActivity[] getUserActivity() {
		return getUserActivity(null);
	}
}
