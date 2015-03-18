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

abstract public class AbstractHotfixOperation extends GitFlowOperation {
	protected String hotfixName;

	public AbstractHotfixOperation(GitFlowRepository repository, String hotfixName) {
		super(repository);
		this.hotfixName = hotfixName;
	}

	protected static String getHotfixName(GitFlowRepository repository) throws WrongGitFlowStateException,
	CoreException, IOException {
		if (!repository.isHotfix()) {
			throw new WrongGitFlowStateException("Not on a hotfix branch.");
		}
		String currentBranch = repository.getRepository().getBranch();
		return currentBranch.substring(repository.getHotfixPrefix().length());
	}
}
