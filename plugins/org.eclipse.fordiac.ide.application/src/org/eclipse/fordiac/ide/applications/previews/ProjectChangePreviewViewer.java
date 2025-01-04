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
package org.eclipse.fordiac.ide.applications.previews;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.fordiac.ide.application.editparts.ElementEditPartFactory;
import org.eclipse.fordiac.ide.model.libraryElement.AutomationSystem;
import org.eclipse.fordiac.ide.model.libraryElement.FBNetwork;
import org.eclipse.fordiac.ide.model.ui.editors.AdvancedScrollingGraphicalViewer;
import org.eclipse.fordiac.ide.systemmanagement.SystemManager;
import org.eclipse.fordiac.ide.typemanagement.refactoring.UpdateInstancesChange;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
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
import org.eclipse.swt.widgets.ScrollBar;

@SuppressWarnings("restriction")
public class ProjectChangePreviewViewer implements IChangePreviewViewer {

	private SashForm control;
	private AdvancedScrollingGraphicalViewer graphicalViewerLeft;
	private AdvancedScrollingGraphicalViewer graphicalViewerRight;

	@Override
	public void createControl(final Composite parent) {
		System.out.println("Creating Project Change preview");

		// Ensure parent layout is set correctly
		parent.setLayout(new GridLayout(1, false));

		control = new SashForm(parent, SWT.VERTICAL);
		control.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		// Left Composite
		final Composite compl = new Composite(control, SWT.NONE);
		compl.setLayout(new GridLayout());
		final Label labell = new Label(compl, SWT.NONE);
		graphicalViewerLeft = new AdvancedScrollingGraphicalViewer();
		graphicalViewerLeft.createControl(compl);
		graphicalViewerLeft.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		// Force parent layout
		parent.layout(true, true);

	}

	@Override
	public Control getControl() {
		return control;
	}

	@Override
	public void setInput(final ChangePreviewViewerInput input) {

		System.out.println("Creating Project Change preview");
		if (!(input.getChange() instanceof UpdateInstancesChange)) {
			return;
		}

		final UpdateInstancesChange change = (UpdateInstancesChange) input.getChange();

		final AutomationSystem system = SystemManager.INSTANCE.getProjectSystems(change.getProject()).getFirst();

		final FBNetwork network = system.getApplication().getFirst().getFBNetwork();

		final GraphicalEditor mockEditor = new GraphicalEditor() {

			@Override
			protected void initializeGraphicalViewer() {
				// No initialization needed for mock
			}

			@Override
			public void doSave(final IProgressMonitor monitor) {
				// No save final operation needed for mock

			}

		};

		graphicalViewerLeft.setRootEditPart(new ScalableFreeformRootEditPart());
		graphicalViewerLeft.setEditPartFactory(new ElementEditPartFactory(mockEditor));
		graphicalViewerLeft.setContents(network);

	}

	private void synchronizeScrolling(final AdvancedScrollingGraphicalViewer source,
			final AdvancedScrollingGraphicalViewer target) {
		if (source.getControl() instanceof FigureCanvas && target.getControl() instanceof FigureCanvas) {
			final FigureCanvas sourceCanvas = (FigureCanvas) source.getControl();
			final FigureCanvas targetCanvas = (FigureCanvas) target.getControl();

			final ScrollBar sourceHBar = sourceCanvas.getHorizontalBar();
			if (sourceHBar != null) {
				sourceHBar.addListener(SWT.Selection, event -> {
					final ScrollBar targetHBar = targetCanvas.getHorizontalBar();
					if (targetHBar != null) {
						targetHBar.setSelection(sourceHBar.getSelection());
						targetCanvas.scrollTo(sourceCanvas.getHorizontalBar().getSelection(),
								sourceCanvas.getVerticalBar().getSelection());
					}
				});
			}

			final ScrollBar sourceVBar = sourceCanvas.getVerticalBar();
			if (sourceVBar != null) {
				sourceVBar.addListener(SWT.Selection, event -> {
					final ScrollBar targetVBar = targetCanvas.getVerticalBar();
					if (targetVBar != null) {
						targetVBar.setSelection(sourceVBar.getSelection());
						targetCanvas.scrollTo(sourceCanvas.getHorizontalBar().getSelection(),
								sourceCanvas.getVerticalBar().getSelection());
					}
				});
			}
		}
	}

}