/*******************************************************************************
 * Copyright (c) 2019 Johannes Kepler University Linz
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Bianca Wiesmayr - initial implementation
 *******************************************************************************/
package org.eclipse.fordiac.ide.gef.handlers;

import org.eclipse.fordiac.ide.model.ui.editors.AdvancedScrollingGraphicalViewer;
import org.eclipse.gef.ui.parts.GraphicalViewerKeyHandler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;

public class AdvancedGraphicalViewerKeyHandler extends GraphicalViewerKeyHandler {
	private static final int SCROLL_SPEED = 400;

	public AdvancedGraphicalViewerKeyHandler(final AdvancedScrollingGraphicalViewer viewer) {
		super(viewer);
	}

	@Override
	public boolean keyPressed(final KeyEvent event) {
		final boolean modifierPressed = (event.stateMask & SWT.MODIFIER_MASK) != 0;
		switch (event.keyCode) {
		case SWT.ARROW_DOWN:
			if (!modifierPressed) {
				getViewer().scrollByOffset(0, SCROLL_SPEED);
				return true;
			}
			break;
		case SWT.ARROW_UP:
			if (!modifierPressed) {
				getViewer().scrollByOffset(0, -SCROLL_SPEED);
				return true;
			}
			break;
		case SWT.ARROW_RIGHT:
			if (!modifierPressed) {
				getViewer().scrollByOffset(SCROLL_SPEED, 0);
				return true;
			}
			break;
		case SWT.ARROW_LEFT:
			if (!modifierPressed) {
				getViewer().scrollByOffset(-SCROLL_SPEED, 0);
				return true;
			}
			break;
		case SWT.PAGE_DOWN:
			if ((event.stateMask & SWT.SHIFT) != 0) {
				getViewer().scrollByOffset(getViewer().getFigureCanvasSize().x, 0);
			} else {
				getViewer().scrollByOffset(0, getViewer().getFigureCanvasSize().y);
			}
			return true;
		case SWT.PAGE_UP:
			if ((event.stateMask & SWT.SHIFT) != 0) {
				getViewer().scrollByOffset(-getViewer().getFigureCanvasSize().x, 0);
			} else {
				getViewer().scrollByOffset(0, -getViewer().getFigureCanvasSize().y);
			}
			return true;
		default:
			return super.keyPressed(event);
		}
		return false;
	}

	@Override
	public AdvancedScrollingGraphicalViewer getViewer() {
		return (AdvancedScrollingGraphicalViewer) super.getViewer();
	}
}
