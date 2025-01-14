/**
 * *******************************************************************************
 * Copyright (c) 2008 - 2018 Profactor GmbH, TU Wien ACIN, fortiss GmbH
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *    Gerhard Ebenhofer, Alois Zoitl, Ingo Hegny, Monika Wenger, Martin Jobst
 *      - initial API and implementation and/or initial documentation
 * *******************************************************************************
 */
package org.eclipse.fordiac.ide.model.Palette.util;

import java.util.Map;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;

import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.fordiac.ide.model.Palette.*;

/** <!-- begin-user-doc --> The <b>Adapter Factory</b> for the model. It provides an adapter <code>createXXX</code>
 * method for each class of the model. <!-- end-user-doc -->
 * 
 * @see org.eclipse.fordiac.ide.model.Palette.PalettePackage
 * @generated */
public class PaletteAdapterFactory extends AdapterFactoryImpl {
	/** The cached model package. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated */
	protected static PalettePackage modelPackage;

	/** Creates an instance of the adapter factory. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated */
	public PaletteAdapterFactory() {
		if (modelPackage == null) {
			modelPackage = PalettePackage.eINSTANCE;
		}
	}

	/** Returns whether this factory is applicable for the type of the object. <!-- begin-user-doc --> This
	 * implementation returns <code>true</code> if the object is either the model's package or is an instance object of
	 * the model. <!-- end-user-doc -->
	 * 
	 * @return whether this factory is applicable for the type of the object.
	 * @generated */
	@Override
	public boolean isFactoryForType(Object object) {
		if (object == modelPackage) {
			return true;
		}
		if (object instanceof EObject) {
			return ((EObject) object).eClass().getEPackage() == modelPackage;
		}
		return false;
	}

	/** The switch that delegates to the <code>createXXX</code> methods. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated */
	protected PaletteSwitch<Adapter> modelSwitch = new PaletteSwitch<Adapter>() {
		@Override
		public Adapter casePalette(Palette object) {
			return createPaletteAdapter();
		}

		@Override
		public Adapter caseStringToAdapterTypePaletteEntryMap(Map.Entry<String, AdapterTypePaletteEntry> object) {
			return createStringToAdapterTypePaletteEntryMapAdapter();
		}

		@Override
		public Adapter caseStringToFDeviceTypePaletteEntryMap(Map.Entry<String, DeviceTypePaletteEntry> object) {
			return createStringToFDeviceTypePaletteEntryMapAdapter();
		}

		@Override
		public Adapter caseStringToFBTypePaletteEntryMap(Map.Entry<String, FBTypePaletteEntry> object) {
			return createStringToFBTypePaletteEntryMapAdapter();
		}

		@Override
		public Adapter caseStringToResourceTypeEntryMap(Map.Entry<String, ResourceTypeEntry> object) {
			return createStringToResourceTypeEntryMapAdapter();
		}

		@Override
		public Adapter caseStringToSegmentTypePaletteEntryMap(Map.Entry<String, SegmentTypePaletteEntry> object) {
			return createStringToSegmentTypePaletteEntryMapAdapter();
		}

		@Override
		public Adapter caseStringToSubApplicationTypePaletteEntryMap(
				Map.Entry<String, SubApplicationTypePaletteEntry> object) {
			return createStringToSubApplicationTypePaletteEntryMapAdapter();
		}

		@Override
		public Adapter casePaletteEntry(PaletteEntry object) {
			return createPaletteEntryAdapter();
		}

		@Override
		public Adapter caseAdapterTypePaletteEntry(AdapterTypePaletteEntry object) {
			return createAdapterTypePaletteEntryAdapter();
		}

		@Override
		public Adapter caseDataTypePaletteEntry(DataTypePaletteEntry object) {
			return createDataTypePaletteEntryAdapter();
		}

		@Override
		public Adapter caseDeviceTypePaletteEntry(DeviceTypePaletteEntry object) {
			return createDeviceTypePaletteEntryAdapter();
		}

		@Override
		public Adapter caseFBTypePaletteEntry(FBTypePaletteEntry object) {
			return createFBTypePaletteEntryAdapter();
		}

		@Override
		public Adapter caseResourceTypeEntry(ResourceTypeEntry object) {
			return createResourceTypeEntryAdapter();
		}

		@Override
		public Adapter caseSegmentTypePaletteEntry(SegmentTypePaletteEntry object) {
			return createSegmentTypePaletteEntryAdapter();
		}

		@Override
		public Adapter caseSubApplicationTypePaletteEntry(SubApplicationTypePaletteEntry object) {
			return createSubApplicationTypePaletteEntryAdapter();
		}

		@Override
		public Adapter caseSystemPaletteEntry(SystemPaletteEntry object) {
			return createSystemPaletteEntryAdapter();
		}

		@Override
		public Adapter defaultCase(EObject object) {
			return createEObjectAdapter();
		}
	};

