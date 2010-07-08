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
 * @author jongw
 * 
 */
public interface IServer extends IProperties, ISubscribable {

	/**
	 * Returns the port.
	 * 
	 * @return int
	 */
	public int getPort();

	/**
	 * Returns the TCP/IP address.
	 * 
	 * @return String
	 */
	public String getAddress();

	/**
	 * Returns the {@link IServerConnection} object used for communications.
	 * 
	 * @return the {@link IServerConnection} object used for communcations.
	 *         Cannot be null.
	 */
	public IServerConnection getServerConnection();

	/**
	 * Sets the server connection. Please note that the
	 * {@link #getServerConnection()} method is guaranteed not to return null.
	 * Server factories would call this method.
	 * 
	 * @param connection
	 */
	public void setServerConnection(IServerConnection connection);

	/**
	 * Initializes the server. You can setup the connection here but note that
	 * news servers are very impatient and close the connection as soon as they
	 * can. You could do other one time setup here like getting the overview
	 * headers, query for capabilities et cetera. This method must set the
	 * initialized flag, if {@link #isInitialized()} returns false, no
	 * initialization whatsoever may be assumed to have occurred.
	 * 
	 * @throws NNTPException
	 */
	public void init() throws NNTPException;

	/**
	 * Indicates that this server communicates over a secure layer (SSL/TSL).
	 * 
	 * @return
	 */
	public boolean isSecure();

	/**
	 * Returns true if this is an anonymous server connection.
	 * 
	 * @return boolean
	 */
	public boolean isAnonymous();

	/**
	 * Gets the organization this user belongs to.
	 * 
	 * @return
	 */
	public String getOrganization();

	/**
	 * Gets the overview headers from this server, could be null if they were
	 * not set before.
	 * 
	 * @return the list over overview headers, or null
	 */
	public String[] getOverviewHeaders();

	/**
	 * Sets the overview headers for quick reference. This should be set by the
	 * {@link IServerConnection} if it is fetched for the first time.
	 * 
	 * @param headers
	 */
	public void setOverviewHeaders(String[] headers);

	/**
	 * @return the unique id of this server
	 */
	String getID();

	/**
	 * Asynchronous flag to indicate that the server should be visited again to
	 * make sure everything is still in sync with the database. This is a
	 * convenience flag, no operation of the server may be blocked by the server
	 * being dirty.
	 * 
	 * @param dirty
	 */
	public void setDirty(boolean dirty);

	/**
	 * Asynchronous flag to indicate that the server should be visited again to
	 * make sure everything is still in sync with the database. This is a
	 * convenience flag, no operation of the server may be blocked by the server
	 * being dirty.
	 * 
	 */
	public boolean isDirty();

	/**
	 * Composes a server url like this: nntp://address:port
	 * 
	 * @return the url for this server
	 */
	public String getURL();

	/**
	 * Indicates if the initialize method has run successfully.
	 * 
	 * @return
	 */
	public boolean isInitialized();
}
