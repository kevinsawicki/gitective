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

import static org.eclipse.jgit.lib.Constants.DEFAULT_REMOTE_NAME;
import static org.eclipse.jgit.lib.Constants.R_HEADS;
import static org.eclipse.jgit.lib.Constants.R_NOTES;
import static org.eclipse.jgit.lib.Constants.R_REMOTES;
import static org.eclipse.jgit.lib.Constants.R_TAGS;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jgit.api.LsRemoteCommand;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.RemoteConfig;

/**
 * Utilities for dealing with Git repositories.
 * <p>
 * This class provides helpers for getting the branches, tags, and note
 * references in a repository.
 * <p>
 * This class also provides helpers for knowing which references differ between
 * a local and remote repository.
 */
public abstract class RepositoryUtils {

	/**
	 * Wrapper class for a remote and local ref that are different
	 */
	public static class RefDiff {

		private final Ref local;

		private final Ref remote;

		/**
		 * Create ref diff
		 *
		 * @param local
		 * @param remote
		 */
		protected RefDiff(final Ref local, final Ref remote) {
			this.local = local;
			this.remote = remote;
		}

		/**
		 * Get local ref. This will be null if the ref only exists remotely.
		 *
		 * @return ref
		 */
		public Ref getLocal() {
			return local;
		}

		/**
		 * Get non-null remote ref
		 *
		 * @return ref
		 */
		public Ref getRemote() {
			return remote;
		}
	}

	/**
	 * Get the refs with prefix in repository
	 *
	 * @param repository
	 * @param prefix
	 * @return collection of refs
	 */
	protected static Collection<Ref> getRefs(final Repository repository,
			final String prefix) {
		try {
			return repository.getRefDatabase().getRefs(prefix).values();
		} catch (IOException e) {
			throw new GitException(e, repository);
		}
	}

	/**
	 * Get all the note references in the given repository.
	 *
	 * @param repository
	 * @return non-null but possibly empty array of note references
	 */
	public static Collection<String> getNoteRefs(final Repository repository) {
		if (repository == null)
			throw new IllegalArgumentException(
					Assert.formatNotNull("Repository"));

		final Collection<Ref> refs = getRefs(repository, R_NOTES);
		final List<String> notes = new ArrayList<String>(refs.size());
		for (Ref ref : refs)
			notes.add(ref.getName());
		return notes;
	}

	/**
	 * Get all the local and remote tracking branch references in the given
	 * repository.
	 *
	 * @param repository
	 * @return non-null but possibly array of branch reference names
	 */
	public static Collection<String> getBranches(final Repository repository) {
		if (repository == null)
			throw new IllegalArgumentException(
					Assert.formatNotNull("Repository"));

		final List<String> branches = new ArrayList<String>();
		for (Ref ref : getRefs(repository, R_HEADS))
			branches.add(ref.getName());
		for (Ref ref : getRefs(repository, R_REMOTES))
			branches.add(ref.getName());
		return branches;
	}

	/**
	 * Get all the tag references in the given repository.
	 *
	 * @param repository
	 * @return non-null but possibly array of branch reference names
	 */
	public static Collection<String> getTags(final Repository repository) {
		if (repository == null)
			throw new IllegalArgumentException(
					Assert.formatNotNull("Repository"));

		return repository.getTags().keySet();
	}

	/**
	 * List the origin remote references and return all remote references that
	 * are missing locally or have a different remote object id than the local
	 * reference.
	 *
	 * @param repository
	 * @return non-null but possibly empty collection of {@link RefDiff}
	 */
	public static Collection<RefDiff> diffOriginRefs(final Repository repository) {
		return diffRemoteRefs(repository, DEFAULT_REMOTE_NAME);
	}

	/**
	 * Does the given remote exist in the repository?
	 *
	 * @param repository
	 * @param remote
	 * @return true if exists, false otherwise
	 * @throws URISyntaxException
	 */
	protected static boolean hasRemote(final Repository repository,
			final String remote) throws URISyntaxException {
		final RemoteConfig config = new RemoteConfig(repository.getConfig(),
				remote);
		return !config.getURIs().isEmpty() || !config.getPushURIs().isEmpty();
	}

