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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.egit.gitflow.GitFlowRepository;
import org.eclipse.egit.gitflow.WrongGitFlowStateException;

public final class ReleaseFinishOperation extends AbstractReleaseOperation {
	public ReleaseFinishOperation(GitFlowRepository repository, String releaseName) {
		super(repository, releaseName);
	}

	public ReleaseFinishOperation(GitFlowRepository repository) throws WrongGitFlowStateException, CoreException, IOException {
		this(repository, getReleaseName(repository));
	}

	public void execute(IProgressMonitor monitor) throws CoreException {
		String releaseBranchName = repository.getReleaseBranchName(versionName);
		mergeTo(monitor, releaseBranchName, repository.getMaster());
		finish(monitor, releaseBranchName);
		safeCreateTag(monitor, repository.getVersionTagPrefix() + versionName, "Release of " + versionName);
	}
}
