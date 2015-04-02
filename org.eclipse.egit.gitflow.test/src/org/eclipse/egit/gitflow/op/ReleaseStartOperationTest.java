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

import org.eclipse.egit.gitflow.GitFlowRepository;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Test;

public class ReleaseStartOperationTest extends AbstractGitFlowOperationTest {
	@Test
	public void testReleaseBranchCreated() throws Exception {
		testRepository.createInitialCommit("testReleaseStart\n\nfirst commit\n");

		Repository repository = testRepository.getRepository();
		new InitOperation(repository).execute(null);
		GitFlowRepository gfRepo = new GitFlowRepository(repository);

		new ReleaseStartOperation(gfRepo, MY_RELEASE).execute(null);

		assertEquals(gfRepo.getFullReleaseBranchName(MY_RELEASE), repository
				.getFullBranch());
	}

	@Test
	public void testReleaseStartWithContent() throws Exception {
		testRepository.createInitialCommit("testReleaseStartWithContent\n\nfirst commit\n");

		Repository repository = testRepository.getRepository();
		new InitOperation(repository).execute(null);
		GitFlowRepository gfRepo = new GitFlowRepository(repository);

		testUtils.addFileToProject(project.getProject(), "folder1/file1.txt", "Hello world");
		testRepository.connect(project.getProject());
		testRepository.trackAllFiles(project.getProject());
		RevCommit developCommit = testRepository.commit("Initial commit");

		new ReleaseStartOperation(gfRepo, MY_RELEASE).execute(null);

		RevCommit releaseHead = gfRepo.findHead(gfRepo.getReleaseBranchName(MY_RELEASE));
		assertEquals(developCommit, releaseHead);
	}
}
