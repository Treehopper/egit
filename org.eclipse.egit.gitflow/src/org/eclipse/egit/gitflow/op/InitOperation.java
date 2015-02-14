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
import org.eclipse.egit.core.op.BranchOperation;
import org.eclipse.egit.core.op.CreateLocalBranchOperation;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.errors.NoWorkTreeException;
import org.eclipse.jgit.lib.Repository;

@SuppressWarnings("restriction")
public final class InitOperation extends GitFlowOperation {
	public InitOperation(Repository repository) {
		super(repository);
	}

	public void execute(IProgressMonitor monitor) throws CoreException {
		try {
			// provoke NoHeadException
			Git.wrap(repository).log().addPath(".").call();
		} catch (NoWorkTreeException e) {
			throw new RuntimeException(e);
		} catch (NoHeadException e) {
			throw new RuntimeException(e);
		} catch (GitAPIException e) {
			throw new RuntimeException(e);
		}

		try {
			CreateLocalBranchOperation branchFromHead = createBranchFromHead(repository, DEVELOP);
			branchFromHead.execute(monitor);
			BranchOperation checkoutOperation = new BranchOperation(repository, DEVELOP);
			checkoutOperation.execute(monitor);
		} catch (CoreException e) {
			throw new RuntimeException(e);
		}
	}
}
