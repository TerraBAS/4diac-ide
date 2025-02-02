/*******************************************************************************
 * Copyright (c) 2014 fortiss GmbH
 * 				 2020 Johannes Kepler University Linz
 *               2020 Primetals Technologies Austria GmbH
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Bianca Wiesmayr - initial API and implementation and/or initial documentation
 *   Lukas Wais		 - Adaption to work with new super class
 *******************************************************************************/

package org.eclipse.fordiac.ide.application.wizards;

import java.io.File;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.fordiac.ide.application.Messages;
import org.eclipse.fordiac.ide.model.Palette.PaletteEntry;
import org.eclipse.fordiac.ide.model.data.DataFactory;
import org.eclipse.fordiac.ide.model.data.StructuredType;
import org.eclipse.fordiac.ide.model.dataexport.AbstractTypeExporter;
import org.eclipse.fordiac.ide.model.helpers.InterfaceListCopier;
import org.eclipse.fordiac.ide.model.libraryElement.VarDeclaration;
import org.eclipse.fordiac.ide.model.typelibrary.TypeLibrary;
import org.eclipse.fordiac.ide.model.typelibrary.TypeLibraryTags;
import org.eclipse.fordiac.ide.model.ui.widgets.OpenStructMenu;
import org.eclipse.fordiac.ide.typemanagement.preferences.TypeManagementPreferencesHelper;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.StructuredSelection;

public class SaveAsStructWizard extends AbstractSaveAsWizard {
	private static final String SUBAPP_SECTION = "SUBAPP_SECTION"; //$NON-NLS-1$
	private final List<VarDeclaration> varDecl;
	private final IProject project;
	private String datatypeName;
	private boolean replaceSource;

	public SaveAsStructWizard(final List<VarDeclaration> varDecl, final IProject project, final String windowTitle) {
		super(SUBAPP_SECTION);
		setWindowTitle(windowTitle);
		this.varDecl = varDecl;
		this.project = project;
	}

	@Override
	public boolean performFinish() {
		if (perform()) {
			final IFile targetFile = getTargetTypeFile();
			final PaletteEntry entry = createPaletteEntry(targetFile);
			final StructuredType type = DataFactory.eINSTANCE.createStructuredType();
			entry.setType(type);
			InterfaceListCopier.copyVarList(type.getMemberVariables(), varDecl);

			TypeManagementPreferencesHelper.setupVersionInfo(type);

			datatypeName = TypeLibrary.getTypeNameFromFile(targetFile);
			type.setName(datatypeName);
			AbstractTypeExporter.saveType(entry);

			if (newFilePage.getOpenType()) {
				OpenStructMenu.openStructEditor(targetFile);
			}

			if (newFilePage.getReplaceSource()) {
				replaceSource = true;
			}

		}
		return true;
	}

	private PaletteEntry createPaletteEntry(final IFile targetTypeFile) {
		return TypeLibrary.getTypeLibrary(project).createPaletteEntry(targetTypeFile);
	}

	public boolean replaceSource() {
		return replaceSource;
	}

	public String getDatatypeName() {
		return datatypeName;
	}

	@Override
	public void addPages() {
		final StructuredSelection selection = new StructuredSelection(project); // select the current project
		newFilePage = SaveAsWizardPage.createSaveAsStructWizardPage(Messages.SaveAsStructWizard_WizardPageName,
				selection);
		newFilePage.setFileName(varDecl.get(0).getName());
		addPage(newFilePage);
	}

	@Override
	protected boolean askOverwrite() {
		return MessageDialog.openConfirm(getShell(), Messages.ConvertToStructHandler_ErrorTitle,
				Messages.ConvertToStructHandler_ErrorMessage);
	}

	@Override
	public IFile getTargetTypeFile() {
		final Path path = new Path(newFilePage.getContainerFullPath() + File.separator + newFilePage.getFileName()
		+ TypeLibraryTags.DATA_TYPE_FILE_ENDING_WITH_DOT);
		return ResourcesPlugin.getWorkspace().getRoot().getFile(path);
	}

}
