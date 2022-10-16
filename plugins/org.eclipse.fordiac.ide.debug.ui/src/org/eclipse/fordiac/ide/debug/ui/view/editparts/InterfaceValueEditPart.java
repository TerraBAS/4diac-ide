/*******************************************************************************
 * Copyright (c)  2012 - 2022 Profactor GmbH, fortiss GmbH,
 * 							  Primetals Technologies Austria GmbH
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Gerhard Ebenhofer, Alois Zoitl, Monika Wenger - initial implementation
 *   Alois Zoitl - extracted createFigure and position calculation from
 *                 TestEditPart from the FBTester to be used here
 *******************************************************************************/
package org.eclipse.fordiac.ide.debug.ui.view.editparts;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.fordiac.ide.model.eval.value.Value;
import org.eclipse.fordiac.ide.model.libraryElement.IInterfaceElement;

public class InterfaceValueEditPart extends AbstractDebugInterfaceValueEditPart {

	@Override
	public InterfaceValueEntity getModel() {
		return (InterfaceValueEntity) super.getModel();
	}

	@Override
	protected IFigure createFigure() {
		final Label l = (Label) super.createFigure();
		l.setText(getModel().getVariable().getValue().toString());
		return l;
	}

	@Override
	protected IInterfaceElement getInterfaceElement() {
		return getModel().getInterfaceElement();
	}

	public void setValue(final Value value) {
		if (isActive() && getFigure() != null) {
			getFigure().setText(value.toString());
			refreshVisuals();
		}
	}

}