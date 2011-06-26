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
 * This class controls fetching of information from the server or from the
 * store.
 * 
 * @author jongw
 * 
 */
public interface IServerStoreFacade extends IInputOutputSystem,
		IBasicNNTPInterface, IAdministration {

	/**
	 * If an exception occurred in the store or the sever connection it is
	 * stored and you can retrieve it with this method.
	 * 
	 * @return
	 */
	public Exception getLastException();

	/**
	 * Catches up since the last visit to the server and stores the information
	 * in the store.
	 * 
	 * @param newsgroup
	 * @throws NNTPIOException
	 * @throws UnexpectedResponseException
	 */
	public void catchUp(INewsgroup newsgroup) throws NNTPIOException;

	/**
	 * Returns the list of newsgroup this user is subscribed to in this server.
	 * 
	 * @param server
	 * @return
	 * @throws StoreException 
	 */
	public INewsgroup[] getSubscribedNewsgroups(IServer server) throws StoreException;

	/**
	 * Returns the newsgroup this user is subscribed to in this server.
	 * 
	 * @param server
	 * @return {@link INewsgroup} or null
	 */
	public INewsgroup getSubscribedNewsgroup(IServer server, String newsgroup);

	public void init();

	/**
	 * Gets the first store.
	 * 
	 * @return
	 */
	public IStore getFirstStore();

	/**
	 * Gets all stores.
	 * 
	 * @return
	 */
	public IStore[] getStores();

	public void updateArticle(IArticle article) throws StoreException;

	/**
	 * Get the current user articles
	 * @param newsgroup Newsgroup
	 * @return the current user articles
	 */
	public IArticle[] getThisUserArticles(INewsgroup newsgroup);
	
	/**
	 * Get marked articles
	 * @param newsgroup Newsgroup
	 * @return marked articles of a particular newsgroup
	 */
	public IArticle[] getMarkedArticles(INewsgroup newsgroup);
}
