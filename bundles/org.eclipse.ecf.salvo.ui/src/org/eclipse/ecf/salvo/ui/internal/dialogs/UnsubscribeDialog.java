/*******************************************************************************
 *  Copyright (c) 2010 Weltevree Beheer BV, Remain Software & Industrial-TSI
 *                                                                      
 * All rights reserved. This program and the accompanying materials     
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at             
 * http://www.eclipse.org/legal/epl-v10.html                            
 *                                                                      
 * Contributors:                                                        
 *    Wim Jongman - initial API and implementation
 *******************************************************************************/
package org.eclipse.ecf.salvo.ui.internal.dialogs;

import org.eclipse.ecf.protocol.nntp.model.INewsgroup;
import org.eclipse.ecf.protocol.nntp.model.IServer;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;


public class UnsubscribeDialog extends MessageDialog {

	private static boolean isServer;

	protected UnsubscribeDialog(Shell parentShell, String dialogTitle,
			Image dialogTitleImage, String dialogMessage, int dialogImageType,
			String[] dialogButtonLabels, int defaultIndex) {
		super(parentShell, dialogTitle, dialogTitleImage, dialogMessage,
				dialogImageType, dialogButtonLabels, defaultIndex);
	}

	@Override
	protected Control createCustomArea(Composite parent) {
		if (isServer) {
			Composite container = (Composite) super.createDialogArea(parent);
			final Button button = new Button(container, SWT.CHECK);
			button
					.setText("Delete resource contents on disk? (cannot be undone)");
			return container;
		} else
			return super.createCustomArea(parent);
	}

	public static void openConfirm(IServer object, Shell activeShell,
			String address, String string) {
		isServer = true;
		
		UnsubscribeDialog.openConfirm(activeShell, address, string);

	}

	public static void openConfirm(INewsgroup object,
			Shell activeShell, String address, String string) {
		isServer = false;
		openConfirm(activeShell, address, string);

	}
}
