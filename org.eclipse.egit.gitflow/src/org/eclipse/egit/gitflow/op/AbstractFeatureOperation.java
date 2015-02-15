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
import org.eclipse.egit.gitflow.WrongGitFlowStateException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Repository;

abstract public class AbstractFeatureOperation extends GitFlowOperation {
	public static final String FEATURE_PREFIX = "feature";

	protected String featureName;

	public AbstractFeatureOperation(Repository repository, String featureName) {
		super(repository);
		this.featureName = featureName;
	}

	protected static String createFeatureBranchName(String featureName) {
		return FEATURE_PREFIX + SEP + featureName;
	}

	protected static String getFeatureName(Repository repository) throws WrongGitFlowStateException, CoreException {
		if (!isFeature(repository)) {
			throw new WrongGitFlowStateException("Not on a feature branch.");
		}
		return getBranchNameTuple(repository)[1];
	}

	protected static boolean isFeature(Repository repository) throws CoreException {
		return hasTwoSegmentsWithPrefix(repository, FEATURE_PREFIX);
	}

	public static String getFullFeatureBranchName(String featureName) {
		return Constants.R_HEADS + getFeatureBranchName(featureName);
	}

	private static String getFeatureBranchName(String featureName) {
		return FEATURE_PREFIX + SEP + featureName;
	}
}
