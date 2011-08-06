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
import org.eclipse.ecf.salvo.ui.external.provider.INewsGroupProvider;


public class TestNewsgroupProvider implements INewsGroupProvider {

	private String user;
	private String password;
	private String org;
	private String login;
	private String email;
	private String serverAddress;
	private int serverPort;
	private boolean isSecure;
	private String newsgroupName;
	private String newsgroupDescription;
	
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
		return org;
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
		return serverAddress;
	}
	
	@Override
	public int getServerPort() {
		return serverPort;
	}
	
	@Override
	public boolean isSecure() {
		return isSecure;
	}

	@Override
	public String getNewsgroupName() {
		return newsgroupName;
	}

	@Override
	public String getNewsgroupDescription() {
		return newsgroupDescription;
	}

	@Override
	public boolean init() {
		// Initializing parameter. User Input dialog can be defined here
		
		user = "Foo Bar";
		password = "flinder1f7";
		org = "eclipse.org";
		login = "exquisitus";
		email = "foo.bar@foobar.org";
		serverAddress = "news.eclipse.org";
		serverPort = 119;
		isSecure = true;
		newsgroupName = "eclipse.test";
		newsgroupDescription = "For testing purposes";
		
		return true;
	}

}
