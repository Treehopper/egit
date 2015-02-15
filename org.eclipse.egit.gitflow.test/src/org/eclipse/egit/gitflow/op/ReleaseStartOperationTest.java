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

import org.eclipse.jgit.lib.Repository;
import org.junit.Test;

public class ReleaseStartOperationTest extends AbstractReleaseOperationTest {
	@Test
	public void testReleaseStart() throws Exception {
		testRepository.createInitialCommit("testReleaseStart\n\nfirst commit\n");

		Repository repository = testRepository.getRepository();
		new InitOperation(repository).execute(null);

		new ReleaseStartOperation(repository, MY_RELEASE).execute(null);

		assertEquals(getFullReleaseBranchName(MY_RELEASE), repository
				.getFullBranch());
	}
}
