/*******************************************************************************
 * Copyright (C) 2015, Max Hohenegger <eclipse@hohenegger.eu>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.egit.gitflow.op;

import static org.junit.Assert.assertEquals;

import org.eclipse.egit.core.op.BranchOperation;
import org.eclipse.egit.gitflow.GitFlowRepository;
import org.eclipse.egit.gitflow.WrongGitFlowStateException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Test;

@SuppressWarnings("restriction")
public class ReleaseFinishOperationTest extends AbstractGitFlowOperationTest {
	@Test
	public void testReleaseFinish() throws Exception {
		testRepository.createInitialCommit("testReleaseFinish\n\nfirst commit\n");

		Repository repository = testRepository.getRepository();
		new InitOperation(repository).execute(null);
		GitFlowRepository gfRepo = new GitFlowRepository(repository);

		new ReleaseStartOperation(gfRepo, MY_RELEASE).execute(null);

		RevCommit branchCommit = testRepository.createInitialCommit("testReleaseFinish\n\nbranch commit\n");

		new ReleaseFinishOperation(gfRepo).execute(null);

		assertEquals(gfRepo.getDevelopFull(), repository.getFullBranch());


		String branchName = gfRepo.getReleaseBranchName(MY_RELEASE);

		// tag created?
		assertEquals(branchCommit, findCommitForTag(repository, MY_RELEASE));

		// branch removed?
		assertEquals(findBranch(repository, branchName), null);

		RevCommit developHead = gfRepo.findHead();
		assertEquals(branchCommit, developHead);

		RevCommit masterHead = gfRepo.findHead(MY_MASTER);
		assertEquals(branchCommit, masterHead);

	}

	@Test(expected = WrongGitFlowStateException.class)
	public void testReleaseFinishFail() throws Exception {
		testRepository.createInitialCommit("testReleaseFinishFail\n\nfirst commit\n");

		Repository repository = testRepository.getRepository();
		new InitOperation(repository).execute(null);
		GitFlowRepository gfRepo = new GitFlowRepository(repository);

		new ReleaseStartOperation(gfRepo, MY_RELEASE).execute(null);

		new BranchOperation(repository, gfRepo.getDevelop()).execute(null);

		new ReleaseFinishOperation(gfRepo).execute(null);
	}
}
