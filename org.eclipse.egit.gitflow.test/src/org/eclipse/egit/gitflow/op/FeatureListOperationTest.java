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

import java.util.List;

import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Ref;
import org.junit.Test;

public class FeatureListOperationTest extends AbstractDualRepositoryTestCase {
	@Test
	public void testFeatureList() throws Exception {
		new FeatureStartOperation(repository1.getRepository(), MY_FEATURE).execute(null);

		FeatureListOperation featureListOperation = new FeatureListOperation(repository2.getRepository());
		featureListOperation.execute(null);
		List<Ref> result = featureListOperation.getResult();
		assertEquals(1, result.size());
		assertEquals(Constants.R_REMOTES + Constants.DEFAULT_REMOTE_NAME + SEP + GitFlowOperation.FEATURE_PREFIX + SEP
				+ MY_FEATURE, result.get(0).getName());

		new FeatureFinishOperation(repository1.getRepository(), MY_FEATURE);
	}
}
