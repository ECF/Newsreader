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

import javax.annotation.PostConstruct;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.ecf.protocol.nntp.model.IServer;
import org.eclipse.ecf.salvo.ui.internal.resources.ISalvoResource;
import org.eclipse.ecf.salvo.ui.wizards.NewNewsServerWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

public class ChangeServer {

	@Execute
	public Object execute(
			@Optional @Named(IServiceConstants.ACTIVE_SELECTION) ISalvoResource resource,
			Shell shell) {

		if (resource.getObject() instanceof IServer) {
			NewNewsServerWizard wizard = new NewNewsServerWizard(
					(IServer) resource.getObject());
			WizardDialog dialog = new WizardDialog(shell, wizard);
			dialog.create();
			dialog.open();
		}
		return null;
	}
}
