/*******************************************************************************
 * Copyright (C) 2015, Max Hohenegger <eclipse@hohenegger.eu>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.egit.gitflow.op;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.egit.gitflow.WrongGitFlowStateException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Repository;

abstract public class AbstractReleaseOperation extends GitFlowOperation {
	public static final String RELEASE_PREFIX = "release";

	protected String releaseName;

	public AbstractReleaseOperation(Repository repository, String releaseName) {
		super(repository);
		this.releaseName = releaseName;
	}

	protected static String createReleaseBranchName(String releaseName) {
		return RELEASE_PREFIX + SEP + releaseName;
	}

	public static String getFullReleaseBranchName(String releaseName) {
		return Constants.R_HEADS + getReleaseBranchName(releaseName);
	}

	protected static String getReleaseName(Repository repository) throws WrongGitFlowStateException, CoreException {
		if (!isRelease(repository)) {
			throw new WrongGitFlowStateException("Not on a feature branch.");
		}
		return getBranchNameTuple(repository)[1];
	}

	protected static boolean isRelease(Repository repository) throws CoreException {
		return hasTwoSegmentsWithPrefix(repository, RELEASE_PREFIX);
	}

	private static String getReleaseBranchName(String featureName) {
		return RELEASE_PREFIX + SEP + featureName;
	}
}
