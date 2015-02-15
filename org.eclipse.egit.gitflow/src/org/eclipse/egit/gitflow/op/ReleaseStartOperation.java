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
import org.eclipse.egit.gitflow.Activator;
import org.eclipse.egit.gitflow.WrongGitFlowStateException;
import org.eclipse.jgit.lib.Repository;

public final class ReleaseStartOperation extends AbstractReleaseOperation {
	public ReleaseStartOperation(Repository repository, String releaseName) {
		super(repository, releaseName);
	}

	public void execute(IProgressMonitor monitor) throws CoreException {
		String branchName = createReleaseBranchName(releaseName);

		try {
			start(monitor, branchName);
		} catch (WrongGitFlowStateException e) {
			throw new CoreException(Activator.error(e.getMessage(), e));
		}
	}
}
