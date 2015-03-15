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
import org.eclipse.egit.core.op.BranchOperation;
import org.eclipse.egit.core.op.CommitOperation;
import org.eclipse.egit.core.op.CreateLocalBranchOperation;
import org.eclipse.egit.gitflow.Activator;
import org.eclipse.egit.gitflow.GitFlowRepository;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;

@SuppressWarnings("restriction")
public final class InitOperation extends GitFlowOperation {
	// TODO: this must be configurable
	public static final String DEVELOP = "develop";
	public static final String MASTER = "master";
	public static final String RELEASE_PREFIX = "release/";
	public static final String FEATURE_PREFIX = "feature/";
	public static final String HOTFIX_PREFIX = "hotfix/";

	private String develop;
	private String master;
	private String feature;
	private String release;
	private String hotfix;

	public InitOperation(Repository jGitRepository, String develop, String master, String feature, String release,
			String hotfix) {
		super(new GitFlowRepository(jGitRepository));
		this.develop = develop;
		this.master = master;
		this.feature = feature;
		this.release = release;
		this.hotfix = hotfix;
	}

	public void execute(IProgressMonitor monitor) throws CoreException {
		try {
			setPrefixes(feature, release, hotfix);
			setBranches(develop, master);
			repository.getRepository().getConfig().save();
		} catch (IOException e) {
			throw new CoreException(Activator.error(e.getMessage(), e));
		}

		if (!repository.hasBranches()) {
			new CommitOperation(repository.getRepository(), repository.getUser(), repository.getUser(),
					"Git Flow inital commit").execute(monitor);
		}

		try {
			if (!repository.hasBranch(DEVELOP)) {
				CreateLocalBranchOperation branchFromHead = createBranchFromHead(develop);
				branchFromHead.execute(monitor);
				BranchOperation checkoutOperation = new BranchOperation(repository.getRepository(), develop);
				checkoutOperation.execute(monitor);
			}
		} catch (GitAPIException e) {
			throw new CoreException(Activator.error(e.getMessage(), e));
		}
	}

	private void setPrefixes(String feature, String release, String hotfix) {
		repository.setPrefix("feature", feature);
		repository.setPrefix("release", release);
		repository.setPrefix("hotfix", hotfix);
	}

	private void setBranches(String develop, String master) {
		repository.setBranch("develop", develop);
		repository.setBranch("master", master);
	}
}
