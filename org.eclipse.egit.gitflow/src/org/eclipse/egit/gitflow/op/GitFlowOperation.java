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

import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.egit.core.internal.job.RuleUtil;
import org.eclipse.egit.core.op.CreateLocalBranchOperation;
import org.eclipse.egit.core.op.IEGitOperation;
import org.eclipse.jgit.errors.AmbiguousObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.RevisionSyntaxException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

@SuppressWarnings("restriction")
abstract public class GitFlowOperation implements IEGitOperation {
	static final String SEP = "/";

	static final String DEVELOP = "develop";
	static final String FEATURE_PREFIX = "feature";
	static final String RELEASE_PREFIX = "release";

	static final String DEVELOP_FULL = Constants.R_HEADS + "develop";

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
}
