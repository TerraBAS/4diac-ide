/*******************************************************************************
 * Copyright (c) 2024 Johannes Kepler University
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Martin Schwarz - initial implementation
 *******************************************************************************/
package org.eclipse.fordiac.ide.typemanagement.refactoring.rename;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.fordiac.ide.model.typelibrary.TypeEntry;
import org.eclipse.fordiac.ide.model.typelibrary.TypeLibraryManager;
import org.eclipse.ltk.core.refactoring.CompositeChange;

public class ProjectFbInstanceChange extends CompositeChange {

	private final IFile file;
	private final IProject project;
	private final TypeEntry oldTypeEntry;

	public ProjectFbInstanceChange(final IFile file, final IProject project) {
		super(project.getName());
		this.file = file;
		this.project = project;
		this.oldTypeEntry = TypeLibraryManager.INSTANCE.getTypeEntryForFile(file);
	}

	public IProject getProject() {
		return project;
	}

}
