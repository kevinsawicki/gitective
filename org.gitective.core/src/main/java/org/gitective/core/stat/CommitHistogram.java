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
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.revwalk.RevCommit;

/**
 * Commit histogram class
 */
public class CommitHistogram implements Serializable {

	/** serialVersionUID */
	private static final long serialVersionUID = 5626774395562827167L;

	private final Map<String, UserCommitActivity> committers = new HashMap<String, UserCommitActivity>();
	private final Map<String, UserCommitActivity> authors = new HashMap<String, UserCommitActivity>();

	/**
	 * Include commit in histogram
	 * 
	 * @param commit
	 * @return this histogram
	 */
	public CommitHistogram include(final RevCommit commit) {
		final PersonIdent author = commit.getAuthorIdent();
		if (author != null)
			include(commit, author, authors);
		final PersonIdent committer = commit.getCommitterIdent();
		if (committer != null)
			include(commit, committer, committers);
		return this;
	}

	private CommitHistogram include(final RevCommit commit,
			final PersonIdent user, final Map<String, UserCommitActivity> users) {
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
	 * Get author activity by given email address
	 * 
	 * @param email
	 * @return activity or null if none
	 */
	public UserCommitActivity author(final String email) {
		return email != null ? authors.get(email) : null;
	}

	/**
	 * Convert given collection to array and sort by comparator
	 * 
	 * @param users
	 * @param comparator
	 * @return sorted array
	 */
	protected UserCommitActivity[] toArray(final Collection<UserCommitActivity> users,
			final Comparator<UserCommitActivity> comparator) {
		final UserCommitActivity[] activity = users.toArray(new UserCommitActivity[users
				.size()]);
		if (comparator != null)
			Arrays.sort(activity, comparator);
		return activity;
	}

	/**
	 * Get all author activity sorted by optional comparator
	 * 
	 * @param comparator
	 * @return non-null but possibly empty array of author activity
	 */
	public UserCommitActivity[] authors(final Comparator<UserCommitActivity> comparator) {
		return toArray(authors.values(), comparator);
	}

	/**
	 * Get all author activity
	 * 
	 * @see #authors(Comparator)
	 * @return non-null but possibly empty array of author activity
	 */
	public UserCommitActivity[] authors() {
		return authors(null);
	}

	/**
	 * Get committer activity for given email address
	 * 
	 * @param email
	 * @return activity or null if none
	 */
	public UserCommitActivity committer(final String email) {
		return email != null ? committers.get(email) : null;
	}

	/**
	 * Get all committer activity sorted by optional comparator
	 * 
	 * @param comparator
	 * @return non-null but possibly empty array of committer activity
	 */
	public UserCommitActivity[] committers(final Comparator<UserCommitActivity> comparator) {
		return toArray(committers.values(), comparator);
	}

	/**
	 * Get all committer activity
	 * 
	 * @see #committers(Comparator)
	 * @return non-null but possibly empty array of author activity
	 */
	public UserCommitActivity[] committers() {
		return committers(null);
	}
}
