/*******************************************************************************
 * Copyright (c) 2011 - 2017 TU Wien ACIN, fortiss GmbH
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Alois Zoitl
 *     - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.fordiac.ide.typemanagement.navigator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.edit.provider.ViewerNotification;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.fordiac.ide.model.Palette.PaletteEntry;
import org.eclipse.fordiac.ide.model.libraryElement.AdapterFBType;
import org.eclipse.fordiac.ide.model.libraryElement.AdapterType;
import org.eclipse.fordiac.ide.model.libraryElement.Application;
import org.eclipse.fordiac.ide.model.libraryElement.AutomationSystem;
import org.eclipse.fordiac.ide.model.libraryElement.Device;
import org.eclipse.fordiac.ide.model.libraryElement.FB;
import org.eclipse.fordiac.ide.model.libraryElement.FBType;
import org.eclipse.fordiac.ide.model.libraryElement.LibraryElement;
import org.eclipse.fordiac.ide.model.libraryElement.Resource;
import org.eclipse.fordiac.ide.model.libraryElement.SubApp;
import org.eclipse.fordiac.ide.model.libraryElement.SystemConfiguration;
import org.eclipse.fordiac.ide.model.typelibrary.TypeLibrary;

public class FBTypeContentProvider extends AdapterFactoryContentProvider {

	public FBTypeContentProvider() {
		super(FBTypeComposedAdapterFactory.getAdapterFactory());
	}

	@Override
	public Object[] getElements(final Object inputElement) {
		return getChildren(inputElement);
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof IFile) {
			final IFile element = (IFile) parentElement;
			final PaletteEntry entry = TypeLibrary.getPaletteEntryForFile(element);
			if (null != entry) {
				parentElement = entry.getType();
				if (parentElement instanceof AdapterType) {
					parentElement = ((AdapterType) parentElement).getAdapterFBType();
				}
			}
		}
		if ((parentElement instanceof AutomationSystem) || (parentElement instanceof Application)
				|| (parentElement instanceof SystemConfiguration) || (parentElement instanceof FB)
				|| (parentElement instanceof Device) || (parentElement instanceof Resource)
				|| (parentElement instanceof SubApp)) {
			return new Object[0];
		}
		return super.getChildren(parentElement);
	}

	@Override
	public Object getParent(final Object element) {
		if (element instanceof IFile) {
			return ((IResource) element).getParent();
		}
		return super.getParent(element);
		// FIXME check for the correct elements and return the IFile for them
		//			if(retval instanceof FBType){
		//
		//			}
	}

	@Override
	public boolean hasChildren(final Object element) {
		if ((element instanceof AutomationSystem) || (element instanceof Application)
				|| (element instanceof SystemConfiguration) || (element instanceof FB) || (element instanceof Device)
				|| (element instanceof Resource) || (element instanceof SubApp)) {
			return false;
		}
		if (element instanceof IFile) {
			return true;
		}
		return super.hasChildren(element);
	}

	@Override
	public void notifyChanged(final Notification notification) {
		if (notification.getNotifier() instanceof FBType) {
			// as the automation system is changed we need to perform a special refresh here
			LibraryElement type = (LibraryElement) notification.getNotifier();
			if (type instanceof AdapterFBType) {
				type = ((AdapterFBType) type).getAdapterType();
			}
			super.notifyChanged(new ViewerNotification(notification, type.getPaletteEntry().getFile()));
		} else {
			super.notifyChanged(notification);
		}
	}

}
