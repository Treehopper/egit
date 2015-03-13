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
import org.eclipse.egit.core.op.BranchOperation;
import org.eclipse.egit.core.op.CommitOperation;
import org.eclipse.egit.core.op.CreateLocalBranchOperation;
import org.eclipse.egit.gitflow.Activator;
import org.eclipse.jgit.lib.Repository;

@SuppressWarnings("restriction")
public final class InitOperation extends GitFlowOperation {
	public InitOperation(Repository repository) {
		super(repository);
	}

	public void execute(IProgressMonitor monitor) throws CoreException {
		if (!hasBranches()) {
			new CommitOperation(repository, getUser(), getUser(), "Git Flow inital commit").execute(monitor);
		}

		CreateLocalBranchOperation branchFromHead = createBranchFromHead(repository, DEVELOP);
		branchFromHead.execute(monitor);
		BranchOperation checkoutOperation = new BranchOperation(repository, DEVELOP);
		checkoutOperation.execute(monitor);

		try {
			// TODO: this must be configurable
			setPrefixes(FEATURE_PREFIX + SEP, RELEASE_PREFIX + SEP, DEVELOP + SEP);
			setBranches(DEVELOP, MASTER);
			repository.getConfig().save();
		} catch (IOException e) {
			throw new CoreException(Activator.error(e.getMessage(), e));
		}
	}

	private void setPrefixes(String feature, String release, String hotfix) {
		setPrefix("feature", feature);
		setPrefix("release", release);
		setPrefix("hotfix", hotfix);
	}

	private void setBranches(String develop, String master) {
		setBranch("develop", develop);
		setBranch("master", master);
	}
}
