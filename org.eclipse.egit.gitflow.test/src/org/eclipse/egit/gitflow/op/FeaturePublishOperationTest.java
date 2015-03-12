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
import org.eclipse.jgit.lib.RefUpdate;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.PushResult;
import org.junit.Test;

public class FeaturePublishOperationTest extends AbstractDualRepositoryTestCase {
	@Test
	public void testFeaturePublish() throws Exception {
		new FeatureStartOperation(repository2.getRepository(), MY_FEATURE).execute(null);
		RevCommit branchCommit = repository2.createInitialCommit("testFeaturePublish");
		FeaturePublishOperation featurePublishOperation = new FeaturePublishOperation(repository2.getRepository(), 0);
		featurePublishOperation.execute(null);
		PushOperationResult result = featurePublishOperation.getOperationResult();

		assertTrue(result.isSuccessfulConnection(repository1.getUri()));
		PushResult pushResult = result.getPushResult(repository1.getUri());
		assertEquals(RefUpdate.Result.NEW, pushResult.getTrackingRefUpdates().iterator().next().getResult());

		assertCommitArrivedAtRemote(branchCommit, repository1.getRepository());

		new FeatureFinishOperation(repository1.getRepository(), MY_FEATURE);
	}
}