	/** Creates an adapter for the <code>target</code>. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param target the object to adapt.
	 * @return the adapter for the <code>target</code>.
	 * @generated */
	@Override
	public Adapter createAdapter(Notifier target) {
		return modelSwitch.doSwitch((EObject) target);
	}

	/** Creates a new adapter for an object of class '{@link org.eclipse.fordiac.ide.model.Palette.Palette
	 * <em>Palette</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore
	 * cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
	 * 
	 * @return the new adapter.
	 * @see org.eclipse.fordiac.ide.model.Palette.Palette
	 * @generated */
	public Adapter createPaletteAdapter() {
		return null;
	}

	/** Creates a new adapter for an object of class '{@link java.util.Map.Entry <em>String To Adapter Type Palette
	 * Entry Map</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore
	 * cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
	 * 
	 * @return the new adapter.
	 * @see java.util.Map.Entry
	 * @generated */
	public Adapter createStringToAdapterTypePaletteEntryMapAdapter() {
		return null;
	}

	/** Creates a new adapter for an object of class '{@link java.util.Map.Entry <em>String To FDevice Type Palette
	 * Entry Map</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore
	 * cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
	 * 
	 * @return the new adapter.
	 * @see java.util.Map.Entry
	 * @generated */
	public Adapter createStringToFDeviceTypePaletteEntryMapAdapter() {
		return null;
	}

	/** Creates a new adapter for an object of class '{@link java.util.Map.Entry <em>String To FB Type Palette Entry
	 * Map</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
	 * 
	 * @return the new adapter.
	 * @see java.util.Map.Entry
	 * @generated */
	public Adapter createStringToFBTypePaletteEntryMapAdapter() {
		return null;
	}

	/** Creates a new adapter for an object of class '{@link java.util.Map.Entry <em>String To Resource Type Entry
	 * Map</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
	 * 
	 * @return the new adapter.
	 * @see java.util.Map.Entry
	 * @generated */
	public Adapter createStringToResourceTypeEntryMapAdapter() {
		return null;
	}

	/** Creates a new adapter for an object of class '{@link java.util.Map.Entry <em>String To Segment Type Palette
	 * Entry Map</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore
	 * cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
	 * 
	 * @return the new adapter.
	 * @see java.util.Map.Entry
	 * @generated */
	public Adapter createStringToSegmentTypePaletteEntryMapAdapter() {
		return null;
	}

	/** Creates a new adapter for an object of class '{@link java.util.Map.Entry <em>String To Sub Application Type
	 * Palette Entry Map</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can easily
	 * ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc
	 * -->
	 * 
	 * @return the new adapter.
	 * @see java.util.Map.Entry
	 * @generated */
	public Adapter createStringToSubApplicationTypePaletteEntryMapAdapter() {
		return null;
	}

	/** Creates a new adapter for an object of class '{@link org.eclipse.fordiac.ide.model.Palette.PaletteEntry
	 * <em>Entry</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore
	 * cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
	 * 
	 * @return the new adapter.
	 * @see org.eclipse.fordiac.ide.model.Palette.PaletteEntry
	 * @generated */
	public Adapter createPaletteEntryAdapter() {
		return null;
	}

