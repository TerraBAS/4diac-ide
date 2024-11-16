/*******************************************************************************
 * Copyright (c) 2024 Primetals Technologies Austria GmbH
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Michael Oberlehner
 *     - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.fordiac.ide.typemanagement.refactoring;

import java.util.EnumSet;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;

public abstract class ConfigurableChange<T extends EObject> extends AbstractCommandChange<T>
		implements IFordiacPreviewChange {

	private final EnumSet<ChangeState> state;

	protected ConfigurableChange(final String name, final URI elementURI, final Class elementClass) {
		super(name, elementURI, elementClass);
		this.state = getDefaultSelection();
		initEnablement();
	}

	protected ConfigurableChange(final URI elementURI, final Class elementClass) {
		super(elementURI, elementClass);
		this.state = getDefaultSelection();
		initEnablement();
	}

	private void initEnablement() {
		if (getDefaultSelection().contains(ChangeState.NO_CHANGE)) {
			setEnabled(false);
		}
	}

	@Override
	public EnumSet<ChangeState> getState() {
		return state;
	}

	@Override
	public abstract EnumSet<ChangeState> getAllowedChoices();

	@Override
	public void addState(final ChangeState newState) {
		state.add(newState);
	}

	@Override
	public EnumSet<ChangeState> getDefaultSelection() {
		return EnumSet.of(ChangeState.NO_CHANGE);
	}
}
