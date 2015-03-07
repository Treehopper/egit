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
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.egit.core.op.TagOperation;
import org.eclipse.egit.gitflow.WrongGitFlowStateException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.TagBuilder;
import org.eclipse.jgit.revwalk.RevCommit;

@SuppressWarnings("restriction")
public final class ReleaseFinishOperation extends AbstractReleaseOperation {
	public ReleaseFinishOperation(Repository repository, String releaseName) {
		super(repository, releaseName);
	}

	public ReleaseFinishOperation(Repository repository) throws WrongGitFlowStateException, CoreException {
		this(repository, getReleaseName(repository));
	}

	public void execute(IProgressMonitor monitor) throws CoreException {
		String releaseBranchName = createReleaseBranchName(releaseName);
		mergeTo(monitor, releaseBranchName, MASTER);
		finish(monitor, releaseBranchName);
		RevCommit head = findHead(repository);
		createTag(monitor, head, "Release of " + releaseName);
	}

	private void createTag(IProgressMonitor monitor, RevCommit head, String message) throws CoreException {
		TagBuilder tag = new TagBuilder();
		tag.setTag(releaseName);
		tag.setMessage(message);
		tag.setObjectId(head);
		new TagOperation(repository, tag, false).execute(monitor);
	}
}
