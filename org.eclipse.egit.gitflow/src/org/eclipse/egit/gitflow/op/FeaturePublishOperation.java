/*******************************************************************************
 * Copyright (C) 2015, Max Hohenegger <eclipse@hohenegger.eu>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.egit.gitflow.op;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.egit.core.op.PushOperation;
import org.eclipse.egit.gitflow.Activator;
import org.eclipse.egit.gitflow.WrongGitFlowStateException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Repository;

@SuppressWarnings("restriction")
public final class FeaturePublishOperation extends AbstractFeatureOperation {

	public FeaturePublishOperation(Repository repository, String featureName) throws CoreException {
		super(repository, featureName);
	}

	public FeaturePublishOperation(Repository repository) throws WrongGitFlowStateException, CoreException {
		this(repository, getFeatureName(repository));
	}

	public void execute(IProgressMonitor monitor) throws CoreException {
		try {
			new PushOperation(repository, Constants.DEFAULT_REMOTE_NAME, false, 0).run(monitor);
		} catch (InvocationTargetException e) {
			throw new CoreException(Activator.error(e.getMessage(), e));
		} catch (Exception e) {
			throw new CoreException(Activator.error(e.getMessage(), e));
		}
	}
}
