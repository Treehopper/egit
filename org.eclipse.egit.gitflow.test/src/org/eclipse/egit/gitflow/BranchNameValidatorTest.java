/*******************************************************************************
 * Copyright (C) 2015, Max Hohenegger <eclipse@hohenegger.eu>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.egit.gitflow;

import static org.eclipse.egit.gitflow.BranchNameValidator.featureExists;
import static org.eclipse.egit.gitflow.BranchNameValidator.isBranchNameValid;
import static org.eclipse.egit.gitflow.BranchNameValidator.releaseExists;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.egit.gitflow.op.AbstractGitFlowOperationTest;
import org.eclipse.egit.gitflow.op.FeatureStartOperation;
import org.eclipse.egit.gitflow.op.InitOperation;
import org.eclipse.egit.gitflow.op.ReleaseStartOperation;
import org.eclipse.jgit.lib.Repository;
import org.junit.Test;
public class BranchNameValidatorTest extends AbstractGitFlowOperationTest {

	@Test
	public void testFeatureExists() throws Exception {
		testRepository.createInitialCommit("testInitOperation\n\nfirst commit\n");

		Repository repository = testRepository.getRepository();
		new InitOperation(repository).execute(null);
		new FeatureStartOperation(repository, MY_FEATURE).execute(null);

		assertTrue(featureExists(repository, MY_FEATURE));
	}

	@Test
	public void testReleaseExists() throws Exception {
		testRepository.createInitialCommit("testInitOperation\n\nfirst commit\n");

		Repository repository = testRepository.getRepository();
		new InitOperation(repository).execute(null);
		new ReleaseStartOperation(repository, MY_RELEASE).execute(null);

		assertTrue(releaseExists(repository, MY_RELEASE));
	}

	@Test
	public void testBranchNotExists() throws Exception {
		testRepository.createInitialCommit("testInitOperation\n\nfirst commit\n");

		Repository repository = testRepository.getRepository();
		new InitOperation(repository).execute(null);
		new ReleaseStartOperation(repository, MY_RELEASE).execute(null);

		assertFalse(releaseExists(repository, "notThere"));
	}

	@Test
	public void testBranchNameValid() throws Exception {
		assertTrue(isBranchNameValid(MY_RELEASE));
		assertTrue(isBranchNameValid(MY_FEATURE));
		assertFalse(isBranchNameValid("/"));
		assertFalse(isBranchNameValid(""));
	}
}