/*******************************************************************************
 * Copyright (C) 2015, Max Hohenegger <eclipse@hohenegger.eu>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.egit.gitflow.ui.internal.actions;

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
import org.eclipse.egit.gitflow.op.ReleaseStartOperation;
import org.eclipse.egit.gitflow.ui.Activator;
import org.eclipse.egit.gitflow.ui.internal.validation.ReleaseNameValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.ui.handlers.HandlerUtil;

public class ReleaseStartHandler extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getCurrentSelection(event);
		PlatformObject firstElement = (PlatformObject) selection.getFirstElement();
		Repository repository = (Repository) firstElement.getAdapter(Repository.class);
		final GitFlowRepository gfRepo = new GitFlowRepository(repository);

		InputDialog inputDialog = new InputDialog(HandlerUtil.getActiveShell(event), "Provide release name",
				"Please provide a name for the new release.", "", new ReleaseNameValidator(gfRepo));

		if (inputDialog.open() != Window.OK) {
			return null;
		}

		final String releaseName = inputDialog.getValue();

		Job job = new Job("Starting new Release...") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					new ReleaseStartOperation(gfRepo, releaseName).execute(monitor);
				} catch (CoreException e) {
					return Activator.error(e.getMessage(), e);
				}
				return Status.OK_STATUS;
			}
		};
		job.setUser(true);
		job.schedule();

		return null;
	}
}
