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
import org.eclipse.jgit.lib.Repository;

@SuppressWarnings("restriction")
public final class FeatureCheckoutOperation extends AbstractFeatureOperation {
	public FeatureCheckoutOperation(Repository repository, String featureName) {
		super(repository, featureName);
	}

	public void execute(IProgressMonitor monitor) throws CoreException {
		String branchName = createFeatureBranchName(featureName);

		new BranchOperation(repository, branchName).execute(null);
	}
}
