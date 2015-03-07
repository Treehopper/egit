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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.egit.core.op.PushOperation;
import org.eclipse.egit.core.op.PushOperationSpecification;
import org.eclipse.egit.gitflow.Activator;
import org.eclipse.egit.gitflow.WrongGitFlowStateException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.RemoteRefUpdate;
import org.eclipse.jgit.transport.Transport;
import org.eclipse.jgit.transport.URIish;

@SuppressWarnings("restriction")
public final class FeaturePublishOperation extends AbstractFeatureOperation {
	// "+refs/heads/*:refs/remotes/origin/*"
	private static final String REFS_HEADS_REFS_REMOTES_ORIGIN = String.format("+%s*:%s%s/*", Constants.R_HEADS, Constants.R_REMOTES, Constants.DEFAULT_REMOTE_NAME);;
	private URIish uri;

	public FeaturePublishOperation(Repository repository, String featureName) throws CoreException {
		super(repository, featureName);

		StoredConfig config = repository.getConfig();
		String url = config.getString("remote", Constants.DEFAULT_REMOTE_NAME, "url");
		if (url == null) {
			throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, "No remote URI."));
		}
		try {
			this.uri = new URIish(url);
		} catch (URISyntaxException e) {
			throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Remote URI invalid."));
		}
	}

	public FeaturePublishOperation(Repository repository) throws WrongGitFlowStateException, CoreException {
		this(repository, getFeatureName(repository));
	}

	private PushOperation createPushOperation() throws Exception {
		return new PushOperation(repository, Constants.DEFAULT_REMOTE_NAME, false, 0);
	}

	static Collection<RemoteRefUpdate> copyUpdates(final Collection<RemoteRefUpdate> refUpdates) throws IOException {
		final Collection<RemoteRefUpdate> copy = new ArrayList<RemoteRefUpdate>(refUpdates.size());
		for (final RemoteRefUpdate rru : refUpdates) {
			copy.add(new RemoteRefUpdate(rru, null));
		}
		return copy;
	}

	public void execute(IProgressMonitor monitor) throws CoreException {
		try {
			String branch = getFullFeatureBranchName(featureName);
			Collection<RefSpec> displayedRefSpecs = Collections.singleton(new RefSpec(branch + ":" + branch));
			Collection<RefSpec> fetchSpecs = Collections.singleton(new RefSpec(REFS_HEADS_REFS_REMOTES_ORIGIN));
			final Collection<RemoteRefUpdate> updates = Transport.findRemoteRefUpdatesFor(repository,
					displayedRefSpecs, fetchSpecs);

			final PushOperationSpecification spec = new PushOperationSpecification();
			spec.addURIRefUpdates(uri, copyUpdates(updates));

			createPushOperation().run(monitor);
		} catch (InvocationTargetException e) {
			throw new CoreException(Activator.error(e.getMessage(), e));
		} catch (Exception e) {
			throw new CoreException(Activator.error(e.getMessage(), e));
		}
	}
}
