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
import org.eclipse.egit.gitflow.GitFlowRepository;

public final class HotfixStartOperation extends AbstractHotfixOperation {
	public HotfixStartOperation(GitFlowRepository repository, String hotfixName) {
		super(repository, hotfixName);
	}

	public void execute(IProgressMonitor monitor) throws CoreException {
		String branchName = repository.getHotfixBranchName(hotfixName);

		start(monitor, branchName, repository.findHead(repository.getMaster()));
	}
}
