/*******************************************************************************
 * Copyright (c) 2023 Martin Erich Jobst
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Martin Jobst - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.fordiac.ide.gef.editors;

import java.util.function.Consumer;

import org.eclipse.fordiac.ide.model.commands.change.ChangeValueCommand;
import org.eclipse.fordiac.ide.model.edit.helper.InitialValueHelper;
import org.eclipse.fordiac.ide.model.libraryElement.VarDeclaration;
import org.eclipse.fordiac.ide.structuredtextalgorithm.ui.editor.embedded.STAlgorithmEmbeddedEditorUtil;
import org.eclipse.fordiac.ide.structuredtextalgorithm.ui.editor.embedded.STAlgorithmInitialValueEditedResourceProvider;
import org.eclipse.fordiac.ide.ui.providers.SourceViewerColorProvider;
import org.eclipse.gef.commands.Command;
import org.eclipse.jface.text.contentassist.ContentAssistEvent;
import org.eclipse.jface.text.contentassist.ICompletionListener;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.xtext.ui.editor.embedded.EmbeddedEditor;
import org.eclipse.xtext.ui.editor.embedded.EmbeddedEditorModelAccess;
import org.eclipse.xtext.ui.editor.embedded.IEditedResourceProvider;

@SuppressWarnings("restriction")
public class InitialValueEditor {

	private StyledText control;
	protected EmbeddedEditor embeddedEditor;
	protected EmbeddedEditorModelAccess modelAccess;
	private boolean proposalPopupOpen;

	private VarDeclaration varDeclaration;
	private Consumer<Command> commandExecutor;

	public InitialValueEditor(final Composite parent, final int style) {
		createControl(parent, style);
	}

	protected void createControl(final Composite parent, final int style) {
		final IEditedResourceProvider editedResourceProvider = new STAlgorithmInitialValueEditedResourceProvider(null);
		embeddedEditor = STAlgorithmEmbeddedEditorUtil.getEmbeddedEditorFactory().newEditor(editedResourceProvider)
				.withStyle(style).withParent(parent);
		modelAccess = embeddedEditor.createPartialEditor();
		embeddedEditor.getViewer().setEditable(false);
		SourceViewerColorProvider.initializeSourceViewerColors(embeddedEditor.getViewer());
		embeddedEditor.getViewer().getContentAssistantFacade().addCompletionListener(new ICompletionListener() {

			@Override
			public void selectionChanged(final ICompletionProposal proposal, final boolean smartToggle) {
				// ignore
			}

			@Override
			public void assistSessionStarted(final ContentAssistEvent event) {
				proposalPopupOpen = true;
			}

			@Override
			public void assistSessionEnded(final ContentAssistEvent event) {
				proposalPopupOpen = false;
			}
		});
		embeddedEditor.getViewer().addTextPresentationListener(textPresentation -> {
			if (!embeddedEditor.getViewer().getUndoManager().undoable()
					&& !InitialValueHelper.hasInitalValue(varDeclaration)) {
				textPresentation.replaceStyleRange(new StyleRange(textPresentation.getExtent().getOffset(),
						textPresentation.getExtent().getLength(), InitialValueHelper.getForegroundColor(varDeclaration),
						control.getBackground()));
			}
		});
		control = (StyledText) embeddedEditor.getViewer().getControl();
		control.addFocusListener(new FocusAdapter() {

			@Override
			public void focusGained(final FocusEvent e) {
				parent.getDisplay().asyncExec(() -> {
					if (!control.isDisposed()) {
						control.setSelection(0, control.getText().length());
					}
				});
			}

			@Override
			public void focusLost(final FocusEvent e) {
				commit();
			}
		});
		control.addVerifyKeyListener(event -> {
			if (!isProposalPopupOpen()) {
				if ((event.keyCode == SWT.CR || event.keyCode == SWT.KEYPAD_CR) && event.stateMask != SWT.MOD3) {
					parent.forceFocus();
					event.doit = false;
				} else if (event.keyCode == SWT.ESC && event.stateMask == 0) {
					final var commandExecutorCache = this.commandExecutor;
					this.commandExecutor = null;
					parent.forceFocus();
					this.commandExecutor = commandExecutorCache;
					event.doit = false;
				}
			}
		});
	}

	public void commit() {
		executeCommand(new ChangeValueCommand(varDeclaration, modelAccess.getEditablePart()));
		refresh();
	}

	public void refresh() {
		final var commandExecutorCache = this.commandExecutor;
		this.commandExecutor = null;
		STAlgorithmEmbeddedEditorUtil.updateEditor(embeddedEditor, varDeclaration);
		modelAccess.updateModel(InitialValueHelper.getInitalOrDefaultValue(varDeclaration));
		control.setSelection(0);
		this.commandExecutor = commandExecutorCache;
	}

	protected boolean isProposalPopupOpen() {
		return proposalPopupOpen;
	}

	public void executeCommand(final Command command) {
		if (commandExecutor != null) {
			commandExecutor.accept(command);
		}
	}

	public VarDeclaration getVarDeclaration() {
		return varDeclaration;
	}

	public void setVarDeclaration(final VarDeclaration varDeclaration) {
		this.varDeclaration = varDeclaration;
	}

	public Consumer<Command> getCommandExecutor() {
		return commandExecutor;
	}

	public void setCommandExecutor(final Consumer<Command> commandExecutor) {
		this.commandExecutor = commandExecutor;
	}

	public boolean isEditable() {
		return embeddedEditor.getViewer().isEditable();
	}

	public void setEditable(final boolean editable) {
		embeddedEditor.getViewer().setEditable(editable);
	}

	public StyledText getControl() {
		return control;
	}
}