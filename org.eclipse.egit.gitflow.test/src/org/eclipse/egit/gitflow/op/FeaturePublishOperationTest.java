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
import static org.junit.Assert.assertTrue;

import org.eclipse.egit.core.op.PushOperationResult;
import org.eclipse.egit.gitflow.GitFlowRepository;
import org.eclipse.jgit.lib.RefUpdate;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.PushResult;
import org.junit.Test;

@SuppressWarnings("restriction")
public class FeaturePublishOperationTest extends AbstractDualRepositoryTestCase {
	@Test
	public void testFeaturePublish() throws Exception {
		GitFlowRepository gfRepo1 = new GitFlowRepository(repository1.getRepository());

		new InitOperation(repository2.getRepository()).execute(null);
		GitFlowRepository gfRepo2 = new GitFlowRepository(repository2.getRepository());

		new FeatureStartOperation(gfRepo2, MY_FEATURE).execute(null);
		RevCommit branchCommit = repository2.createInitialCommit("testFeaturePublish");
		FeaturePublishOperation featurePublishOperation = new FeaturePublishOperation(gfRepo2, 0);
		featurePublishOperation.execute(null);
		PushOperationResult result = featurePublishOperation.getOperationResult();

		assertTrue(result.isSuccessfulConnection(repository1.getUri()));
		PushResult pushResult = result.getPushResult(repository1.getUri());
		assertEquals(RefUpdate.Result.NEW, pushResult.getTrackingRefUpdates().iterator().next().getResult());

		assertCommitArrivedAtRemote(branchCommit, repository1.getRepository());

		new FeatureFinishOperation(gfRepo1, MY_FEATURE);
	}
}
