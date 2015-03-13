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
import java.util.List;

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
import org.eclipse.egit.gitflow.Activator;
import org.eclipse.egit.gitflow.WrongGitFlowStateException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.AmbiguousObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.RevisionSyntaxException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.FetchResult;
import org.eclipse.jgit.transport.RemoteConfig;

@SuppressWarnings("restriction")
abstract public class GitFlowOperation implements IEGitOperation {
	public static final String SEP = "/";

	public static final String DEVELOP = "develop";
	public static final String MASTER = "master";
	public static final String RELEASE_PREFIX = "release";
	public static final String FEATURE_PREFIX = "feature";
	public static final String HOTFIX_PREFIX = "hotfix";

	public static final String DEVELOP_FULL = Constants.R_HEADS + DEVELOP;


	protected Repository repository;

	public GitFlowOperation(Repository repository) {
		this.repository = repository;
	}

	public ISchedulingRule getSchedulingRule() {
		return RuleUtil.getRule(repository);
	}

	protected CreateLocalBranchOperation createBranchFromHead(Repository repository, String branchName) {
		return new CreateLocalBranchOperation(repository, branchName, findHead(repository));
	}

	protected RevCommit findHead(Repository repo) {
		RevWalk walk = new RevWalk(repo);

		try {
			ObjectId head = repo.resolve(Constants.HEAD);
			return walk.parseCommit(head);
		} catch (RevisionSyntaxException e) {
			throw new RuntimeException(e);
		} catch (AmbiguousObjectException e) {
			throw new RuntimeException(e);
		} catch (IncorrectObjectTypeException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	protected boolean hasBranches() {
		List<Ref> branches;
		try {
			branches = Git.wrap(repository).branchList().call();
			return !branches.isEmpty();
		} catch (GitAPIException e) {
			throw new RuntimeException(e);
		}
	}

	protected String getUser() {
		StoredConfig config = repository.getConfig();
		String userName = config.getString("user", null, "name");
		String email = config.getString("user", null, "email");
		return String.format("%s <%s>", userName, email);
	}

	protected String getFeaturePrefix() {
		return getPrefix("feature");
	}

	protected String getReleasePrefix() {
		return getPrefix("releae");
	}

	protected String getHotfixPrefix() {
		return getPrefix("hotfix");
	}

	private String getPrefix(String prefixName) {
		StoredConfig config = repository.getConfig();
		return config.getString("gitflow", "prefix", prefixName);
	}

	protected void setPrefix(String prefixName, String value) {
		StoredConfig config = repository.getConfig();
		config.setString("gitflow", "prefix", prefixName, value);
	}

	protected void setBranch(String branchName, String value) {
		StoredConfig config = repository.getConfig();
		config.setString("gitflow", "branch", branchName, value);
	}

	protected void start(IProgressMonitor monitor, String branchName) throws WrongGitFlowStateException, CoreException {
		try {
			if (!repository.getBranch().equals(DEVELOP)) {
				throw new WrongGitFlowStateException("Not on " + DEVELOP);
			}
		} catch (IOException e) {
			throw new CoreException(Activator.error(e.getMessage(), e));
		}
		try {
			CreateLocalBranchOperation branchOperation = createBranchFromHead(repository, branchName);
			branchOperation.execute(monitor);
			BranchOperation checkoutOperation = new BranchOperation(repository, branchName);
			checkoutOperation.execute(monitor);
		} catch (CoreException e) {
			throw e;
		}
	}

	protected void finish(IProgressMonitor monitor, String branchName) throws CoreException {
		try {
			mergeTo(monitor, branchName, DEVELOP);

			Ref branch = findBranch(repository, branchName);
			if (branch == null) {
				throw new IllegalStateException(String.format("Branch %s missing", branchName));
			}
			new DeleteBranchOperation(repository, branch, false).execute(monitor);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	protected void mergeTo(IProgressMonitor monitor, String branchName, String targetBranchName) throws CoreException {
		try {
			if (!hasBranch(targetBranchName)) {
				throw new RuntimeException(String.format("No branch '%s' found.", targetBranchName));
			}
			new BranchOperation(repository, targetBranchName).execute(monitor);
			new MergeOperation(repository, branchName).execute(monitor);
		} catch (GitAPIException e) {
			throw new RuntimeException(e);
		}
	}

	protected boolean hasBranch(String branch) throws GitAPIException {
		String fullBranchName = Constants.R_HEADS + branch;
		List<Ref> branchList = Git.wrap(repository).branchList().call();
		for (Ref ref : branchList) {
			if (fullBranchName.equals(ref.getTarget().getName())) {
				return true;
			}
		}

		return false;
	}

	private Ref findBranch(Repository repository, String branchName) throws IOException {
		return repository.getRef(Constants.R_HEADS + branchName);
	}

	protected FetchResult fetch(IProgressMonitor monitor) throws URISyntaxException, InvocationTargetException {
		StoredConfig rc = repository.getConfig();
		RemoteConfig config = new RemoteConfig(rc, Constants.DEFAULT_REMOTE_NAME);
		FetchOperation fetchOperation = new FetchOperation(repository, config, 0, false);
		fetchOperation.run(monitor);
		return fetchOperation.getOperationResult();
	}

	protected static boolean hasTwoSegmentsWithPrefix(Repository repository, String prefix) throws CoreException {
		String branch;
		try {
			branch = repository.getBranch();
		} catch (IOException e) {
			throw new CoreException(Activator.error(e.getMessage(), e));
		}

		String[] split = branch.split(SEP);
		if (split.length != 2) {
			return false;
		}

		return prefix.equals(split[0]);
	}

	protected static String[] getBranchNameTuple(Repository repository) throws WrongGitFlowStateException {
		String branch;
		try {
			branch = repository.getBranch();
		} catch (IOException e) {
			throw new WrongGitFlowStateException(e);
		}

		String[] split = branch.split(SEP);
		if (split.length != 2) {
			throw new WrongGitFlowStateException("Not on a git flow branch. Current branch is " + branch);
		}
		return split;
	}
}
