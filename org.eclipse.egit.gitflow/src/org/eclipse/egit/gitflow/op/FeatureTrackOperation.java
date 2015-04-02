/*******************************************************************************
 * Copyright (C) 2015, Max Hohenegger <eclipse@hohenegger.eu>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.egit.gitflow.op;

import static org.eclipse.egit.core.op.CreateLocalBranchOperation.UpstreamConfig.REBASE;
import static org.eclipse.jgit.lib.Constants.DEFAULT_REMOTE_NAME;
import static org.eclipse.jgit.lib.Constants.R_REMOTES;

import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.egit.core.op.BranchOperation;
import org.eclipse.egit.core.op.CreateLocalBranchOperation;
import org.eclipse.egit.gitflow.Activator;
import org.eclipse.egit.gitflow.GitFlowRepository;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.transport.FetchResult;

@SuppressWarnings("restriction")
public final class FeatureTrackOperation extends AbstractFeatureOperation {
	public static final String REMOTE_ORIGIN_FEATURE_PREFIX = R_REMOTES + DEFAULT_REMOTE_NAME + SEP;
	private Ref remoteFeature;
	private FetchResult operationResult;

	public FeatureTrackOperation(GitFlowRepository repository, Ref ref) {
		this(repository, ref, ref.getName().substring(
				(REMOTE_ORIGIN_FEATURE_PREFIX + repository.getFeaturePrefix()).length()));
	}

	public FeatureTrackOperation(GitFlowRepository repository, Ref ref, String newLocalBranch) {
		super(repository, repository.getFeaturePrefix() + newLocalBranch);
		this.remoteFeature = ref;
	}

	public void execute(IProgressMonitor monitor) throws CoreException {
		try {
			String newLocalBranch = featureName;
			operationResult = fetch(monitor);

			if (repository.hasBranch(newLocalBranch)) {
				String errorMessage = String.format("Local branch '%s' already exists.", newLocalBranch);
				throw new CoreException(Activator.error(errorMessage));
			}
			CreateLocalBranchOperation createLocalBranchOperation = new CreateLocalBranchOperation(
					repository.getRepository(), newLocalBranch, remoteFeature, REBASE);
			createLocalBranchOperation.execute(monitor);

			BranchOperation branchOperation = new BranchOperation(repository.getRepository(), newLocalBranch);
			branchOperation.execute(monitor);
		} catch (URISyntaxException e) {
			throw new CoreException(Activator.error(e.getMessage(), e));
		} catch (InvocationTargetException e) {
			Throwable targetException = e.getTargetException();
			throw new CoreException(Activator.error(targetException.getMessage(), targetException));
		} catch (GitAPIException e) {
			throw new CoreException(Activator.error(e.getMessage(), e));
		}

	}

	public FetchResult getOperationResult() {
		return operationResult;
	}
}
