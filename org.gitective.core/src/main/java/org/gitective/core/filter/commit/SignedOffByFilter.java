/******************************************************************************
 *  Copyright (c) 2011 Kevin Sawicki <kevinsawicki@gmail.com>
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *****************************************************************************/
package org.gitective.core.filter.commit;

import java.text.MessageFormat;
import java.util.regex.Pattern;

import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.revwalk.filter.RevFilter;

/**
 * Commit message pattern filter that includes commit that have a Signed-off-by
 * line for a person.
 */
public class SignedOffByFilter extends CommitMessageFindFilter {

	/**
	 * Signed off by format
	 */
	public static final String SIGNED_OFF_BY = Constants.SIGNED_OFF_BY_TAG
			+ "{0} <{1}>";

	private PersonIdent person;

	/**
	 * Create a signed off by filter matching the person
	 * 
	 * @param person
	 *            must be non-null
	 */
	public SignedOffByFilter(PersonIdent person) {
		super(Pattern.quote(MessageFormat.format(SIGNED_OFF_BY,
				person.getName(), person.getEmailAddress())), Pattern.MULTILINE);
	}

	@Override
	public RevFilter clone() {
		return new SignedOffByFilter(person);
	}

}
