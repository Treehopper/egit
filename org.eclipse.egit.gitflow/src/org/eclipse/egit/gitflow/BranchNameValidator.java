/*******************************************************************************
 * Copyright (C) 2015, Max Hohenegger <eclipse@hohenegger.eu>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.egit.gitflow;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.egit.gitflow.op.AbstractFeatureOperation;
import org.eclipse.egit.gitflow.op.AbstractReleaseOperation;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;

public class BranchNameValidator {
	public static final String ILLEGAL_CHARS = "/";

	public static boolean featureExists(Repository repository, String featureName) throws CoreException {
		return branchExists(repository, AbstractFeatureOperation.getFullFeatureBranchName(featureName));
	}

	public static boolean releaseExists(Repository repository, String releaseName) throws CoreException {
		return branchExists(repository, AbstractReleaseOperation.getFullReleaseBranchName(releaseName));
	}

	private static boolean branchExists(Repository repository, String fullBranchName) throws CoreException {
		List<Ref> branches;
		try {
			branches = Git.wrap(repository).branchList().call();
		} catch (GitAPIException e) {
			throw new CoreException(Activator.error(e.getMessage(), e));
		}
		for (Ref ref : branches) {
			if (fullBranchName.equals(ref.getTarget().getName())) {
				return true;
			}
		}

		return false;
	}

	public static boolean isBranchNameValid(String string) {
		if (string.isEmpty()) {
			return false;
		}

		for (int i = 0; i < ILLEGAL_CHARS.length(); i++) {
			char illegalChar = ILLEGAL_CHARS.charAt(i);
			if (string.contains(String.valueOf(illegalChar))) {
				return false;
			}
		}

		return true;
	}
}
