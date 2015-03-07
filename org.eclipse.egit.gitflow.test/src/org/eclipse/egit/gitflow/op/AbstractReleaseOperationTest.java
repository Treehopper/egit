/*******************************************************************************
 * Copyright (C) 2015, Max Hohenegger <eclipse@hohenegger.eu>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.egit.gitflow.op;

import java.io.IOException;

import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

abstract public class AbstractReleaseOperationTest extends AbstractGitFlowOperationTest {
	protected static final String MY_RELEASE = "myRelease";
	protected static final String MY_MASTER = "master";

	protected String getFullReleaseBranchName(String releaseName) {
		return Constants.R_HEADS + getReleaseBranchName(releaseName);
	}

	protected String getReleaseBranchName(String releaseName) {
		return AbstractReleaseOperation.RELEASE_PREFIX + SEP + releaseName;
	}

	protected RevCommit findCommitForTag(Repository repository, String tagName) throws MissingObjectException,
			IncorrectObjectTypeException, IOException {
		return new RevWalk(repository).parseCommit(repository.getRef(Constants.R_TAGS + tagName).getObjectId());
	}
}
