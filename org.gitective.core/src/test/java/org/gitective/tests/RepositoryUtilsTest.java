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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.RefUpdate;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.URIish;
import org.gitective.core.RepositoryUtils;
import org.gitective.core.RepositoryUtils.RefDiff;
import org.junit.Test;

/**
 * Unit tests of {@link RepositoryUtils}
 */
public class RepositoryUtilsTest extends GitTestCase {

	/**
	 * Test creating a {@link RepositoryUtils} anonymous class
	 */
	@Test
	public void constructor() {
		assertNotNull(new RepositoryUtils() {
		});
	}

	/**
	 * Test getting note refs for null repository
	 */
	@Test
	public void noteRefsForNullRepository() {
		try {
			RepositoryUtils.getNoteRefs(null);
		} catch (IllegalArgumentException e) {
			assertNotNull(e);
			assertNotNull(e.getMessage());
			assertTrue(e.getMessage().length() > 0);
		}
	}

	/**
	 * Get branches for null repository
	 */
	@Test(expected = IllegalArgumentException.class)
	public void branchesForNullRepository() {
		RepositoryUtils.getBranches(null);
	}

	/**
	 * Get branches for empty repository
	 *
	 * @throws Exception
	 */
	@Test
	public void branchesForEmptyRepository() throws Exception {
		Collection<String> branches = RepositoryUtils
				.getBranches(new FileRepository(testRepo));
		assertNotNull(branches);
		assertTrue(branches.isEmpty());
	}

	/**
	 * Get branches for repository
	 *
	 * @throws Exception
	 */
	@Test
	public void branchesForRepository() throws Exception {
		add("test.txt", "content");
		Collection<String> branches = RepositoryUtils
				.getBranches(new FileRepository(testRepo));
		assertNotNull(branches);
		assertFalse(branches.isEmpty());
		assertEquals(Constants.R_HEADS + Constants.MASTER, branches.iterator()
				.next());
	}

	/**
	 * Get tags for null repository
	 */
	@Test(expected = IllegalArgumentException.class)
	public void tagsForNullRepository() {
		RepositoryUtils.getTags(null);
	}

	/**
	 * Get tags for empty repository
	 *
	 * @throws Exception
	 */
	@Test
	public void tagsForEmptyRepository() throws Exception {
		Collection<String> branches = RepositoryUtils
				.getBranches(new FileRepository(testRepo));
		assertNotNull(branches);
		assertTrue(branches.isEmpty());
	}

	/**
	 * Get branches for repository
	 *
	 * @throws Exception
	 */
	@Test
	public void tagsForRepository() throws Exception {
		add("test.txt", "content");
		tag("v1");
		Collection<String> tags = RepositoryUtils.getTags(new FileRepository(
				testRepo));
		assertNotNull(tags);
		assertFalse(tags.isEmpty());
		assertEquals("v1", tags.iterator().next());
	}

	/**
	 * Test getting note refs for empty repository
	 *
	 * @throws Exception
	 */
	@Test
	public void noteRefsForEmptyRepository() throws Exception {
		Collection<String> noteRefs = RepositoryUtils
				.getNoteRefs(new FileRepository(testRepo));
		assertNotNull(noteRefs);
		assertTrue(noteRefs.isEmpty());
	}

	/**
	 * Test no remote changes
	 *
	 * @throws Exception
	 */
	@Test
	public void noRemoteChanges() throws Exception {
		add("test.txt", "content");
		Repository repo = new FileRepository(testRepo);
		Collection<RefDiff> diffs = RepositoryUtils.diffRemoteRefs(repo,
				testRepo.toURI().toString());
		assertNotNull(diffs);
		assertTrue(diffs.isEmpty());
	}

	/**
	 * Test one remote change using URI
	 *
	 * @throws Exception
	 */
	@Test
	public void oneRemoteChangeUsingUri() throws Exception {
		RevCommit commit1 = add("test.txt", "content");
		File repo2 = initRepo();
		RevCommit commit2 = add(repo2, "test2.txt", "content2.txt");
		Repository repo = new FileRepository(testRepo);
		Collection<RefDiff> diffs = RepositoryUtils.diffRemoteRefs(repo, repo2
				.toURI().toString());
		assertNotNull(diffs);
		assertFalse(diffs.isEmpty());
		assertNotNull(diffs.iterator().next().getLocal());
		assertNotNull(diffs.iterator().next().getRemote());
		assertEquals(commit1, diffs.iterator().next().getLocal().getObjectId());
		assertEquals(commit2, diffs.iterator().next().getRemote().getObjectId());
	}

