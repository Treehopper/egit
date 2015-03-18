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
import org.eclipse.egit.gitflow.Activator;
import org.eclipse.egit.gitflow.GitFlowRepository;

public final class ReleaseStartOperation extends AbstractReleaseOperation {
	public ReleaseStartOperation(GitFlowRepository repository, String releaseName) {
		super(repository, releaseName);
	}

	public void execute(IProgressMonitor monitor) throws CoreException {
		String branchName = createReleaseBranchName(releaseName);


		try {
			if (!repository.isDevelop()) {
				throw new CoreException(Activator.error("Not on " + repository.getDevelop()));
			}
		} catch (IOException e) {
			throw new CoreException(Activator.error(e.getMessage(), e));
		}
		start(monitor, branchName, repository.findHead());

	}
}
