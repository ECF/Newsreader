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
 * This class combines several interfaces to provide the place where news data
 * is stored off-line. Storage for passwords has to be supplied separately to
 * the store instance by calling the setSecureStore method.
 * 
 * @author Wim Jongman
 * 
 */
public interface IStore extends IStoreEventProvider, IInputOutputSystem,
		IAdministration {

	// /**
	// * @return The last exception or null if there was none.
	// */
	// public Exception getLastException();

	/**
	 * Stores the places where secure information can be stored. This could
	 * depend on the local implementation of the store or the capabilities of
	 * the platform the store is running on. For example, the Eclipse newsreader
	 * implementation is using the secure preferences.
	 * 
	 * @param secureStore
	 */
	public void setSecureStore(ISecureStore secureStore);

	/**
	 * Move the articles to the store.
	 * 
	 * @param articles
	 * @throws StoreException
	 */
	public void storeArticles(IArticle[] articles) throws StoreException;

	/**
	 * Stores the article in the store.
	 * 
	 * @param body
	 * @throws StoreException
	 */
	public void storeArticleBody(IArticle article, String[] body)
			throws StoreException;

	/**
	 * Use this method to check if the store is in sync with the server.
	 * 
	 * @return the last (newest) article in the store or null
	 * @throws StoreException
	 */
	public IArticle getLastArticle(INewsgroup newsgroup) throws StoreException;

	/**
	 * Use this method to check if the store is in sync with the server.
	 * 
	 * @return the first (oldest) article in the store or null
	 * @throws StoreException
	 */
	public IArticle getFirstArticle(INewsgroup newsgroup) throws StoreException;

	/**
	 * Returns a meaningful description of this store. <br>
	 * <br>
	 * Examples:
	 * 
	 * <pre>
	 * Local filesystem storage
	 * Derby 4.2.1
	 * </pre>
	 * 
	 * @return String
	 */
	public String getDescription();

	/**
	 * Updates the article in the store.
	 * 
	 * @return true if the article could be stored and false if this could not
	 *         be done. In the latter case, see {@link #getLastException()}
	 * @throws StoreException
	 */
	public void updateArticle(IArticle article) throws StoreException;

	/**
	 * Gets the secure store which was previously set with
	 * {@link #setSecureStore(ISecureStore)}.
	 * 
	 */
	public ISecureStore getSecureStore();
	
	/**
	 * Get the articles belongs to a particular user
	 * 
	 * @param newsgroup Newsgroup
	 * @param userId Full user name of the user 
	 * @return the articles belongs to a particular user 
	 */
	public IArticle[] getArticlesByUserId(INewsgroup newsgroup, String userId);
}
