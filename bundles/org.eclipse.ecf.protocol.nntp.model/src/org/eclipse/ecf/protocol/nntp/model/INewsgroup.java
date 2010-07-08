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
import java.util.Calendar;

/**
 * @author jongw
 * 
 */
public interface INewsgroup extends Serializable, IProperties, ISubscribable {

	public final String POSTING_PERMITTED = "Y";

	public final String POSTING_NOT_PERMITTED = "n";

	public final String POSTING_PERMITTED_MODERATED = "m";

	/**
	 * This method is used to get the number of the first article in the group.
	 * The server can report a non existing article number as the first article
	 * due to the article being canceled. Clients should be relaxed towards the
	 * low watermark
	 * 
	 * @see {@link #adjustLowWatermark(int)}
	 * 
	 * @param newLowestNumber
	 *            the new lowest number
	 */
	public int getLowWaterMark();

	/**
	 * This method is used get the last article in the group. The server can
	 * report a non existing article number as the last article due to the
	 * article being canceled. Clients should be relaxed towards this number
	 * 
	 * @see {@link #adjustHighWatermark(int)}
	 * 
	 * @param newLowestNumber
	 *            the new lowest number
	 */
	public int getHighWaterMark();

	/**
	 * This method is used to get the total number of articles in the group. RFC
	 * 977 states that this number might not be correct (due to articles being
	 * canceled). Clients should be relaxed towards this number
	 * 
	 * @see {@link #adjustArticleCount(int)}
	 * 
	 * @param newLowestNumber
	 *            the new lowest number
	 */
	public int getArticleCount();

	public String getCreatedBy();

	public Calendar getDateCreated();

	public String getDescription();

	public String getNewsgroupName();

	public IServer getServer();

	/**
	 * Sets the first and last article (these numbers are referred to as the
	 * "reported low water mark" and the "reported high water mark") and an
	 * estimate of the number of articles in the group currently available.
	 * 
	 * @param articleCount
	 * @param lowWaterMark
	 * @param highWaterMark
	 */
	public void setAttributes(int articleCount, int lowWaterMark,
			int highWaterMark);

	public Calendar getLastChangeDate();

	/**
	 * This method is used to adjust the high watermark (the last article in the
	 * group). The server can report a non existing article number as the
	 * highest (last) due to the article being canceled. Clients don't need to
	 * implement this method but should be relaxed towards the high watermark.
	 * 
	 * @param newHighestNumber
	 *            the new highest number
	 */
	public void adjustHighWatermark(int newHighestNumber);

	/**
	 * This method is used to adjust the low watermark (the first article in the
	 * group). The server can report a non existing article number as the first
	 * article due to the article being canceled. Clients don't need to
	 * implement this method but should be relaxed towards the low watermark.
	 * 
	 * @param newLowestNumber
	 *            the new lowest number
	 */
	public void adjustLowWatermark(int newLowestNumber);

	/**
	 * This method is used to adjust the article count. The server can report a
	 * non existing article number as the first article due to the article being
	 * canceled. Clients don't need to implement this method but should be
	 * relaxed towards the article count.
	 * 
	 * @param newCount
	 *            the new count
	 */
	public void adjustArticleCount(int newCount);

	/**
	 * 
	 * @return the string that makes this newsgroup unique
	 */
	String getURL();


}
