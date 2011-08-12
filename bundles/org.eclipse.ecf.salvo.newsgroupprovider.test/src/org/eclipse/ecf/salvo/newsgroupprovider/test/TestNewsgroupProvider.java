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
package org.eclipse.ecf.salvo.newsgroupprovider.test;

import org.eclipse.ecf.salvo.ui.external.provider.INewsGroupProvider;


public class TestNewsgroupProvider implements INewsGroupProvider {

	private String user;
	private String password;
	private String login;
	private String email;
	
	public TestNewsgroupProvider() {
	}

	@Override
	public String getUser() {
		return user;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getOrganization() {
		return "eclipse.org";
	}

	@Override
	public String getLogin() {
		return login;
	}

	@Override
	public String getEmail() {
		return email;
	}

	@Override
	public String getServerAddress() {
		return "news.eclipse.org";
	}
	
	@Override
	public int getServerPort() {
		return 119;
	}
	
	@Override
	public boolean isSecure() {
		return true;
	}

	@Override
	public String getNewsgroupName() {
		return "eclipse.test";
	}

	@Override
	public String getNewsgroupDescription() {
		return "For testing purposes";
	}

	@Override
	public boolean initCredentials() {
		// Initializing credentials. User Input dialog can be defined here
		
		user = "Foo Bar";
		password = "flinder1f7";
		login = "exquisitus";
		email = "foo.bar@foobar.org";
		
		return true;
	}

}
