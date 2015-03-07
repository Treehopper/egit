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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.egit.core.op.BranchOperation;
import org.eclipse.jgit.lib.Repository;
import org.junit.Test;

public class FeatureStartOperationTest extends AbstractFeatureOperationTest {
	@Test
	public void testFeatureStart() throws Exception {
		testRepository.createInitialCommit("testFeatureStart\n\nfirst commit\n");

		Repository repository = testRepository.getRepository();
		new InitOperation(repository).execute(null);

		new FeatureStartOperation(repository, MY_FEATURE).execute(null);

		assertEquals(getFullFeatureBranchName(MY_FEATURE), repository
				.getFullBranch());
	}

	@Test(expected = CoreException.class)
	public void testFeatureStartFail() throws Exception {
		testRepository.createInitialCommit("testFeatureStart\n\nfirst commit\n");

		Repository repository = testRepository.getRepository();
		new InitOperation(repository).execute(null);

		BranchOperation branchOperation = new BranchOperation(repository, MY_MASTER);
		branchOperation.execute(null);

		new FeatureStartOperation(repository, MY_FEATURE).execute(null);
	}
}
