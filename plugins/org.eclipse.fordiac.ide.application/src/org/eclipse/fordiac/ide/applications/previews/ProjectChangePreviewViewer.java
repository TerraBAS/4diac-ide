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
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Polyline;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.fordiac.ide.application.editparts.ElementEditPartFactory;
import org.eclipse.fordiac.ide.model.libraryElement.AutomationSystem;
import org.eclipse.fordiac.ide.model.libraryElement.FBNetwork;
import org.eclipse.fordiac.ide.model.libraryElement.FBNetworkElement;
import org.eclipse.fordiac.ide.model.ui.editors.AdvancedScrollingGraphicalViewer;
import org.eclipse.fordiac.ide.systemmanagement.SystemManager;
import org.eclipse.fordiac.ide.typemanagement.refactoring.UpdateInstancesChange;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
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

@SuppressWarnings("restriction")
public class ProjectChangePreviewViewer implements IChangePreviewViewer {

	private SashForm control;
	private Composite parent;

	private AdvancedScrollingGraphicalViewer graphicalViewer;

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
		graphicalViewer = new AdvancedScrollingGraphicalViewer();
		graphicalViewer.createControl(compl);
		graphicalViewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		parent.layout(true, true);
	}

	@Override
	public Control getControl() {
		return control;
	}

	@Override
	public void setInput(final ChangePreviewViewerInput input) {
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

		resetControl();

		graphicalViewer.setRootEditPart(new ScalableFreeformRootEditPart());
		graphicalViewer.setEditPartFactory(new ElementEditPartFactory(mockEditor));
		graphicalViewer.setContents(network); // exception here

		drawRedRectangleForElement(graphicalViewer, change.getFBNetworkElement());
	}

	private void resetGraphicalViewer(final AdvancedScrollingGraphicalViewer viewer) {
		if (viewer != null) {
			viewer.setContents(null);
			if (viewer.getControl() != null) {
				viewer.getControl().setRedraw(false);
				viewer.getControl().dispose();
			}
		}
	}

	private void drawRedRectangleForElement(final AdvancedScrollingGraphicalViewer viewer, final FBNetworkElement fb) {
		if (viewer != null && fb != null) {
			final EditPart editPart = viewer.getEditPartRegistry().get(fb);

			if (editPart != null) {
				final IFigure figure = ((AbstractGraphicalEditPart) editPart).getFigure();
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
		}
	}

	private void createRedBorder(final AdvancedScrollingGraphicalViewer viewer, final Rectangle bounds) {
		final int padding = 5;

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