/*******************************************************************************
 * Copyright (C) 2015, Max Hohenegger <eclipse@hohenegger.eu>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.egit.gitflow.op;

import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Test;

public class FeaturePublishOperationTest extends AbstractDualRepositoryTestCase {
	@Test
	public void testFeaturePublish() throws Exception {
		new FeatureStartOperation(repository2.getRepository(), MY_FEATURE).execute(null);
		RevCommit branchCommit = repository2.createInitialCommit("testFeaturePublish");
		new FeaturePublishOperation(repository2.getRepository(), 0).execute(null);
		assertCommitArrivedAtRemote(branchCommit, repository1.getRepository());

		new FeatureFinishOperation(repository1.getRepository(), MY_FEATURE);
	}
}
