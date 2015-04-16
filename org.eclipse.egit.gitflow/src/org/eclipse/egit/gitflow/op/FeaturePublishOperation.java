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
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.egit.core.op.PushOperation;
import org.eclipse.egit.core.op.PushOperationResult;

import static org.eclipse.egit.gitflow.Activator.error;

import org.eclipse.egit.gitflow.GitFlowRepository;
import org.eclipse.egit.gitflow.WrongGitFlowStateException;
import org.eclipse.egit.gitflow.internal.CoreText;

import static org.eclipse.jgit.lib.Constants.*;

/**
 * git flow feature publish
 */
@SuppressWarnings("restriction")
public final class FeaturePublishOperation extends AbstractFeatureOperation {
	private PushOperationResult operationResult;

	private int timeout;

	/**
	 * publish given feature branch
	 *
	 * @param repository
	 * @param featureName
	 * @param timeout
	 * @throws CoreException
	 */
	public FeaturePublishOperation(GitFlowRepository repository,
			String featureName, int timeout) throws CoreException {
		super(repository, featureName);
		this.timeout = timeout;
	}

	/**
	 * publish current feature branch
	 *
	 * @param repository
	 * @param timeout
	 * @throws WrongGitFlowStateException
	 * @throws CoreException
	 * @throws IOException
	 */
	public FeaturePublishOperation(GitFlowRepository repository, int timeout)
			throws WrongGitFlowStateException, CoreException, IOException {
		this(repository, getFeatureName(repository), timeout);
	}

	public void execute(IProgressMonitor monitor) throws CoreException {
		try {
			PushOperation pushOperation = new PushOperation(
					repository.getRepository(), DEFAULT_REMOTE_NAME, false,
					timeout);
			pushOperation.run(monitor);
			operationResult = pushOperation.getOperationResult();

			if (!operationResult.isSuccessfulConnectionForAnyURI()) {
				String errorMessage = String.format(
						CoreText.FeaturePublishOperation_pushToRemoteFailed,
						operationResult.getErrorStringForAllURis());
				throw new CoreException(error(errorMessage));
			}
		} catch (InvocationTargetException e) {
			Throwable targetException = e.getTargetException();
			throw new CoreException(error(targetException.getMessage(),
					targetException));
		}

		String newLocalBranch = repository.getFeatureBranchName(featureName);
		try {
			setRemote(newLocalBranch, DEFAULT_REMOTE_NAME);
			setMerge(newLocalBranch,
					repository.getFullFeatureBranchName(featureName));
		} catch (IOException e) {
			throw new CoreException(error(
					CoreText.FeaturePublishOperation_unableToStoreGitConfig, e));
		}
	}

	/**
	 * @return result set after operation was executed
	 */
	public PushOperationResult getOperationResult() {
		return operationResult;
	}
}
