/*******************************************************************************
 * Copyright (C) 2015, Max Hohenegger <eclipse@hohenegger.eu>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.egit.gitflow.ui.internal.validation;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.egit.gitflow.BranchNameValidator;
import org.eclipse.jface.dialogs.IInputValidator;

abstract public class NameValidator implements IInputValidator {
	public String isValid(String newText) {
		try {
			if (branchExists(newText)) {
				return String.format("Name '%s' already exists", newText);
			}
			if (!BranchNameValidator.isBranchNameValid(newText)) {
				return String.format("'%s' is not a valid name. None of the following characters is allowed: '%s'",
						newText, BranchNameValidator.ILLEGAL_CHARS);
			}
		} catch (CoreException e) {
			return null;
		}
		return null;
	}

	abstract protected boolean branchExists(String newText) throws CoreException;
}