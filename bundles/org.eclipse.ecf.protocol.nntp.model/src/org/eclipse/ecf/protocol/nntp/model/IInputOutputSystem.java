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

import java.util.Calendar;

/**
 * This class provides methods that both the server side and the store side
 * need. The server side needs to fetch the information and the store side needs
 * to store the information.
 * 
 * @author wimj@weltevree.com
 * 
 */
public interface IInputOutputSystem {

	/**
	 * Refreshes the attributes from the server and places it in the passed
	 * newsgroup or moves the information from the passed newsgroup into the
	 * store.
	 * 
	 * @throws NNTPIOException
	 * @throws UnexpectedResponseException
	 * @throws StoreException
	 */
	public void updateAttributes(INewsgroup newsgroup) throws NNTPIOException,
			UnexpectedResponseException, StoreException;

	/**
	 * Retrieves the body of this article.
	 * 
	 * @param article
	 * @return the body, may not be null
	 * @throws UnexpectedResponseException
	 * @throws NNTPIOException
	 * @throws StoreException
	 */
	public String[] getArticleBody(IArticle article) throws NNTPIOException,
			UnexpectedResponseException, StoreException;

	/**
	 * This method goes to the server and asks for the active newsgroup
	 * attributes. These attributes are then placed back into the newsgroup.
	 * 
	 * @param server
	 * @param newsgroup
	 * @throws NNTPIOException
	 * @throws UnexpectedResponseException
	 * @throws StoreException
	 */
	public void setWaterMarks(INewsgroup newsgroup) throws NNTPIOException,
			UnexpectedResponseException, StoreException;

	/**
	 * Gets the newsgroup article array with the most new article id in element
	 * 0.
	 * 
	 * @param connection
	 * @return
	 * @throws NNTPIOException
	 * @throws UnexpectedResponseException
	 * @throws StoreException
	 */
	public IArticle[] getArticles(INewsgroup newsgroup, int from, int to)
			throws NNTPIOException, UnexpectedResponseException, StoreException;

	/**
	 * Fetch the followups of this article.
	 * 
	 * @param article
	 * @return the follow ups
	 * @throws NNTPIOException
	 * @throws UnexpectedResponseException
	 * @throws StoreException
	 */
	public IArticle[] getFollowUps(IArticle article) throws NNTPIOException,
			UnexpectedResponseException, StoreException;

	/**
	 * Gets the article from the newsgroup or the store based on the passed
	 * articleId and the fetchType.
	 * 
	 * @param newsgroup
	 * @param articleId
	 *            the group article id which is used in combination with the
	 *            fetchType.
	 * @return the article or null if it was not found.
	 * @throws NNTPConnectException
	 * @throws NNTPIOException
	 * @throws StoreException
	 */
	public IArticle getArticle(INewsgroup newsgroup, int articleId)
			throws NNTPIOException, UnexpectedResponseException, StoreException;

	/**
	 * Gets the article by URL from the newsgroup or the store based on the
	 * passed articleId and the fetchType.
	 * 
	 * @param URL
	 *            - news://server/newsgroup?articleInteger
	 * @return the article or null if it was not found.
	 * @throws NNTPConnectException
	 * @throws NNTPIOException
	 * @throws NNTPException
	 */
	public IArticle getArticle(String URL) throws NNTPIOException,
			UnexpectedResponseException, NNTPException;

	/**
	 * Purges (removes) articles from the store that are the supplied date and
	 * older. It only removes <code>number</code> articles before returning.
	 * This enables you to have control over the time this methods is running.
	 * 
	 * @param purgeDate
	 *            remove articles before and on this date
	 * @param number
	 *            the number of articles to delete before returning or 0 to
	 *            delete all articles before and on <code>purgeDate</code>
	 * @return the number of articles that were deleted during this call
	 * @throws StoreException
	 */
	public int purge(Calendar purgeDate, int number) throws NNTPIOException;

	/**
	 * Deletes the article from the store.
	 * 
	 * @param article
	 *            the {@link IArticle} to remove
	 * @return 
	 * @throws StoreException
	 */
	public int delete(IArticle article) throws NNTPIOException;
}
