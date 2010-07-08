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

/**
 * This class must be implemented by clients to supply the credentials needed to
 * access the server. The server calls this class back to get the desired
 * credentials. This class may do anything to supply the information. For
 * example, a dialog can be presented or the information may be fetched from a
 * encrypted location.
 * 
 * @author Wim Jongman
 * 
 */
public interface ICredentials {

	/**
	 * @return the user name
	 */
	public String getUser();

	/**
	 * @return the login or null if it is an anonymous login
	 */
	public String getLogin();

	/**
	 * @return the password
	 */
	public String getPassword();

	/**
	 * @return the e-mail
	 */
	public String getEmail();

	/**
	 * @return the organization
	 */
	public String getOrganization();
}
