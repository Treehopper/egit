/*******************************************************************************
 * Copyright (C) 2015, Max Hohenegger <eclipse@hohenegger.eu>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.egit.gitflow.ui.internal.actions;

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.egit.core.op.BranchOperation;
import org.eclipse.egit.gitflow.GitFlowRepository;
import org.eclipse.egit.gitflow.ui.Activator;
import org.eclipse.egit.gitflow.ui.internal.dialog.AbstractSelectionDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.ui.handlers.HandlerUtil;

@SuppressWarnings("restriction")
public class FeatureCheckoutHandler extends AbstractHandler {
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getCurrentSelection(event);
		PlatformObject firstElement = (PlatformObject) selection.getFirstElement();
		final Repository repository = (Repository) firstElement.getAdapter(Repository.class);
		final GitFlowRepository gfRepo = new GitFlowRepository(repository);

		final List<Ref> refs = gfRepo.getFeatureBranches();


		AbstractSelectionDialog<Ref> dialog = new AbstractSelectionDialog<Ref>(HandlerUtil.getActiveShell(event), refs,
				"Select Feature", "Local features:") {
			@Override
			protected String getPrefix() {
				return Constants.R_HEADS + gfRepo.getFeaturePrefix();
			}
		};
		if (dialog.open() != Window.OK) {
			return null;
		}
		final Ref ref = dialog.getSelectedNode();

		Job trackingJob = new Job("Checking out feature...") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					new BranchOperation(repository, ref.getName()).execute(monitor);
				} catch (CoreException e) {
					return Activator.error(e.getMessage(), e);
				}
				return Status.OK_STATUS;
			}
		};
		trackingJob.setUser(true);
		trackingJob.schedule();

		return null;
	}
}
