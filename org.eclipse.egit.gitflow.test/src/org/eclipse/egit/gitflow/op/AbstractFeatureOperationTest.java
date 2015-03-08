/*******************************************************************************
 * Copyright (C) 2015, Max Hohenegger <eclipse@hohenegger.eu>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.egit.gitflow.op;

import org.eclipse.jgit.lib.Constants;

abstract public class AbstractFeatureOperationTest extends AbstractGitFlowOperationTest {
	protected String getFullFeatureBranchName(String featureName) {
		return Constants.R_HEADS + getFeatureBranchName(featureName);
	}

	protected String getFeatureBranchName(String featureName) {
		return GitFlowOperation.FEATURE_PREFIX + SEP + featureName;
	}
}
