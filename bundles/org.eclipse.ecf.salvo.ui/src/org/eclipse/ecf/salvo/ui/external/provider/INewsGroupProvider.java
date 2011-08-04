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

package org.eclipse.ecf.salvo.ui.external.provider;


public interface INewsGroupProvider {
	
	// get credentials
	public String getUser();

	public String getPassword();

	public String getOrganization();

	public String getLogin();

	public String getEmail();
	
	// get server
	public String getServerAddress();
	
	public int getServerPort();
	
	public boolean isSecure();
	
	//get newsgroup
	public String getNewsgroupName();
	
	public String getNewsgroupDescription();

}
