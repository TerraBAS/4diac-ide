/*******************************************************************************
 * Copyright (c) 2023 Martin Erich Jobst
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Martin Jobst - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.fordiac.ide.deployment.debug.ui.preferences;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.fordiac.ide.deployment.debug.preferences.DeploymentDebugPreferences;
import org.eclipse.fordiac.ide.deployment.debug.ui.Messages;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

public class DeploymentDebugPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public DeploymentDebugPreferencePage() {
		super(GRID);
	}

	@Override
	public void init(final IWorkbench workbench) {
		setPreferenceStore(new ScopedPreferenceStore(InstanceScope.INSTANCE, DeploymentDebugPreferences.QUALIFIER));
		setDescription(Messages.DeploymentDebugPreferencePage_Description);
	}

	@Override
	public void createFieldEditors() {
		addField(new BooleanFieldEditor(DeploymentDebugPreferences.MONITORING_VALUE_WRITE_THROUGH,
				Messages.DeploymentDebugPreferencePage_MonitoringValueWriteThrough, getFieldEditorParent()));
		final IntegerFieldEditor monitoringValueTransparencyEditor = new IntegerFieldEditor(
				DeploymentDebugPreferences.MONITORING_VALUE_TRANSPARENCY,
				Messages.DeploymentDebugPreferencePage_MonitoringValueTransparency, getFieldEditorParent());
		monitoringValueTransparencyEditor.setValidRange(0, 255);
		addField(monitoringValueTransparencyEditor);
	}
}