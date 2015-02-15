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

@SuppressWarnings("restriction")
public final class ReleaseFinishOperation extends AbstractReleaseOperation {
	public ReleaseFinishOperation(Repository repository, String releaseName) {
		super(repository, releaseName);
	}

	public ReleaseFinishOperation(Repository repository) throws WrongGitFlowStateException, CoreException {
		this(repository, getReleaseName(repository));
	}

	public void execute(IProgressMonitor monitor) throws CoreException {
		finish(monitor, createReleaseBranchName(releaseName));
		TagBuilder tag = new TagBuilder();
		tag.setTag(releaseName);
		tag.setMessage("Release of " + releaseName);
		tag.setObjectId(findHead(repository));
		TagOperation tagOperation = new TagOperation(repository, tag, false);
		try {
			tagOperation.execute(monitor);
		} catch (CoreException e) {
			throw new RuntimeException(e);
		}
	}
}
