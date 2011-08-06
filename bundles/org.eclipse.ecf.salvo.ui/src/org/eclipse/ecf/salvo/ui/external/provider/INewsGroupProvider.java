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
	
	/**
	 * Initialize parameters
	 * User Input dialog can be placed here
	 * @return initialization is success or not
	 */
	public boolean init();
	
	/**
	 * @return the user name
	 */
	public String getUser();

	/**
	 * @return password 
	 */
	public String getPassword();

	/**
	 * @return organization
	 */
	public String getOrganization();

	/**
	 * @return login
	 */
	public String getLogin();

	/**
	 * @return email
	 */
	public String getEmail();
	
	/**
	 * @return server address
	 */
	public String getServerAddress();
	
	/**
	 * @return server port
	 */
	public int getServerPort();
	
	/**
	 * 
	 * @return whether server is secure
	 */
	public boolean isSecure();
	
	/**
	 * @return get newsgroup name
	 */
	public String getNewsgroupName();

	/**
	 * @return get newsgroup address
	 */
	public String getNewsgroupDescription();

}
