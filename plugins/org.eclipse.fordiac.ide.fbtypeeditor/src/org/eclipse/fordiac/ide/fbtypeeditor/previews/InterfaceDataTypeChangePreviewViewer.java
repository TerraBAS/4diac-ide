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
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Polyline;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fordiac.ide.fbtypeeditor.editparts.FBInterfaceEditPartFactory;
import org.eclipse.fordiac.ide.fbtypeeditor.editparts.InterfaceEditPart;
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
	private Composite parent;

	private AdvancedScrollingGraphicalViewer graphicalViewerLeft;
	private AdvancedScrollingGraphicalViewer graphicalViewerRight;

	@Override
	public void createControl(final Composite parent) {
		parent.setLayout(new GridLayout(1, false));

		this.parent = parent;

		resetControl();
	}

	private void resetControl() {
		if (control != null) {
			control.dispose();
		}

		control = new SashForm(parent, SWT.VERTICAL);
		control.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

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

		resetControl();

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

		drawRedRectangleForElement(graphicalViewerLeft, oldTypeEntry.getTypeName());

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

	private void drawRedRectangleForElement(final AdvancedScrollingGraphicalViewer viewer,
			final String originalTypeName) {
		if (viewer != null) {
			viewer.getEditPartRegistry().forEach((key, val) -> {

				if ((val instanceof final InterfaceEditPart part)
						&& part.getCastedModel().getFullTypeName().equals(originalTypeName)) {
					System.out.println("Found matching InterfaceEditPart: " + originalTypeName);

					final IFigure figure = part.getFigure();
					if (figure != null) {
						// Ensure layout is complete before accessing bounds
						viewer.getControl().getDisplay().asyncExec(() -> {
							final Rectangle bounds = figure.getBounds();
							figure.translateToAbsolute(bounds);

							createRedBorder(viewer, bounds);

							final FigureCanvas canvas = (FigureCanvas) viewer.getControl();
							if (canvas != null) {
								canvas.scrollTo(bounds.x - 10, bounds.y - 10);
							}
						});
					}
				}
			});
		}
	}

	private void createRedBorder(final AdvancedScrollingGraphicalViewer viewer, final Rectangle bounds) {
		final int padding = 2;

		final Polyline topLine = new Polyline();
		topLine.addPoint(new Point(bounds.x - padding, bounds.y - padding));
		topLine.addPoint(new Point(bounds.x + bounds.width + padding, bounds.y - padding));
		topLine.setForegroundColor(ColorConstants.red);
		topLine.setLineWidth(2);

		final Polyline bottomLine = new Polyline();
		bottomLine.addPoint(new Point(bounds.x - padding, bounds.y + bounds.height + padding));
		bottomLine.addPoint(new Point(bounds.x + bounds.width + padding, bounds.y + bounds.height + padding));
		bottomLine.setForegroundColor(ColorConstants.red);
		bottomLine.setLineWidth(2);

		final Polyline leftLine = new Polyline();
		leftLine.addPoint(new Point(bounds.x - padding, bounds.y - padding));
		leftLine.addPoint(new Point(bounds.x - padding, bounds.y + bounds.height + padding));
		leftLine.setForegroundColor(ColorConstants.red);
		leftLine.setLineWidth(2);

		final Polyline rightLine = new Polyline();
		rightLine.addPoint(new Point(bounds.x + bounds.width + padding, bounds.y - padding));
		rightLine.addPoint(new Point(bounds.x + bounds.width + padding, bounds.y + bounds.height + padding));
		rightLine.setForegroundColor(ColorConstants.red);
		rightLine.setLineWidth(2);

		final FigureCanvas canvas = (FigureCanvas) viewer.getControl();
		if (canvas != null) {
			canvas.getContents().add(topLine);
			canvas.getContents().add(bottomLine);
			canvas.getContents().add(leftLine);
			canvas.getContents().add(rightLine);
		}
	}

}