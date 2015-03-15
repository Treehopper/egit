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
import org.eclipse.egit.gitflow.GitFlowRepository;
import org.eclipse.egit.gitflow.WrongGitFlowStateException;

abstract public class AbstractFeatureOperation extends GitFlowOperation {
	protected String featureName;

	public AbstractFeatureOperation(GitFlowRepository repository, String featureName) {
		super(repository);
		this.featureName = featureName;
	}

	protected static String getFeatureName(GitFlowRepository repository) throws WrongGitFlowStateException,
	CoreException, IOException {
		if (!repository.isFeature()) {
			throw new WrongGitFlowStateException("Not on a feature branch.");
		}
		String currentBranch = repository.getRepository().getBranch();
		return currentBranch.substring(repository.getFeaturePrefix().length());
	}
}
