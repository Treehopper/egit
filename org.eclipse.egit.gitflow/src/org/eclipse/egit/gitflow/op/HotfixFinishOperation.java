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

import static org.eclipse.egit.gitflow.Activator.error;

import org.eclipse.egit.gitflow.GitFlowRepository;
import org.eclipse.egit.gitflow.WrongGitFlowStateException;
import org.eclipse.jgit.api.MergeResult;

public final class HotfixFinishOperation extends AbstractHotfixOperation {
	private MergeResult mergeResult;

	public HotfixFinishOperation(GitFlowRepository repository, String hotfixName) {
		super(repository, hotfixName);
	}

	public HotfixFinishOperation(GitFlowRepository repository) throws WrongGitFlowStateException, CoreException, IOException {
		this(repository, getHotfixName(repository));
	}

	public void execute(IProgressMonitor monitor) throws CoreException {
		String hotfixBranchName = repository.getHotfixBranchName(versionName);
		MergeResult mergeResult = mergeTo(monitor, hotfixBranchName, repository.getMaster());
		this.mergeResult = mergeResult;
		if (!mergeResult.getMergeStatus().isSuccessful()) {
			throw new CoreException(
					error("Merge from Hotfix to Master branch failed. This shouldn't happen in GitFlow."));
		}

		mergeResult = finish(monitor, hotfixBranchName);
		this.mergeResult = mergeResult;
		if (!mergeResult.getMergeStatus().isSuccessful()) {
			return;
		}

		safeCreateTag(monitor, versionName, "Hotifx " + versionName);
	}

	public MergeResult getOperationResult() {
		return mergeResult;
	}
}
