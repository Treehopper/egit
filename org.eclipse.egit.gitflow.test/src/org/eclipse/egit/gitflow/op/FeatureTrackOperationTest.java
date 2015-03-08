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
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Test;

public class FeatureTrackOperationTest extends AbstractDualRepositoryTestCase {
	@Test
	public void testFeatureTrack() throws Exception {
		new FeatureStartOperation(repository1.getRepository(), MY_FEATURE).execute(null);
		RevCommit branchCommit = repository1.createInitialCommit("testFeatureTrack");

		new FeatureTrackOperation(repository2.getRepository(), getFirstRemoteFeatureRef()).execute(null);
		assertEquals(getFeatureBranchName(MY_FEATURE), repository2.getRepository().getBranch());
		assertEquals(branchCommit, findHead(repository2.getRepository()));

		RevCommit localCommit = repository2.createInitialCommit("testFeatureTrack2");
		new FeaturePublishOperation(repository2.getRepository()).execute(null);
		assertEquals(localCommit, findHead(repository2.getRepository()));
	}

	private Ref getFirstRemoteFeatureRef() throws CoreException {
		FeatureListOperation featureListOperation = new FeatureListOperation(repository2.getRepository());
		featureListOperation.execute(null);
		return featureListOperation.getResult().get(0);
	}
}