	/** Creates a new adapter for an object of class
	 * '{@link org.eclipse.fordiac.ide.model.Palette.AdapterTypePaletteEntry <em>Adapter Type Palette Entry</em>}'. <!--
	 * begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's useful to
	 * ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
	 * 
	 * @return the new adapter.
	 * @see org.eclipse.fordiac.ide.model.Palette.AdapterTypePaletteEntry
	 * @generated */
	public Adapter createAdapterTypePaletteEntryAdapter() {
		return null;
	}

	/** Creates a new adapter for an object of class '{@link org.eclipse.fordiac.ide.model.Palette.DataTypePaletteEntry
	 * <em>Data Type Palette Entry</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we
	 * can easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
	 * end-user-doc -->
	 * 
	 * @return the new adapter.
	 * @see org.eclipse.fordiac.ide.model.Palette.DataTypePaletteEntry
	 * @generated */
	public Adapter createDataTypePaletteEntryAdapter() {
		return null;
	}

	/** Creates a new adapter for an object of class
	 * '{@link org.eclipse.fordiac.ide.model.Palette.DeviceTypePaletteEntry <em>Device Type Palette Entry</em>}'. <!--
	 * begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's useful to
	 * ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
	 * 
	 * @return the new adapter.
	 * @see org.eclipse.fordiac.ide.model.Palette.DeviceTypePaletteEntry
	 * @generated */
	public Adapter createDeviceTypePaletteEntryAdapter() {
		return null;
	}

	/** Creates a new adapter for an object of class '{@link org.eclipse.fordiac.ide.model.Palette.FBTypePaletteEntry
	 * <em>FB Type Palette Entry</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can
	 * easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
	 * end-user-doc -->
	 * 
	 * @return the new adapter.
	 * @see org.eclipse.fordiac.ide.model.Palette.FBTypePaletteEntry
	 * @generated */
	public Adapter createFBTypePaletteEntryAdapter() {
		return null;
	}

	/** Creates a new adapter for an object of class '{@link org.eclipse.fordiac.ide.model.Palette.ResourceTypeEntry
	 * <em>Resource Type Entry</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can
	 * easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
	 * end-user-doc -->
	 * 
	 * @return the new adapter.
	 * @see org.eclipse.fordiac.ide.model.Palette.ResourceTypeEntry
	 * @generated */
	public Adapter createResourceTypeEntryAdapter() {
		return null;
	}

	/** Creates a new adapter for an object of class
	 * '{@link org.eclipse.fordiac.ide.model.Palette.SegmentTypePaletteEntry <em>Segment Type Palette Entry</em>}'. <!--
	 * begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's useful to
	 * ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
	 * 
	 * @return the new adapter.
	 * @see org.eclipse.fordiac.ide.model.Palette.SegmentTypePaletteEntry
	 * @generated */
	public Adapter createSegmentTypePaletteEntryAdapter() {
		return null;
	}

	/** Creates a new adapter for an object of class
	 * '{@link org.eclipse.fordiac.ide.model.Palette.SubApplicationTypePaletteEntry <em>Sub Application Type Palette
	 * Entry</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore
	 * cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
	 * 
	 * @return the new adapter.
	 * @see org.eclipse.fordiac.ide.model.Palette.SubApplicationTypePaletteEntry
	 * @generated */
	public Adapter createSubApplicationTypePaletteEntryAdapter() {
		return null;
	}

	/** Creates a new adapter for an object of class '{@link org.eclipse.fordiac.ide.model.Palette.SystemPaletteEntry
	 * <em>System Palette Entry</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can
	 * easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
	 * end-user-doc -->
	 * 
	 * @return the new adapter.
	 * @see org.eclipse.fordiac.ide.model.Palette.SystemPaletteEntry
	 * @generated */
	public Adapter createSystemPaletteEntryAdapter() {
		return null;
	}

	/** Creates a new adapter for the default case. <!-- begin-user-doc --> This default implementation returns null.
	 * <!-- end-user-doc -->
	 * 
	 * @return the new adapter.
	 * @generated */
	public Adapter createEObjectAdapter() {
		return null;
	}

} // PaletteAdapterFactory
