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
import org.eclipse.egit.core.op.RebaseOperation;
import org.eclipse.egit.gitflow.Activator;
import org.eclipse.egit.gitflow.GitFlowRepository;
import org.eclipse.egit.gitflow.WrongGitFlowStateException;
import org.eclipse.jgit.api.RebaseResult;

@SuppressWarnings("restriction")
public final class FeatureRebaseOperation extends GitFlowOperation {
	private RebaseResult operationResult;

	public FeatureRebaseOperation(GitFlowRepository repository) {
		super(repository);
	}

	public void execute(IProgressMonitor monitor) throws CoreException {
		try {
			if (!repository.isFeature()) {
				throw new WrongGitFlowStateException("Not on a feature branch.");
			}

			RebaseOperation op = new RebaseOperation(repository.getRepository(), repository.getRepository().getRef(
					repository.getDevelopFull()));
			op.execute(null);

			operationResult = op.getResult();
		} catch (WrongGitFlowStateException e) {
			throw new CoreException(Activator.error(e.getMessage(), e));
		} catch (IOException e) {
			throw new CoreException(Activator.error(e.getMessage(), e));
		}
	}

	public RebaseResult getOperationResult() {
		return operationResult;
	}
}
