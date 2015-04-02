/*******************************************************************************
 * Copyright (C) 2015, Max Hohenegger <eclipse@hohenegger.eu>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.egit.gitflow.op;

import static org.eclipse.egit.gitflow.GitFlowRepository.BRANCH_SECTION;
import static org.eclipse.egit.gitflow.GitFlowRepository.MERGE_KEY;
import static org.eclipse.egit.gitflow.GitFlowRepository.REMOTE_KEY;

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.egit.gitflow.GitFlowRepository;
import org.eclipse.egit.gitflow.WrongGitFlowStateException;
import org.eclipse.jgit.lib.StoredConfig;

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

	protected void setRemote(String featureName, String value) throws IOException {
		setBranchValue(featureName, value, REMOTE_KEY);
	}

	protected void setMerge(String featureName, String value) throws IOException {
		setBranchValue(featureName, value, MERGE_KEY);
	}

	private void setBranchValue(String featureName, String value, String mergeKey) throws IOException {
		StoredConfig config = repository.getRepository().getConfig();
		config.setString(BRANCH_SECTION, featureName, mergeKey, value);
		config.save();
	}
}
