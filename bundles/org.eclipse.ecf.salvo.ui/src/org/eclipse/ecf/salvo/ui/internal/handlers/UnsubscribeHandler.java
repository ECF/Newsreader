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
package org.eclipse.ecf.salvo.ui.internal.handlers;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.ecf.protocol.nntp.core.Debug;
import org.eclipse.ecf.protocol.nntp.core.ServerStoreFactory;
import org.eclipse.ecf.protocol.nntp.model.INewsgroup;
import org.eclipse.ecf.protocol.nntp.model.IServer;
import org.eclipse.ecf.protocol.nntp.model.StoreException;
import org.eclipse.ecf.salvo.ui.internal.resources.ISalvoResource;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;

public class UnsubscribeHandler {

	@Execute
	public void execute(ESelectionService service, Shell shell) {

		if (!(service.getSelection() instanceof IStructuredSelection))
			return;

		ISelection selection = (ISelection) service.getSelection();
		if (selection instanceof IStructuredSelection) {
			for (Object selo : ((IStructuredSelection) selection).toArray()) {
				if (selo instanceof ISalvoResource) {
					Object object = ((ISalvoResource) selo).getObject();
					if (object instanceof IServer) {

						MessageDialogWithToggle x = MessageDialogWithToggle
								.openInformation(
										shell,
										((IServer) object).getAddress(),
										"Do you want to unsubscribe from server "
												+ ((IServer) object)
														.getAddress() + "?",
										"Delete resource contents on disk? (cannot be undone)",
										false, null, null);
						if (x.getReturnCode() == Window.OK) {
							try {
								ServerStoreFactory
										.instance()
										.getServerStoreFacade()
										.unsubscribeServer((IServer) object,
												x.getToggleState());
							} catch (StoreException e) {
								MessageDialog.openError(shell, "Problem found",
										e.getMessage());
								Debug.log(getClass(), e);
							}
						}

					} else if (object instanceof INewsgroup) {
						boolean x = MessageDialog.openConfirm(
								shell,
								((INewsgroup) object).getNewsgroupName(),
								"Do you want to unsubscribe from group "
										+ ((INewsgroup) object)
												.getNewsgroupName() + "?");
						if (x) {
							try {
								ServerStoreFactory
										.instance()
										.getServerStoreFacade()
										.unsubscribeNewsgroup(
												(INewsgroup) object, false);
							} catch (StoreException e) {
								MessageDialog.openError(shell, "Problem found",
										e.getMessage());
								Debug.log(getClass(), e);
							}
						}
					}
				}
			}
		}
	}
}
