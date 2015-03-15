/*******************************************************************************
 * Copyright (C) 2015, Max Hohenegger <eclipse@hohenegger.eu>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.egit.gitflow.op;

import static org.eclipse.egit.gitflow.GitFlowDefaults.DEVELOP;
import static org.eclipse.egit.gitflow.GitFlowDefaults.FEATURE_PREFIX;
import static org.eclipse.egit.gitflow.GitFlowDefaults.HOTFIX_PREFIX;
import static org.eclipse.egit.gitflow.GitFlowDefaults.MASTER;
import static org.eclipse.egit.gitflow.GitFlowDefaults.RELEASE_PREFIX;
import static org.junit.Assert.assertEquals;

import org.eclipse.egit.core.op.BranchOperation;
import org.eclipse.egit.gitflow.GitFlowRepository;
import org.eclipse.jgit.lib.Repository;
import org.junit.Test;

@SuppressWarnings("restriction")
public class FeatureCheckoutOperationTest extends AbstractFeatureOperationTest {
	@Test
	public void testFeatureCheckout() throws Exception {
		testRepository.createInitialCommit("testFeatureCheckout\n\nfirst commit\n");

		Repository repository = testRepository.getRepository();
		new InitOperation(repository, DEVELOP, MASTER, FEATURE_PREFIX, RELEASE_PREFIX, HOTFIX_PREFIX).execute(null);
		GitFlowRepository gfRepo = new GitFlowRepository(repository);

		new FeatureStartOperation(gfRepo, MY_FEATURE).execute(null);
		new BranchOperation(repository, gfRepo.getDevelop()).execute(null);

		new FeatureCheckoutOperation(gfRepo, MY_FEATURE).execute(null);

		assertEquals(gfRepo.getFullFeatureBranchName(MY_FEATURE), repository
				.getFullBranch());
	}
}
