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
package org.eclipse.fordiac.ide.fbtypeeditor.previews;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.fordiac.ide.fbtypeeditor.editparts.FBInterfaceEditPartFactory;
import org.eclipse.fordiac.ide.model.ui.editors.AdvancedScrollingGraphicalViewer;
import org.eclipse.fordiac.ide.typemanagement.refactoring.InterfaceDataTypeChange;
import org.eclipse.gef.ui.parts.GraphicalEditor;
import org.eclipse.ltk.ui.refactoring.ChangePreviewViewerInput;
import org.eclipse.ltk.ui.refactoring.IChangePreviewViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

@SuppressWarnings("restriction")
public class InterfaceDataTypeChangePreviewViewer implements IChangePreviewViewer {

	private SashForm control;
	private AdvancedScrollingGraphicalViewer graphicalViewerLeft;
	private AdvancedScrollingGraphicalViewer graphicalViewerRight;

	@Override
	public void createControl(final Composite parent) {
		// Use GridLayout instead of FillLayout
		parent.setLayout(new GridLayout());
		control = new SashForm(parent, SWT.HORIZONTAL);

		// Use GridData to make the SashForm fill all available space
		control.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		// Add left and right composites
		final Composite compl = new Composite(control, SWT.NONE);
		compl.setLayout(new GridLayout());
		final Label labell = new Label(compl, SWT.NONE);
		labell.setText("Before refactor");
		graphicalViewerLeft = new AdvancedScrollingGraphicalViewer();
		graphicalViewerLeft.createControl(compl);
		graphicalViewerLeft.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		final Composite compr = new Composite(control, SWT.NONE);
		compr.setLayout(new GridLayout());
		final Label labelr = new Label(compr, SWT.NONE);
		labelr.setText("After refactor");
		graphicalViewerRight = new AdvancedScrollingGraphicalViewer();
		graphicalViewerRight.createControl(compr);
		graphicalViewerRight.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		// todo lotsa things
		control.setWeights(50, 50);
	}

	@Override
	public Control getControl() {
		return control;
	}

	@Override
	public void setInput(final ChangePreviewViewerInput input) {
		if (!(input.getChange() instanceof InterfaceDataTypeChange)) {
			return;
		}

		final InterfaceDataTypeChange change = (InterfaceDataTypeChange) input.getChange();

		final GraphicalEditor mockEditor = new GraphicalEditor() {

			@Override
			protected void initializeGraphicalViewer() {
				// No initialization needed for mock
			}

			@Override
			public void doSave(final IProgressMonitor monitor) {
				// No save operation needed for mock
			}
		};

		graphicalViewerLeft.setEditPartFactory(
				new FBInterfaceEditPartFactory(mockEditor, change.getOldTypeEntry().getTypeLibrary()));

		// Set the contents to the modified element
		graphicalViewerLeft.setContents(change.getModifiedElement());

		graphicalViewerRight.setEditPartFactory(
				new FBInterfaceEditPartFactory(mockEditor, change.getOldTypeEntry().getTypeLibrary()));

		// Set the contents to the modified element
		graphicalViewerRight.setContents(change.getModifiedElement());

	}

}