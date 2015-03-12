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
import static org.junit.Assert.assertNotNull;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.FetchResult;
import org.junit.Test;
import static org.eclipse.egit.gitflow.op.AbstractFeatureOperation.*;
public class FeatureTrackOperationTest extends AbstractDualRepositoryTestCase {
	@Test
	public void testFeatureTrack() throws Exception {
		new FeatureStartOperation(repository1.getRepository(), MY_FEATURE).execute(null);
		RevCommit branchCommit = repository1.createInitialCommit("testFeatureTrack");

		FeatureTrackOperation featureTrackOperation = new FeatureTrackOperation(repository2.getRepository(), getFirstRemoteFeatureRef());
		featureTrackOperation.execute(null);
		FetchResult operationResult = featureTrackOperation.getOperationResult();
		assertNotNull(operationResult.getAdvertisedRef(Constants.R_HEADS + FEATURE_PREFIX + SEP
				+ MY_FEATURE));
		assertEquals(getFeatureBranchName(MY_FEATURE), repository2.getRepository().getBranch());
		assertEquals(branchCommit, findHead(repository2.getRepository()));

		RevCommit localCommit = repository2.createInitialCommit("testFeatureTrack2");
		new FeaturePublishOperation(repository2.getRepository(), 0).execute(null);
		assertEquals(localCommit, findHead(repository2.getRepository()));
	}

	private Ref getFirstRemoteFeatureRef() throws CoreException {
		FeatureListOperation featureListOperation = new FeatureListOperation(repository2.getRepository(), 0);
		featureListOperation.execute(null);
		return featureListOperation.getResult().get(0);
	}
}
