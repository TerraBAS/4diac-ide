/*******************************************************************************
 * Copyright (c) 2012 fortiss GmbH
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Alois Zoitl
 *     - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.fordiac.ide.model.commands.change;

import org.eclipse.fordiac.ide.model.libraryElement.VersionInfo;
import org.eclipse.gef.commands.Command;

/**
 * The Class ChangeOrganizationCommand.
 */
public class ChangeOrganizationCommand extends Command {
	
	private VersionInfo versionInfo;
	
	/** The new ApplicationDomain value. */
	private String newOrganization;

	/** The old ApplicationDomain value. */
	private String oldOrganization;

	public ChangeOrganizationCommand(final VersionInfo versionInfo, final String newOrganization) {
		super();
		this.versionInfo = versionInfo;
		this.newOrganization = newOrganization;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	@Override
	public void execute() {
		oldOrganization = versionInfo.getOrganization();
		redo();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	@Override
	public void undo() {
		versionInfo.setOrganization(oldOrganization);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.commands.Command#redo()
	 */
	@Override
	public void redo() {
		versionInfo.setOrganization(newOrganization);
	}

}
