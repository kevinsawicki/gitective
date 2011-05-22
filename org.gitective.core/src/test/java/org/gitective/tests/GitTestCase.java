/******************************************************************************
 *  Copyright (c) 2011 Kevin Sawicki <kevinsawicki@gmail.com>
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *****************************************************************************/
package org.gitective.tests;

import java.io.File;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.Date;

import junit.framework.TestCase;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.revwalk.RevCommit;

/**
 * Base test case with utilities for common Git operations performed during
 * testing
 */
public abstract class GitTestCase extends TestCase {

	/**
	 * Test repository .git directory
	 */
	protected File testRepo;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		String tmpDir = System.getProperty("java.io.tmpdir");
		assertNotNull("java.io.tmpdir was null", tmpDir);
		File dir = new File(tmpDir, "git-test-case-" + System.nanoTime());
		assertTrue(dir.mkdir());

		Git.init().setDirectory(dir).setBare(false).call();
		testRepo = new File(dir, Constants.DOT_GIT);

		setUser(new PersonIdent("Test User", "user@test.com"));
	}

	/**
	 * Set user config
	 * 
	 * @param person
	 * @throws Exception
	 */
	protected void setUser(PersonIdent person) throws Exception {
		StoredConfig config = Git.open(testRepo).getRepository().getConfig();
		config.setString("user", null, "email", person.getEmailAddress());
		config.setString("user", null, "name", person.getName());
		config.save();
	}

	/**
	 * Create branch with name and checkout
	 * 
	 * @param name
	 * @return branch ref
	 * @throws Exception
	 */
	protected Ref branch(String name) throws Exception {
		Git git = Git.open(testRepo);
		git.branchCreate().setName(name).call();
		Ref ref = git.checkout().setName(name).call();
		assertNotNull(ref);
		return ref;
	}

	/**
	 * Add file to test repository
	 * 
	 * @param path
	 * @param content
	 * @return commit
	 * @throws Exception
	 */
	protected RevCommit add(String path, String content) throws Exception {
		String message = MessageFormat.format("Committing {0} at {1}", path,
				new Date());
		return add(path, content, message);
	}

	/**
	 * Add file to test repository
	 * 
	 * @param path
	 * @param content
	 * @param message
	 * @return commit
	 * @throws Exception
	 */
	protected RevCommit add(String path, String content, String message)
			throws Exception {
		File file = new File(testRepo.getParentFile(), path);
		if (!file.getParentFile().exists())
			assertTrue(file.getParentFile().mkdirs());
		if (!file.exists())
			assertTrue(file.createNewFile());
		PrintWriter writer = new PrintWriter(file);
		try {
			writer.print(content);
		} finally {
			writer.close();
		}
		Git git = Git.open(testRepo);
		git.add().addFilepattern(path).call();
		RevCommit commit = git.commit().setOnly(path).setMessage(message)
				.call();
		assertNotNull(commit);
		return commit;
	}
}
