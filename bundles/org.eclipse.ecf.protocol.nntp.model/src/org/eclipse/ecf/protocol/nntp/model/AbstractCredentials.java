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
package org.eclipse.ecf.protocol.nntp.model;

public class AbstractCredentials implements ICredentials {

	private final String user;
	private final String email;
	private final String logIn;
	private final String pass;

	public AbstractCredentials(String user, String email, String logIn,
			String pass) {
		this.user = user;
		this.email = email;
		this.logIn = logIn;
		this.pass = pass;
	}

	public String getEmail() {
		return email;
	}

	public String getLogin() {
		return logIn;
	}

	public String getPassword() {
		return pass;
	}

	public String getUser() {
		return user;
	}

	public String getOrganization() {
		return SALVO.SALVO_ORGANIZATION;
	}

}
