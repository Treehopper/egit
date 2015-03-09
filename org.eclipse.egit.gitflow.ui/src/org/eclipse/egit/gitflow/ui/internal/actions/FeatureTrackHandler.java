/*******************************************************************************
 * Copyright (C) 2015, Max Hohenegger <eclipse@hohenegger.eu>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.egit.gitflow.ui.internal.actions;

import java.util.ArrayList;
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
import org.eclipse.egit.gitflow.op.FeatureListOperation;
import org.eclipse.egit.gitflow.op.FeatureTrackOperation;
import org.eclipse.egit.gitflow.ui.Activator;
import org.eclipse.egit.ui.UIPreferences;
import org.eclipse.egit.ui.internal.dialogs.BranchSelectionDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.swt.SWT;
import org.eclipse.ui.handlers.HandlerUtil;

@SuppressWarnings("restriction")
public class FeatureTrackHandler extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getCurrentSelection(event);
		PlatformObject firstElement = (PlatformObject) selection.getFirstElement();
		final Repository repository = (Repository) firstElement.getAdapter(Repository.class);

		final List<Ref> refs = new ArrayList<Ref>();

		Job listingJob = new Job("Fetching remote features...") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					int timeout = Activator.getDefault().getPreferenceStore()
							.getInt(UIPreferences.REMOTE_CONNECTION_TIMEOUT);
					FeatureListOperation featureListOperation = new FeatureListOperation(repository, timeout);
					featureListOperation.execute(monitor);
					refs.addAll(featureListOperation.getResult());
				} catch (CoreException e) {
					return Activator.error(e.getMessage(), e);
				}
				return Status.OK_STATUS;
			}
		};
		listingJob.setUser(true);
		listingJob.schedule();
		try {
			listingJob.join();
			if (!listingJob.getResult().isOK()) {
				return null;
			}
		} catch (InterruptedException e) {
			throw new ExecutionException(e.getMessage(), e);
		}

		BranchSelectionDialog<Ref> dialog = new BranchSelectionDialog<Ref>(HandlerUtil.getActiveShell(event), refs,
				"Select Feature", "Remote features:", SWT.NONE);
		if (dialog.open() != Window.OK) {
			return null;
		}
		final Ref ref = dialog.getSelectedNode();

		Job trackingJob = new Job("Tracking feature...") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					new FeatureTrackOperation(repository, ref).execute(monitor);
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
