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
import org.eclipse.egit.gitflow.Activator;
import org.eclipse.egit.gitflow.GitFlowRepository;
import org.eclipse.egit.gitflow.WrongGitFlowStateException;
import org.eclipse.jgit.lib.Constants;

@SuppressWarnings("restriction")
public final class FeaturePublishOperation extends AbstractFeatureOperation {

	private PushOperation pushOperation;

	public FeaturePublishOperation(GitFlowRepository repository, String featureName, int timeout) throws CoreException {
		super(repository, featureName);
		pushOperation = new PushOperation(repository.getRepository(), Constants.DEFAULT_REMOTE_NAME, false, timeout);
	}

	public FeaturePublishOperation(GitFlowRepository repository, int timeout) throws WrongGitFlowStateException,
			CoreException, IOException {
		this(repository, getFeatureName(repository), timeout);
	}

	public void execute(IProgressMonitor monitor) throws CoreException {
		try {
			pushOperation.run(monitor);
		} catch (InvocationTargetException e) {
			Throwable targetException = e.getTargetException();
			throw new CoreException(Activator.error(targetException.getMessage(), targetException));
		}
	}

	public PushOperationResult getOperationResult() {
		return pushOperation.getOperationResult();
	}
}
