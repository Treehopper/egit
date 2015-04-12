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
import static java.lang.String.format;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import static org.eclipse.egit.gitflow.Activator.error;
import static org.eclipse.jgit.lib.Constants.*;

import org.eclipse.egit.gitflow.GitFlowRepository;
import org.eclipse.jgit.errors.AmbiguousObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.RevisionSyntaxException;
import org.eclipse.jgit.revwalk.RevCommit;

public final class ReleaseStartOperation extends AbstractReleaseOperation {
	private String startCommitSha1;

	public ReleaseStartOperation(GitFlowRepository repository, String startCommitSha1, String releaseName) {
		super(repository, releaseName);
		this.startCommitSha1 = startCommitSha1;
	}

	public ReleaseStartOperation(GitFlowRepository repository, String releaseName) {
		super(repository, releaseName);
		this.startCommitSha1 = repository.findHead().getName();
	}

	public void execute(IProgressMonitor monitor) throws CoreException {

		String branchName = repository.getReleaseBranchName(versionName);

		try {
			if (releaseExists(versionName)) {
				throw new CoreException(error(format("The release name '%s' already exists.", versionName)));
			}
			if (!repository.isDevelop()) {
				throw new CoreException(error("Not on " + repository.getDevelop()));
			}
		} catch (IOException e) {
			throw new CoreException(error(e.getMessage(), e));
		}

		RevCommit commit = repository.findCommit(startCommitSha1);
		start(monitor, branchName, commit);
	}

	public boolean releaseExists(String versionName) throws RevisionSyntaxException, AmbiguousObjectException,
	IncorrectObjectTypeException, IOException {
		return null != repository.getRepository().resolve(R_TAGS + repository.getVersionTagPrefix() + versionName);
	}
}
