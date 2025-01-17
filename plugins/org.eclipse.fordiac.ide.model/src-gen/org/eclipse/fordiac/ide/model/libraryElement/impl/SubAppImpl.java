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
package org.eclipse.fordiac.ide.model.libraryElement.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.fordiac.ide.model.libraryElement.FBNetwork;
import org.eclipse.fordiac.ide.model.libraryElement.LibraryElementPackage;
import org.eclipse.fordiac.ide.model.libraryElement.SubApp;
import org.eclipse.fordiac.ide.model.libraryElement.SubAppType;

/** <!-- begin-user-doc --> An implementation of the model object '<em><b>Sub App</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 * <li>{@link org.eclipse.fordiac.ide.model.libraryElement.impl.SubAppImpl#getSubAppNetwork <em>Sub App
 * Network</em>}</li>
 * </ul>
 *
 * @generated */
public class SubAppImpl extends FBNetworkElementImpl implements SubApp {
	/** The cached value of the '{@link #getSubAppNetwork() <em>Sub App Network</em>}' containment reference. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getSubAppNetwork()
	 * @generated
	 * @ordered */
	protected FBNetwork subAppNetwork;

	/** <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated */
	protected SubAppImpl() {
		super();
	}

	/** <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated */
	@Override
	protected EClass eStaticClass() {
		return LibraryElementPackage.Literals.SUB_APP;
	}

	/** <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated */
	@Override
	public FBNetwork getSubAppNetwork() {
		if (subAppNetwork != null && subAppNetwork.eIsProxy()) {
			InternalEObject oldSubAppNetwork = (InternalEObject) subAppNetwork;
			subAppNetwork = (FBNetwork) eResolveProxy(oldSubAppNetwork);
			if (subAppNetwork != oldSubAppNetwork) {
				InternalEObject newSubAppNetwork = (InternalEObject) subAppNetwork;
				NotificationChain msgs = oldSubAppNetwork.eInverseRemove(this,
						EOPPOSITE_FEATURE_BASE - LibraryElementPackage.SUB_APP__SUB_APP_NETWORK, null, null);
				if (newSubAppNetwork.eInternalContainer() == null) {
					msgs = newSubAppNetwork.eInverseAdd(this,
							EOPPOSITE_FEATURE_BASE - LibraryElementPackage.SUB_APP__SUB_APP_NETWORK, null, msgs);
				}
				if (msgs != null)
					msgs.dispatch();
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE,
							LibraryElementPackage.SUB_APP__SUB_APP_NETWORK, oldSubAppNetwork, subAppNetwork));
			}
		}
		return subAppNetwork;
	}

	/** <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated */
	public FBNetwork basicGetSubAppNetwork() {
		return subAppNetwork;
	}

	/** <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated */
	public NotificationChain basicSetSubAppNetwork(FBNetwork newSubAppNetwork, NotificationChain msgs) {
		FBNetwork oldSubAppNetwork = subAppNetwork;
		subAppNetwork = newSubAppNetwork;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET,
					LibraryElementPackage.SUB_APP__SUB_APP_NETWORK, oldSubAppNetwork, newSubAppNetwork);
			if (msgs == null)
				msgs = notification;
			else
				msgs.add(notification);
		}
		return msgs;
	}

	/** <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated */
	@Override
	public void setSubAppNetwork(FBNetwork newSubAppNetwork) {
		if (newSubAppNetwork != subAppNetwork) {
			NotificationChain msgs = null;
			if (subAppNetwork != null)
				msgs = ((InternalEObject) subAppNetwork).eInverseRemove(this,
						EOPPOSITE_FEATURE_BASE - LibraryElementPackage.SUB_APP__SUB_APP_NETWORK, null, msgs);
			if (newSubAppNetwork != null)
				msgs = ((InternalEObject) newSubAppNetwork).eInverseAdd(this,
						EOPPOSITE_FEATURE_BASE - LibraryElementPackage.SUB_APP__SUB_APP_NETWORK, null, msgs);
			msgs = basicSetSubAppNetwork(newSubAppNetwork, msgs);
			if (msgs != null)
				msgs.dispatch();
		} else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, LibraryElementPackage.SUB_APP__SUB_APP_NETWORK,
					newSubAppNetwork, newSubAppNetwork));
	}

	/** <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated */
	@Override
	public SubAppType getType() {
		// this cannot be moved to the annotation class because there we don't have the super access!!!
		org.eclipse.fordiac.ide.model.libraryElement.LibraryElement type = super.getType();
		if (type instanceof SubAppType) {
			return (SubAppType) type;
		}
		return null;
	}

	/** <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated */
	@Override
	public boolean isUnfolded() {
		org.eclipse.fordiac.ide.model.libraryElement.Attribute attr = this
				.getAttribute(org.eclipse.fordiac.ide.model.LibraryElementTags.SUBAPP_REPRESENTATION_ATTRIBUTE);
		return (attr != null) && "true".equals(attr.getValue()); //$NON-NLS-1$

	}

	/** <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated */
	@Override
	public boolean isTyped() {
		return (getType() != null);
	}

	/** <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated */
	@Override
	public FBNetwork loadSubAppNetwork() {
		return org.eclipse.fordiac.ide.model.annotations.SubAppAnnotations.loadSubAppNetwork(this);
	}

	/** <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
		case LibraryElementPackage.SUB_APP__SUB_APP_NETWORK:
			return basicSetSubAppNetwork(null, msgs);
		default:
			return super.eInverseRemove(otherEnd, featureID, msgs);
		}
	}

	/** <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
		case LibraryElementPackage.SUB_APP__SUB_APP_NETWORK:
			if (resolve)
				return getSubAppNetwork();
			return basicGetSubAppNetwork();
		default:
			return super.eGet(featureID, resolve, coreType);
		}
	}

	/** <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
		case LibraryElementPackage.SUB_APP__SUB_APP_NETWORK:
			setSubAppNetwork((FBNetwork) newValue);
			return;
		default:
			super.eSet(featureID, newValue);
			return;
		}
	}

	/** <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
		case LibraryElementPackage.SUB_APP__SUB_APP_NETWORK:
			setSubAppNetwork((FBNetwork) null);
			return;
		default:
			super.eUnset(featureID);
			return;
		}
	}

	/** <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
		case LibraryElementPackage.SUB_APP__SUB_APP_NETWORK:
			return subAppNetwork != null;
		default:
			return super.eIsSet(featureID);
		}
	}

} // SubAppImpl
