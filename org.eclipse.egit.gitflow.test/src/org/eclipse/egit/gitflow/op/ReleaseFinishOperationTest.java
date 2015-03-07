/*******************************************************************************
 * Copyright (C) 2015, Max Hohenegger <eclipse@hohenegger.eu>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.egit.gitflow.op;

import static org.junit.Assert.*;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Test;

public class ReleaseFinishOperationTest extends AbstractReleaseOperationTest {
	@Test
	public void testReleaseFinish() throws Exception {
		testRepository.createInitialCommit("testReleaseFinish\n\nfirst commit\n");

		Repository repository = testRepository.getRepository();
		new InitOperation(repository).execute(null);

		new ReleaseStartOperation(repository, MY_RELEASE).execute(null);

		RevCommit branchCommit = testRepository.createInitialCommit("testReleaseFinish\n\nbranch commit\n");

		new ReleaseFinishOperation(repository).execute(null);

		assertEquals(DEVELOP_FULL, repository.getFullBranch());


		String branchName = getReleaseBranchName(MY_RELEASE);

		// tag created?
		assertEquals(branchCommit, findCommitForTag(repository, MY_RELEASE));

		// branch removed?
		assertEquals(findBranch(repository, branchName), null);

		RevCommit developHead = findHead(repository);
		assertEquals(branchCommit, developHead);

		RevCommit masterHead = findHead(repository, "master");
		assertEquals(branchCommit, masterHead);

	}
}
