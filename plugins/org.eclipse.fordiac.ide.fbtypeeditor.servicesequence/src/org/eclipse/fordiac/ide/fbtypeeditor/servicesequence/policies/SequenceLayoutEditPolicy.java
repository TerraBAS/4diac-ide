/*******************************************************************************
 * Copyright (c) 2014 - 2015 fortiss GmbH
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Monika Wenger
 *     - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.fordiac.ide.fbtypeeditor.servicesequence.policies;

import org.eclipse.fordiac.ide.fbtypeeditor.servicesequence.commands.CreateTransactionCommand;
import org.eclipse.fordiac.ide.fbtypeeditor.servicesequence.commands.MoveTransactionCommand;
import org.eclipse.fordiac.ide.fbtypeeditor.servicesequence.editparts.TransactionEditPart;
import org.eclipse.fordiac.ide.gef.policies.EmptyXYLayoutEditPolicy;
import org.eclipse.fordiac.ide.model.libraryElement.Primitive;
import org.eclipse.fordiac.ide.model.libraryElement.ServiceSequence;
import org.eclipse.fordiac.ide.model.libraryElement.ServiceTransaction;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.gef.requests.CreateRequest;

public class SequenceLayoutEditPolicy extends EmptyXYLayoutEditPolicy {

	@Override
	protected Command createChangeConstraintCommand(final ChangeBoundsRequest request, final EditPart child, final Object constraint) {
		EditPart after;
		if (child instanceof TransactionEditPart) {
			final ServiceSequence target = (ServiceSequence) getHost().getModel();
			after = getInsertionReference(request.getLocation());
			int newindex = -1;
			if (after != null) {
				if (after.getModel() instanceof ServiceTransaction) {
					final ServiceTransaction refElement = (ServiceTransaction) after.getModel();
					newindex = target.getServiceTransaction().indexOf(refElement);
				} else {
					if (after.getModel() instanceof Primitive) {
						final ServiceTransaction refElement = ((TransactionEditPart) after.getParent()).getModel();
						newindex = target.getServiceTransaction().indexOf(refElement);
					}
				}
				if (newindex > -1) {
					return new MoveTransactionCommand((ServiceTransaction) child.getModel(),
							target.getServiceTransaction().indexOf(child.getModel()), newindex);
				}
			}
		}
		return null;
	}

	@Override
	protected Command getCreateCommand(final CreateRequest request) {
		final Object model = getHost().getModel();
		if (model instanceof ServiceSequence) {
			return new CreateTransactionCommand((ServiceSequence) model);
		}
		return null;
	}

}
