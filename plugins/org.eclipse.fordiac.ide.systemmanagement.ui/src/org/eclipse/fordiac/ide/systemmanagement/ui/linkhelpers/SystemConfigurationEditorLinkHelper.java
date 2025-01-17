/*******************************************************************************
 * Copyright (c) 2015, 2016, 2018 fortiss GmbH, Johannes Kepler University
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
package org.eclipse.fordiac.ide.systemmanagement.ui.linkhelpers;

import org.eclipse.fordiac.ide.model.libraryElement.Device;
import org.eclipse.fordiac.ide.model.libraryElement.Segment;
import org.eclipse.fordiac.ide.model.libraryElement.SystemConfiguration;
import org.eclipse.fordiac.ide.model.ui.editors.HandlerHelper;
import org.eclipse.fordiac.ide.systemconfiguration.editor.SystemConfigurationEditorInput;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;

public class SystemConfigurationEditorLinkHelper extends AbstractEditorLinkHelper {

	@Override
	public IStructuredSelection findSelection(final IEditorInput anInput) {
		if (anInput instanceof SystemConfigurationEditorInput) {
			final SystemConfigurationEditorInput sysConfInput = (SystemConfigurationEditorInput) anInput;
			return new StructuredSelection(sysConfInput.getContent());
		}
		return StructuredSelection.EMPTY;
	}

	@Override
	public void activateEditor(final IWorkbenchPage aPage, final IStructuredSelection aSelection) {
		if (aSelection == null || aSelection.isEmpty()) {
			return;
		}

		SystemConfiguration sysConf = null;
		Object refObject = null;
		if (aSelection.getFirstElement() instanceof SystemConfiguration) {
			sysConf = (SystemConfiguration) aSelection.getFirstElement();
		} else if (aSelection.getFirstElement() instanceof Device) {
			refObject = aSelection.getFirstElement();
			sysConf = ((Device) refObject).getSystemConfiguration();
		} else if (aSelection.getFirstElement() instanceof Segment) {
			refObject = aSelection.getFirstElement();
			sysConf = (SystemConfiguration) ((Segment) refObject).eContainer();
		}

		if (null != sysConf) {
			final IEditorInput sysConfInput = new SystemConfigurationEditorInput(sysConf);
			final IEditorPart editor = activateEditor(aPage, sysConfInput);
			HandlerHelper.selectElement(refObject, editor);
		}

	}

}
