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
import java.net.URISyntaxException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.egit.core.op.BranchOperation;
import org.eclipse.egit.core.op.CreateLocalBranchOperation;
import org.eclipse.egit.core.op.CreateLocalBranchOperation.UpstreamConfig;
import org.eclipse.egit.core.op.FetchOperation;
import org.eclipse.egit.gitflow.Activator;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.transport.RemoteConfig;

@SuppressWarnings("restriction")
public final class FeatureTrackOperation extends AbstractFeatureOperation {
	public FeatureTrackOperation(Repository repository, String remoteFeatureName) {
		super(repository, remoteFeatureName);
	}

	public void execute(IProgressMonitor monitor) throws CoreException {
		try {
			String newLocalBranch = getFeatureBranchName(featureName);
			String remoteBranchName = newLocalBranch;
			StoredConfig rc = repository.getConfig();
			RemoteConfig config = new RemoteConfig(rc, Constants.DEFAULT_REMOTE_NAME);
			new FetchOperation(repository, config, 0, false).run(monitor);

			Ref remoteFeature = repository.getRef(Constants.R_REMOTES + Constants.DEFAULT_REMOTE_NAME + "/"
					+ remoteBranchName);
			new CreateLocalBranchOperation(repository, newLocalBranch, remoteFeature, UpstreamConfig.REBASE)
			.execute(monitor);

			new BranchOperation(repository, newLocalBranch).execute(monitor);
		} catch (URISyntaxException e) {
			new CoreException(Activator.error(e.getMessage(), e));
		} catch (InvocationTargetException e) {
			new CoreException(Activator.error(e.getMessage(), e));
		} catch (IOException e) {
			new CoreException(Activator.error(e.getMessage(), e));
		}

	}
}
