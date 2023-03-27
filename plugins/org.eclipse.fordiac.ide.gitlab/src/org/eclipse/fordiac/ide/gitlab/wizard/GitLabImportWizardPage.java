/*******************************************************************************
 * Copyright (c) 2023 Primetals Technologies Austria GmbH
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Dunja Životin - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.fordiac.ide.gitlab.wizard;

import org.eclipse.fordiac.ide.gitlab.management.GitLabDownloadManager;
import org.eclipse.jface.widgets.WidgetFactory;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class GitLabImportWizardPage extends WizardPage {
	
	private Text url;
	private Text token;
	private Composite container;
	private GitLabDownloadManager downloadManager;

	public GitLabImportWizardPage(String pageName) {
		super(pageName);
		setTitle(pageName); //NON-NLS-1
		setDescription("Import files from GitLab into the workspace"); //NON-NLS-1
	}
	
	public GitLabDownloadManager getDownloadManager() {
		return downloadManager;
	}

	public String getUrl() {
		return url.getText();
		}

	public String getToken() {
		return token.getText();
	}
	
	@Override
	public void setPageComplete(boolean complete) {
		super.setPageComplete(complete);
		if(complete && validateFields()) {
			connect();
		}
	}
	
	private void connect() {
		downloadManager = new GitLabDownloadManager();
    	downloadManager.connectToGitLab(getUrl(), getToken());
    	
	}
	
	private boolean validateFields() {
		return !url.getText().isEmpty() && !token.getText().isEmpty();
	}
	
    @Override
    public void createControl(Composite parent) {
     Button connectionButton;
    	container = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        container.setLayout(layout);
        layout.numColumns = 2;
        
        createLabel("URL:");
        url  = new Text(container, SWT.BORDER | SWT.SINGLE);
        url.setFocus();
        
        createLabel("Token:");
        token = new Text(container, SWT.PASSWORD | SWT.BORDER | SWT.SINGLE);
        
        // We could also bind connecting to pressing enter instead of having a button
        connectionButton = WidgetFactory.button(NONE).text("Connect").create(container);
        connectionButton.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseUp(MouseEvent e) {
				// Nothing for now
				
			}
			
			@Override
			public void mouseDown(MouseEvent e) {
				setPageComplete(true);
			}

			@Override
			public void mouseDoubleClick(MouseEvent e) {
				// Nothing for now
				
			}
		});
        
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        url.setLayoutData(gd);
        token.setLayoutData(gd);
        // required to avoid an error in the system
        setControl(container);
        // Cannot be complete before we connect to GitLab
        setPageComplete(false);
    }
    

    
    private Label createLabel(String labelText) {
    	Label label = new Label(container, SWT.NONE);
        label.setText(labelText);
    	return label;
    }
    
}