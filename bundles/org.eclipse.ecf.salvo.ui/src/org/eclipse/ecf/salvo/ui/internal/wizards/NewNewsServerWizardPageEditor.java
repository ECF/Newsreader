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
package org.eclipse.ecf.salvo.ui.internal.wizards;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;

public class NewNewsServerWizardPageEditor implements ModifyListener {

	private final NewNewsServerWizardPage page;

	public NewNewsServerWizardPageEditor(NewNewsServerWizardPage page) {
		this.page = page;
	}

	public void modifyText(ModifyEvent e) {
		
		page.setErrorMessage(null);
		page.setServerValidated(false);

		// Address should not be empty string
		//
		if (page.getAddress().trim().length() == 0) {
			page.setErrorMessage("Invalid address.");
			return;
		}

		// Try if the port could be resolved to a numeric
		//
		try {
			page.getPort();
		} catch (Exception e2) {
			page.setErrorMessage("Invalid port. Try 119.");
			return;
		}
		
		// Uaer must be filled
		//
		if (page.getUser().trim().length() == 0) {
			page.setErrorMessage("Invalid user");
			return;
		}
		
		if (page.getEmail().trim().length() == 0) {
			page.setErrorMessage("Invalid email address");
			return;
		}
		
		
		// if name and password required
		//
		if (page.isLogonRequired()) {

			// If not anonymous then it should not be empty string
			//
			if (page.getLogin().trim().length() == 0) {
				page.setErrorMessage("Invalid login");
				return;
			}

			// If not anonymous then password should not be empty string
			//
			if (page.getPass().trim().length() == 0) {
				page.setErrorMessage("Invalid password");
				return;
			}
		}
	}
}
