/*******************************************************************************
 * Copyright (C) 2015, Max Hohenegger <eclipse@hohenegger.eu>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.egit.gitflow.ui.internal.actions;

import java.io.IOException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.egit.gitflow.GitFlowRepository;
import org.eclipse.egit.gitflow.WrongGitFlowStateException;
import org.eclipse.egit.gitflow.op.FeatureFinishOperation;
import org.eclipse.egit.gitflow.ui.internal.UIText;

import static org.eclipse.egit.gitflow.ui.Activator.error;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * git flow feature finish
 */
public class FeatureFinishHandler extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil
				.getCurrentSelection(event);
		PlatformObject firstElement = (PlatformObject) selection
				.getFirstElement();
		Repository repository = (Repository) firstElement
				.getAdapter(Repository.class);
		final GitFlowRepository gfRepo = new GitFlowRepository(repository);

		Job job = new Job(UIText.FeatureFinishHandler_finishingFeature) {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					new FeatureFinishOperation(gfRepo).execute(monitor);
				} catch (WrongGitFlowStateException e) {
					return error(e.getMessage(), e);
				} catch (CoreException e) {
					return error(e.getMessage(), e);
				} catch (IOException e) {
					return error(e.getMessage(), e);
				}
				return Status.OK_STATUS;
			}
		};
		job.setUser(true);
		job.schedule();

		return null;
	}
}