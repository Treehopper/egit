/*******************************************************************************
 * Copyright (C) 2015, Max Hohenegger <eclipse@hohenegger.eu>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.egit.gitflow.op;

import static org.eclipse.egit.gitflow.GitFlowRepository.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.egit.gitflow.GitFlowRepository;

import static org.eclipse.jgit.lib.Constants.*;

import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.FetchResult;
import org.junit.Test;
public class FeatureTrackOperationTest extends AbstractDualRepositoryTestCase {
	@Test
	public void testFeatureTrack() throws Exception {
		GitFlowRepository gfRepo1 = new GitFlowRepository(repository1.getRepository());
		GitFlowRepository gfRepo2 = new GitFlowRepository(repository2.getRepository());
		new FeatureStartOperation(gfRepo1, MY_FEATURE).execute(null);
		RevCommit branchCommit = repository1.createInitialCommit("testFeatureTrack");

		FeatureTrackOperation featureTrackOperation = new FeatureTrackOperation(gfRepo2,
				getFirstRemoteFeatureRef(gfRepo2));
		featureTrackOperation.execute(null);
		FetchResult operationResult = featureTrackOperation.getOperationResult();
		assertNotNull(operationResult.getAdvertisedRef(gfRepo2.getFullFeatureBranchName(MY_FEATURE)));
		assertEquals(gfRepo2.getFeatureBranchName(MY_FEATURE), repository2.getRepository().getBranch());
		assertEquals(branchCommit, findHead(repository2.getRepository()));

		RevCommit localCommit = repository2.createInitialCommit("testFeatureTrack2");
		new FeaturePublishOperation(gfRepo2, 0).execute(null);
		assertEquals(localCommit, findHead(repository2.getRepository()));

		// config updated?
		assertEquals(DEFAULT_REMOTE_NAME, getRemote(gfRepo2, MY_FEATURE));
		assertEquals(R_HEADS + gfRepo2.getFeatureBranchName(MY_FEATURE), getMerge(gfRepo2, MY_FEATURE));
	}

	private Ref getFirstRemoteFeatureRef(GitFlowRepository gfRepo) throws CoreException {
		FeatureListOperation featureListOperation = new FeatureListOperation(gfRepo, 0);
		featureListOperation.execute(null);
		return featureListOperation.getResult().get(0);
	}

	private String getRemote(GitFlowRepository gfRepo, String featureName) {
		Repository repository = gfRepo.getRepository();
		StoredConfig config = repository.getConfig();
		return config.getString(BRANCH_SECTION, gfRepo.getFeatureBranchName(featureName), REMOTE_KEY);
	}

	private String getMerge(GitFlowRepository gfRepo, String featureName) {
		Repository repository = gfRepo.getRepository();
		StoredConfig config = repository.getConfig();
		return config.getString(BRANCH_SECTION, gfRepo.getFeatureBranchName(featureName), MERGE_KEY);
	}
}
