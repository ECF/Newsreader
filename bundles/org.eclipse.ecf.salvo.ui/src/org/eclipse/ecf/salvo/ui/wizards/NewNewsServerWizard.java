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
package org.eclipse.ecf.salvo.ui.wizards;

import org.eclipse.ecf.protocol.nntp.core.Debug;
import org.eclipse.ecf.protocol.nntp.core.ServerStoreFactory;
import org.eclipse.ecf.protocol.nntp.model.INewsgroup;
import org.eclipse.ecf.protocol.nntp.model.IServer;
import org.eclipse.ecf.protocol.nntp.model.IServerStoreFacade;
import org.eclipse.ecf.protocol.nntp.model.NNTPException;
import org.eclipse.ecf.salvo.ui.internal.wizards.NewNewsServerWizardPage;
import org.eclipse.ecf.salvo.ui.internal.wizards.SubscribeGroupWizardPage;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;


public class NewNewsServerWizard extends Wizard implements INewWizard {

	protected NewNewsServerWizardPage page1;
	protected SubscribeGroupWizardPage page2;
	private IServer server;

	public NewNewsServerWizard() {
	}

	public NewNewsServerWizard(IServer server) {
		this.server = server;
	}

	@Override
	public void addPages() {
		page1 = new NewNewsServerWizardPage("Get Server Information");
		page2 = new SubscribeGroupWizardPage("Subscribe to Group");
		addPage(page1);
		addPage(page2);
	}

	@Override
	public boolean performFinish() {

		IServerStoreFacade storeFacade = ServerStoreFactory.instance()
				.getServerStoreFacade();
		try {
			storeFacade.subscribeServer(page1.getServer(), page1.getPass());
		} catch (NNTPException e1) {
			Debug.log(getClass(), e1);
			setWindowTitle(e1.getMessage());
			return false;
		}

		for (INewsgroup group : page2.getGroups()) {
			try {
				storeFacade.subscribeNewsgroup(group);
			} catch (NNTPException e) {
				MessageDialog.openError(getShell(), "Problem subscribing", e
						.getMessage());
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return true;
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {

	}

	@Override
	public void createPageControls(Composite pageContainer) {
		IWizardPage page = (IWizardPage) super.getPages()[0];
		page.createControl(pageContainer);
	}

	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		try {
			if (page instanceof SubscribeGroupWizardPage)
				((SubscribeGroupWizardPage) page).setInput(page1.getServer());
		} catch (NNTPException e) {
			Debug.log(getClass(), e);
		}
		return super.getNextPage(page);
	}

	public IServer getServer() {
		return server;
	}
}
