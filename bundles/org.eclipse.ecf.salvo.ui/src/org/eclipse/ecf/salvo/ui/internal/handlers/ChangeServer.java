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

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ecf.protocol.nntp.model.IServer;
import org.eclipse.ecf.salvo.ui.internal.resources.ISalvoResource;
import org.eclipse.ecf.salvo.ui.tools.SelectionUtil;
import org.eclipse.ecf.salvo.ui.wizards.NewNewsServerWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.handlers.HandlerUtil;


public class ChangeServer extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {

		ISalvoResource resource = (ISalvoResource) SelectionUtil.getFirstObjectFromSelection(HandlerUtil
				.getCurrentSelection(event), ISalvoResource.class);
		if (resource.getObject() instanceof IServer) {
			NewNewsServerWizard wizard = new NewNewsServerWizard((IServer) resource.getObject());
			WizardDialog dialog = new WizardDialog(HandlerUtil.getActiveShell(event), wizard);
			dialog.create();
			dialog.open();
		}
		return null;
	}

}
