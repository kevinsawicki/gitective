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

import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.errors.StopWalkException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.revwalk.filter.RevFilter;
import org.gitective.core.GitException;
import org.gitective.core.filter.commit.AndCommitFilter;
import org.gitective.core.filter.commit.CommitCountFilter;
import org.gitective.core.filter.commit.CommitFilter;
import org.gitective.core.service.CommitFinder;
import org.junit.Test;

/**
 * Unit tests of {@link CommitFinder}
 */
public class CommitFinderTest extends GitTestCase {

	/**
	 * Test matcher throwing an {@link IOException} when visiting a commit
	 * 
	 * @throws Exception
	 */
	@Test
	public void matcherThrowsIOException() throws Exception {
		add("test.txt", "content");
		CommitFinder finder = new CommitFinder(testRepo);
		final IOException exception = new IOException("message");
		finder.setMatcher(new CommitFilter() {

			public boolean include(RevWalk walker, RevCommit cmit)
					throws IOException {
				throw exception;
			}

			public RevFilter clone() {
				return this;
			}
		});
		try {
			finder.find();
			fail("Exception not thrown by matcher filter");
		} catch (GitException e) {
			assertNotNull(e);
			assertEquals(exception, e.getCause());
		}
	}

	/**
	 * Test matcher throwing a {@link StopWalkException} and it being suppressed
	 * and the walk stopping
	 * 
	 * @throws Exception
	 */
	@Test
	public void suppressedStopWalkExceptionThrownByMatcher() throws Exception {
		add("test.txt", "content");
		CommitFinder finder = new CommitFinder(testRepo);
		CommitCountFilter count = new CommitCountFilter();
		finder.setMatcher(new AndCommitFilter(new CommitFilter() {

			public boolean include(RevWalk walker, RevCommit cmit)
					throws IOException {
				throw StopWalkException.INSTANCE;
			}

			public RevFilter clone() {
				return this;
			}
		}, count));
		finder.find();
		assertEquals(0, count.getCount());
	}
}
