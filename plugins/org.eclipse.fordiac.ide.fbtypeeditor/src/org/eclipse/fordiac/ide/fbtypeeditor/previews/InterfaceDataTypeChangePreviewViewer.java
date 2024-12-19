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
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fordiac.ide.fbtypeeditor.editparts.FBInterfaceEditPartFactory;
import org.eclipse.fordiac.ide.model.libraryElement.FBType;
import org.eclipse.fordiac.ide.model.typelibrary.TypeEntry;
import org.eclipse.fordiac.ide.model.ui.editors.AdvancedScrollingGraphicalViewer;
import org.eclipse.fordiac.ide.typemanagement.refactoring.InterfaceDataTypeChange;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.editparts.ScalableRootEditPart;
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
public class InterfaceDataTypeChangePreviewViewer implements IChangePreviewViewer {

	private SashForm control;
	private AdvancedScrollingGraphicalViewer graphicalViewerLeft;
	private AdvancedScrollingGraphicalViewer graphicalViewerRight;

	@Override
	public void createControl(final Composite parent) {
		// Ensure parent layout is set correctly
		parent.setLayout(new GridLayout(1, false));

		control = new SashForm(parent, SWT.VERTICAL);
		control.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		// Left Composite
		final Composite compl = new Composite(control, SWT.NONE);
		compl.setLayout(new GridLayout());
		final Label labell = new Label(compl, SWT.NONE);
		labell.setText("Before refactor");
		graphicalViewerLeft = new AdvancedScrollingGraphicalViewer();
		graphicalViewerLeft.createControl(compl);
		graphicalViewerLeft.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		// Right Composite
		final Composite compr = new Composite(control, SWT.NONE);
		compr.setLayout(new GridLayout());
		final Label labelr = new Label(compr, SWT.NONE);
		labelr.setText("After refactor");
		graphicalViewerRight = new AdvancedScrollingGraphicalViewer();
		graphicalViewerRight.createControl(compr);
		graphicalViewerRight.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		// Force parent layout
		parent.layout(true, true);

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

		final String newName = change.getNewName();
		final TypeEntry oldTypeEntry = change.getOldTypeEntry();
		final FBType originalFbType = (FBType) change.getModifiedElement();
		final FBType refactoredFbType = EcoreUtil.copy(originalFbType);

		refactoredFbType.getInterfaceList().getInputs()
				.filter(var -> var.getTypeName().equals(oldTypeEntry.getTypeName())).forEach(var -> {
					var.setType(EcoreUtil.copy(var.getType()));
					var.getType().setName(newName);
				});

		refactoredFbType.getInterfaceList().getOutputs()
				.filter(var -> var.getTypeName().equals(oldTypeEntry.getTypeName())).forEach(var -> {
					var.setType(EcoreUtil.copy(var.getType()));
					var.getType().setName(newName);
				});

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

		graphicalViewerLeft.setRootEditPart(new ScalableRootEditPart());
		graphicalViewerLeft.setEditPartFactory(
				new FBInterfaceEditPartFactory(mockEditor, change.getOldTypeEntry().getTypeLibrary()));
		graphicalViewerLeft.setContents(change.getModifiedElement());

		graphicalViewerRight.setRootEditPart(new ScalableRootEditPart());
		graphicalViewerRight.setEditPartFactory(
				new FBInterfaceEditPartFactory(mockEditor, change.getOldTypeEntry().getTypeLibrary()));
		graphicalViewerRight.setContents(refactoredFbType);

		synchronizeScrolling(graphicalViewerLeft, graphicalViewerRight);
		synchronizeScrolling(graphicalViewerRight, graphicalViewerLeft);

		final GraphicalEditPart gEditPartR = (GraphicalEditPart) graphicalViewerRight.getContents();

		gEditPartR.setLayoutConstraint(gEditPartR.getChildren().get(0), gEditPartR.getChildren().get(0).getFigure(),
				new Rectangle(400, 50, -1, -1));

		final GraphicalEditPart gEditPartL = (GraphicalEditPart) graphicalViewerLeft.getContents();

		gEditPartL.setLayoutConstraint(gEditPartL.getChildren().get(0), gEditPartL.getChildren().get(0).getFigure(),
				new Rectangle(400, 50, -1, -1));

		// todo remove grid

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