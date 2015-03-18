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
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.egit.core.internal.job.RuleUtil;
import org.eclipse.egit.core.op.BranchOperation;
import org.eclipse.egit.core.op.CreateLocalBranchOperation;
import org.eclipse.egit.core.op.DeleteBranchOperation;
import org.eclipse.egit.core.op.FetchOperation;
import org.eclipse.egit.core.op.IEGitOperation;
import org.eclipse.egit.core.op.MergeOperation;
import org.eclipse.egit.gitflow.GitFlowRepository;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.FetchResult;
import org.eclipse.jgit.transport.RemoteConfig;

@SuppressWarnings("restriction")
abstract public class GitFlowOperation implements IEGitOperation {
	public static final String SEP = "/";

	protected GitFlowRepository repository;

	public GitFlowOperation(GitFlowRepository repository) {
		this.repository = repository;
	}

	public GitFlowOperation() {
	}

	public ISchedulingRule getSchedulingRule() {
		return RuleUtil.getRule(repository.getRepository());
	}

	protected CreateLocalBranchOperation createBranchFromHead(String branchName, RevCommit sourceCommit) {
		return new CreateLocalBranchOperation(repository.getRepository(), branchName, sourceCommit);
	}

	protected void start(IProgressMonitor monitor, String branchName, RevCommit sourceCommit) throws CoreException {
		CreateLocalBranchOperation branchOperation = createBranchFromHead(branchName, sourceCommit);
		branchOperation.execute(monitor);
		BranchOperation checkoutOperation = new BranchOperation(repository.getRepository(), branchName);
		checkoutOperation.execute(monitor);
	}

	protected void finish(IProgressMonitor monitor, String branchName) throws CoreException {
		try {
			mergeTo(monitor, branchName, repository.getDevelop());

			Ref branch = repository.findBranch(branchName);
			if (branch == null) {
				throw new IllegalStateException(String.format("Branch %s missing", branchName));
			}
			new DeleteBranchOperation(repository.getRepository(), branch, false).execute(monitor);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	protected void mergeTo(IProgressMonitor monitor, String branchName, String targetBranchName) throws CoreException {
		try {
			if (!repository.hasBranch(targetBranchName)) {
				throw new RuntimeException(String.format("No branch '%s' found.", targetBranchName));
			}
			new BranchOperation(repository.getRepository(), targetBranchName).execute(monitor);
			new MergeOperation(repository.getRepository(), branchName).execute(monitor);
		} catch (GitAPIException e) {
			throw new RuntimeException(e);
		}
	}

	protected FetchResult fetch(IProgressMonitor monitor) throws URISyntaxException, InvocationTargetException {
		RemoteConfig config = getDefaultRemoteConfig();
		FetchOperation fetchOperation = new FetchOperation(repository.getRepository(), config, 0, false);
		fetchOperation.run(monitor);
		return fetchOperation.getOperationResult();
	}

	private RemoteConfig getDefaultRemoteConfig() throws URISyntaxException {
		StoredConfig rc = repository.getRepository().getConfig();
		RemoteConfig config = new RemoteConfig(rc, Constants.DEFAULT_REMOTE_NAME);
		return config;
	}
}
