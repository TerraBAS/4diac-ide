/********************************************************************************
 * Copyright (c) 2008 - 2017 Profactor GmbH, TU Wien ACIN, fortiss GmbH
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *  Gerhard Ebenhofer, Alois Zoitl, Ingo Hegny, Monika Wenger
 *    - initial API and implementation and/or initial documentation
 ********************************************************************************/
package org.eclipse.fordiac.ide.model.libraryElement;

import org.eclipse.fordiac.ide.model.Palette.PaletteEntry;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Typed Configureable Object</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fordiac.ide.model.libraryElement.TypedConfigureableObject#getPaletteEntry <em>Palette Entry</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fordiac.ide.model.libraryElement.LibraryElementPackage#getTypedConfigureableObject()
 * @model
 * @generated
 */
public interface TypedConfigureableObject extends ConfigurableObject {
	/**
	 * Returns the value of the '<em><b>Palette Entry</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Palette Entry</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Palette Entry</em>' reference.
	 * @see #setPaletteEntry(PaletteEntry)
	 * @see org.eclipse.fordiac.ide.model.libraryElement.LibraryElementPackage#getTypedConfigureableObject_PaletteEntry()
	 * @model required="true" transient="true"
	 * @generated
	 */
	PaletteEntry getPaletteEntry();

	/**
	 * Sets the value of the '{@link org.eclipse.fordiac.ide.model.libraryElement.TypedConfigureableObject#getPaletteEntry <em>Palette Entry</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Palette Entry</em>' reference.
	 * @see #getPaletteEntry()
	 * @generated
	 */
	void setPaletteEntry(PaletteEntry value);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model kind="operation" required="true"
	 * @generated
	 */
	String getTypeName();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model kind="operation" required="true"
	 * @generated
	 */
	LibraryElement getType();

} // TypedConfigureableObject