	/**
	 * Test one origin change
	 *
	 *
	 * @throws Exception
	 */
	@Test
	public void oneOriginChange() throws Exception {
		RevCommit commit1 = add("test.txt", "content");
		File repo2 = initRepo();
		RevCommit commit2 = add(repo2, "test2.txt", "content2.txt");
		Repository repo = new FileRepository(testRepo);
		RefUpdate originMaster = repo.updateRef(Constants.R_REMOTES
				+ Constants.DEFAULT_REMOTE_NAME + "/" + Constants.MASTER);
		originMaster.setNewObjectId(commit1);
		originMaster.forceUpdate();
		RemoteConfig config = new RemoteConfig(repo.getConfig(),
				Constants.DEFAULT_REMOTE_NAME);
		config.addURI(new URIish(repo2.toURI().toString()));
		config.update(repo.getConfig());
		Collection<RefDiff> diffs = RepositoryUtils.diffOriginRefs(repo);
		assertNotNull(diffs);
		assertFalse(diffs.isEmpty());
		assertNotNull(diffs.iterator().next().getLocal());
		assertNotNull(diffs.iterator().next().getRemote());
		assertEquals(commit1, diffs.iterator().next().getLocal().getObjectId());
		assertEquals(commit2, diffs.iterator().next().getRemote().getObjectId());
	}

	/**
	 * Test remote changes with null repository
	 *
	 * @throws Exception
	 */
	@Test(expected = IllegalArgumentException.class)
	public void noRemoteChangesNullRepository() throws Exception {
		RepositoryUtils.diffRemoteRefs(null, "");
	}

	/**
	 * Test remote changes with null remote
	 *
	 * @throws Exception
	 */
	@Test(expected = IllegalArgumentException.class)
	public void noRemoteChangesNullRemote() throws Exception {
		RepositoryUtils.diffRemoteRefs(new FileRepository(testRepo), null);
	}

	/**
	 * Test remote changes with empty remote
	 *
	 * @throws Exception
	 */
	@Test(expected = IllegalArgumentException.class)
	public void noRemoteChangesEmptyRemote() throws Exception {
		RepositoryUtils.diffRemoteRefs(new FileRepository(testRepo), "");
	}

	/**
	 * Test mapping names to emails
	 */
	@Test
	public void mapNamesToEmail() {
		assertNotNull(RepositoryUtils.mapNamesToEmails(null));
		assertEquals(0, RepositoryUtils.mapNamesToEmails(null).size());
		assertNotNull(RepositoryUtils.mapNamesToEmails(Collections
				.<PersonIdent> emptyList()));
		assertEquals(
				0,
				RepositoryUtils.mapNamesToEmails(
						Collections.<PersonIdent> emptyList()).size());

		PersonIdent person1 = new PersonIdent("a", "b1");
		PersonIdent person2 = new PersonIdent("a", "b2");
		PersonIdent person3 = new PersonIdent("b", "c1");
		PersonIdent person4 = new PersonIdent(null, "c2");
		PersonIdent person5 = new PersonIdent("", "c2");
		PersonIdent person6 = new PersonIdent("c", null);
		Map<String, Set<String>> mapped = RepositoryUtils
				.mapNamesToEmails(Arrays.asList(person1, person2, person3,
						person4, person5, person6));
		assertNotNull(mapped);
		assertFalse(mapped.isEmpty());
		assertEquals(3, mapped.size());
		assertNotNull(mapped.get("a"));
		assertEquals(2, mapped.get("a").size());
		assertNotNull(mapped.get("b"));
		assertEquals(1, mapped.get("b").size());
		assertNotNull(mapped.get("c"));
		assertEquals(0, mapped.get("c").size());
		assertTrue(mapped.get("a").contains("b1"));
		assertTrue(mapped.get("a").contains("b2"));
		assertTrue(mapped.get("b").contains("c1"));
	}

	/**
	 * Test mapping names to emails
	 */
	@Test
	public void mapEmailsToNames() {
		assertNotNull(RepositoryUtils.mapEmailsToNames(null));
		assertEquals(0, RepositoryUtils.mapEmailsToNames(null).size());
		assertNotNull(RepositoryUtils.mapEmailsToNames(Collections
				.<PersonIdent> emptyList()));
		assertEquals(
				0,
				RepositoryUtils.mapEmailsToNames(
						Collections.<PersonIdent> emptyList()).size());

		PersonIdent person1 = new PersonIdent("b1", "a");
		PersonIdent person2 = new PersonIdent("b2", "a");
		PersonIdent person3 = new PersonIdent("c1", "b");
		PersonIdent person4 = new PersonIdent("c2", null);
		PersonIdent person5 = new PersonIdent("c2", "");
		PersonIdent person6 = new PersonIdent(null, "c");
		Map<String, Set<String>> mapped = RepositoryUtils
				.mapEmailsToNames(Arrays.asList(person1, person2, person3,
						person4, person5, person6));
		assertNotNull(mapped);
		assertFalse(mapped.isEmpty());
		assertEquals(3, mapped.size());
		assertNotNull(mapped.get("a"));
		assertEquals(2, mapped.get("a").size());
		assertNotNull(mapped.get("b"));
		assertEquals(1, mapped.get("b").size());
		assertNotNull(mapped.get("c"));
		assertEquals(0, mapped.get("c").size());
		assertTrue(mapped.get("a").contains("b1"));
		assertTrue(mapped.get("a").contains("b2"));
		assertTrue(mapped.get("b").contains("c1"));
	}
}