	/**
	 * List remote references and return all remote references that are missing
	 * locally or have a different remote object id than the local reference.
	 *
	 * @param repository
	 * @param remote
	 * @return non-null but possibly collection of {@link RefDiff}
	 */
	public static Collection<RefDiff> diffRemoteRefs(
			final Repository repository, final String remote) {
		if (repository == null)
			throw new IllegalArgumentException(
					Assert.formatNotNull("Repository"));
		if (remote == null)
			throw new IllegalArgumentException(Assert.formatNotNull("Remote"));
		if (remote.length() == 0)
			throw new IllegalArgumentException(Assert.formatNotEmpty("Remote"));

		final LsRemoteCommand lsRemote = new LsRemoteCommand(repository);
		lsRemote.setRemote(remote);
		try {
			final Collection<Ref> remoteRefs = lsRemote.call();
			final List<RefDiff> diffs = new ArrayList<RefDiff>();
			if (hasRemote(repository, remote)) {
				final String refPrefix = R_REMOTES + remote + "/";
				for (Ref remoteRef : remoteRefs) {
					String name = remoteRef.getName();
					if (name.startsWith(R_HEADS))
						name = refPrefix + name.substring(R_HEADS.length());
					else if (!name.startsWith(R_TAGS))
						name = refPrefix + name;
					final Ref localRef = repository.getRef(name);
					if (localRef == null
							|| !remoteRef.getObjectId().equals(
									localRef.getObjectId()))
						diffs.add(new RefDiff(localRef, remoteRef));
				}
			} else
				for (Ref remoteRef : remoteRefs) {
					final Ref localRef = repository.getRef(remoteRef.getName());
					if (localRef == null
							|| !remoteRef.getObjectId().equals(
									localRef.getObjectId()))
						diffs.add(new RefDiff(localRef, remoteRef));
				}
			return diffs;
		} catch (Exception e) {
			throw new GitException(e, repository);
		}
	}

	/**
	 * Map names to e-mail addresses for all given {@link PersonIdent} instances
	 *
	 * @see PersonIdent#getName()
	 * @see PersonIdent#getEmailAddress()
	 * @param persons
	 * @return non-null but possibly empty map of names to e-mail addresses
	 */
	public static Map<String, Set<String>> mapNamesToEmails(
			final Collection<PersonIdent> persons) {
		if (persons == null || persons.isEmpty())
			return Collections.emptyMap();

		final Map<String, Set<String>> namesToEmails = new HashMap<String, Set<String>>(
				persons.size());
		for (PersonIdent person : persons) {
			Set<String> emails = namesToEmails.get(person.getName());
			if (emails == null) {
				emails = new HashSet<String>(2);
				namesToEmails.put(person.getName(), emails);
			}
			emails.add(person.getEmailAddress());
		}
		return namesToEmails;
	}

	/**
	 * Map e-mail addresses to names for all given {@link PersonIdent} instances
	 *
	 * @see PersonIdent#getName()
	 * @see PersonIdent#getEmailAddress()
	 * @param persons
	 * @return non-null but possibly empty map of e-mail addresses to names
	 */
	public static Map<String, Set<String>> mapEmailsToNames(
			final Collection<PersonIdent> persons) {
		if (persons == null || persons.isEmpty())
			return Collections.emptyMap();

		final Map<String, Set<String>> emailsToNames = new HashMap<String, Set<String>>(
				persons.size());
		for (PersonIdent person : persons) {
			Set<String> names = emailsToNames.get(person.getEmailAddress());
			if (names == null) {
				names = new HashSet<String>(2);
				emailsToNames.put(person.getEmailAddress(), names);
			}
			names.add(person.getName());
		}
		return emailsToNames;
	}
}
