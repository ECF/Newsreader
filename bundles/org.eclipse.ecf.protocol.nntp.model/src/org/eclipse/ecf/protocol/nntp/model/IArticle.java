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

import java.io.Serializable;

/**
 * This class contains the article meta data. The actual contents must be
 * fetched from the server or store.
 * 
 * @author Wim Jongman
 */
public interface IArticle extends Serializable, IProperties {

	/**
	 * @param attribute
	 *            The String attribute
	 * @return the String value of the specified header attribute.
	 */
	public String getHeaderAttributeValue(String attribute);

	/**
	 * 
	 * @return the newsgroup that holds this article
	 */
	public INewsgroup getNewsgroup();

	/**
	 * @return the server that holds the newsgroup that holds this article
	 */
	public IServer getServer();

	/**
	 * @return the article parent of this article or null if this is an original
	 *         post.
	 * 
	 */
	public IArticle getParent();

	/**
	 * @return The number of this article in this newsgroup
	 * 
	 */
	public int getArticleNumber();

	/**
	 * @return String - the article subject
	 */
	public String getSubject();

	/**
	 * @return String - the poster of this article.
	 */
	public String getFrom();

	/**
	 * Either the full name is omitted, or it appears in parentheses after the
	 * electronic address of the person posting the article, or it appears
	 * before an electronic address enclosed in angle brackets. Thus, the three
	 * permissible forms are:<br>
	 * <br>
	 * From: mark@cbosgd.UUCP <br>
	 * From: mark@cbosgd.UUCP (Mark Horton) <br>
	 * From: Mark Horton &lt;mark@cbosgd.UUCP&gt;
	 * 
	 * @return - The full user name as specified in RFC850
	 */
	public String getFullUserName();

	/**
	 * @return String - the post date
	 */
	public String getDate();

	/**
	 * @return String - the ID of this message.
	 */
	public String getMessageId();

	/**
	 * @return String[] - the references of the article contain the article id's
	 *         that this article is a reply to. The first entry is the main
	 *         topic and the next entries are replies to that in a thread.
	 */
	public String[] getReferences();

	/**
	 * @return String - the references of this article
	 */
	public String getXRef();

	/**
	 * Sets the specified header <code>element</code> to the specified
	 * <code>value</code>.
	 * 
	 * @param element
	 * @param value
	 */
	public void setHeaderAttributeValue(String element, String value);

	/**
	 * @return String - The size in readable format like 25KB
	 */
	public String getFormattedSize();

	/**
	 * @return int - The size of this message
	 */
	public int getSize();

	/**
	 * @return boolean - true if this article was already read.
	 */
	public boolean isRead();

	/**
	 * @return boolean - true if all the replies to this article are read.
	 */
	public boolean isReplyRead();

	/**
	 * Sets the read state of this article.
	 * 
	 * @param read
	 *            true if this article was read and false if this article was
	 *            not read.
	 */
	public void setRead(boolean read);

	/**
	 * Sets the read state of all replies to this article.
	 * 
	 * @param read
	 *            true if all replies to this article are read.
	 */
	public void setReplyRead(boolean read);

	/**
	 * 
	 * @return
	 */
	public boolean isPosted();

	/**
	 * The newsgroup is transient.
	 * 
	 * @param newsgroup
	 */
	public void setNewsgroup(INewsgroup newsgroup);

	public void setParent(IArticle article);

	/**
	 * Returns the last reference of this article which is the article replied
	 * to.
	 * 
	 * @return the last reference or null if no such reference exists
	 */
	public String getLastReference();

	public String[] getHeaderAttributes();

	/**
	 * @return true if this was posted by the current user according to the
	 *         server information.
	 */
	public boolean isMine();


	/**
	 * Called to add information to the article that can be queried at display
	 * time.
	 * 
	 * @param article
	 * @param replies
	 */
	public void setThreadAttributes(IArticle[] replies);

	/**
	 * Indicates if this user is commenting on this thread.
	 * 
	 * @param commenting
	 */
	void setCommenting(boolean commenting);

	/**
	 * Indicates if this user is commenting on this thread.
	 */
	boolean isCommenting();

	/**
	 * Indicates if this article is a reply.
	 */
	boolean isReply();

	/**
	 * Indicates if we follow this thread.
	 */
	boolean isMarked();

	/**
	 * Set if we must follow this thread.
	 */
	void setMarked(boolean marked);

	/**
	 * Gets the URL of this article.
	 * 
	 * @return the URL
	 */
	String getURL();

	public String[] getHeaderAttributeValues();
}
