/*******************************************************************************
 *  Copyright (c) 2011 University Of Moratuwa
 *                                                                      
 * All rights reserved. This program and the accompanying materials     
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at             
 * http://www.eclipse.org/legal/epl-v10.html                            
 *                                                                      
 * Contributors:                                                        
 *    Isuru Udana - UI Integration in the Workbench
 *******************************************************************************/
package org.eclipse.ecf.salvo.ui.internal.dialogs;

import java.util.ArrayList;

import org.eclipse.ecf.protocol.nntp.core.Debug;
import org.eclipse.ecf.protocol.nntp.core.ServerStoreFactory;
import org.eclipse.ecf.protocol.nntp.model.IServer;
import org.eclipse.ecf.protocol.nntp.model.NNTPException;
import org.eclipse.ecf.salvo.ui.tools.PreferencesUtil;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;

public class SelectServerDialog extends Dialog {

	private ArrayList<IServer> servers;
	private Combo combo;
	
	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public SelectServerDialog(Shell parentShell) {
		super(parentShell);
		setShellStyle(SWT.DIALOG_TRIM | SWT.RESIZE);
		initServers();
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		parent.getShell().setText("Select Server");
		Composite container = (Composite) super.createDialogArea(parent);
		GridLayout gridLayout = (GridLayout) container.getLayout();
		gridLayout.numColumns = 2;
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		int selectionIndex = 0;
		
		String selectedServerForDigest = PreferencesUtil.instance()
		.loadPluginSettings("selectedServerForDigest");
		
		combo = new Combo(container, SWT.NONE);
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		for (int i=0,length = servers.size(); i<length; i++){
			combo.add(servers.get(i).getAddress());
			if (servers.get(i).getID().equals(selectedServerForDigest)){
				selectionIndex = i;
				
			}
		}
		
		combo.select(selectionIndex);
		
		combo.addSelectionListener(new SelectionListener() {
			
			public void widgetSelected(SelectionEvent arg0) {
				// Saving preferences
				PreferencesUtil.instance().savePluginSettings(
						"selectedServerForDigest", servers.get(combo.getSelectionIndex()).getID());
				
			}
			
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
		});

		return container;
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(349, 165);
	}
	
	private void initServers(){
		
		servers = new ArrayList<IServer>();
		
		try {
			for (IServer server : ServerStoreFactory.instance()
					.getServerStoreFacade().getFirstStore().getServers()) {
				servers.add(server);
			}
		} catch (NNTPException e) {
			Debug.log(getClass(), e);
		}
	}
	
	public IServer getSelectedServer(){
		return servers.get(combo.getSelectionIndex());
	}

}
