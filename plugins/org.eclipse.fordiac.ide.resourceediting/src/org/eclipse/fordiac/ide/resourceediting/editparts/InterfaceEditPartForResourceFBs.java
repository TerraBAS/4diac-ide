/*******************************************************************************
 * Copyright (c) 2008 - 2016 Profactor GmbH, TU Wien ACIN, fortiss GmbH
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Gerhard Ebenhofer, Alois Zoitl, Monika Wenger
 *     - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.fordiac.ide.resourceediting.editparts;

import org.eclipse.draw2d.AncestorListener;
import org.eclipse.draw2d.IFigure;
import org.eclipse.fordiac.ide.gef.editparts.InterfaceEditPart;
import org.eclipse.fordiac.ide.resourceediting.policies.EventNodeEditPolicyForResourceFBs;
import org.eclipse.fordiac.ide.resourceediting.policies.VariableNodeEditPolicyForResourceFBs;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy;

public class InterfaceEditPartForResourceFBs extends InterfaceEditPart {

	@Override
	public void setSelected(int value) {
		super.setSelected(value);
	}
	
	@Override
	protected IFigure createFigure() {
		IFigure fig = super.createFigure();
		fig.addAncestorListener(new AncestorListener() {

			@Override
			public void ancestorRemoved(IFigure ancestor) {

			}

			@Override
			public void ancestorMoved(IFigure ancestor) {
				updateReferenced();
			}

			@Override
			public void ancestorAdded(IFigure ancestor) {
			}
		});

		return fig;
	}

	@Override
	protected GraphicalNodeEditPolicy getNodeEditPolicy() {
		if (isEvent()) {
			return new EventNodeEditPolicyForResourceFBs();
		}
		if (isVariable()) {
			return new VariableNodeEditPolicyForResourceFBs();
		}
		return null;
	}

	private void updateReferenced() {
		EditPart parent = getParent();
		while (parent != null && !(parent instanceof FBNetworkContainerEditPart)) {
			parent = parent.getParent();
		}
		if (parent != null) {
			FBNetworkContainerEditPart fbcep = (FBNetworkContainerEditPart) parent;
			VirtualIO referencedElement = fbcep.getVirtualIOElement(getModel());
			if (referencedElement != null) {
				Object o = getViewer().getEditPartRegistry().get(referencedElement);
				if (o instanceof VirtualInOutputEditPart) {
					((VirtualInOutputEditPart) o).updatePos(this);
				}
			}
		}
	}

}
