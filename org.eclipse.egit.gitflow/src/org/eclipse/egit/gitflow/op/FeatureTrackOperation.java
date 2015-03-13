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
import java.net.URISyntaxException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.egit.core.op.BranchOperation;
import org.eclipse.egit.core.op.CreateLocalBranchOperation;
import org.eclipse.egit.core.op.CreateLocalBranchOperation.UpstreamConfig;
import org.eclipse.egit.gitflow.Activator;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.FetchResult;

@SuppressWarnings("restriction")
public final class FeatureTrackOperation extends AbstractFeatureOperation {
	public static final String REMOTE_ORIGIN_FEATURE_PREFIX = Constants.R_REMOTES + Constants.DEFAULT_REMOTE_NAME + SEP
			+ FEATURE_PREFIX + SEP;
	private Ref remoteFeature;
	private FetchResult operationResult;

	public FeatureTrackOperation(Repository repository, Ref ref) {
		this(repository, ref, ref.getName().substring(REMOTE_ORIGIN_FEATURE_PREFIX.length()));
	}

	public FeatureTrackOperation(Repository repository, Ref ref, String newLocalBranch) {
		super(repository, getFeatureBranchName(newLocalBranch));
		this.remoteFeature = ref;
	}

	public void execute(IProgressMonitor monitor) throws CoreException {
		try {
			String newLocalBranch = featureName;
			operationResult = fetch(monitor);

			if (!hasBranch(newLocalBranch)) {
				new CreateLocalBranchOperation(repository, newLocalBranch, remoteFeature, UpstreamConfig.REBASE)
				.execute(monitor);
			}

			new BranchOperation(repository, newLocalBranch).execute(monitor);
		} catch (URISyntaxException e) {
			throw new CoreException(Activator.error(e.getMessage(), e));
		} catch (InvocationTargetException e) {
			throw new CoreException(Activator.error(e.getMessage(), e));
		} catch (GitAPIException e) {
			throw new CoreException(Activator.error(e.getMessage(), e));
		}

	}

	public FetchResult getOperationResult() {
		return operationResult;
	}
}
