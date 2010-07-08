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
package org.eclipse.ecf.protocol.nntp.core.internal;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.ecf.protocol.nntp.core.Debug;
import org.eclipse.ecf.protocol.nntp.model.INewsgroup;
import org.eclipse.ecf.protocol.nntp.model.IServer;

public class Newsgroup implements INewsgroup {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private transient final IServer server;

	private final String newsgroupName;

	private final String description;

	private int articleCount;

	private int lowWaterMark;

	private int highWaterMark;

	private Calendar lastUpdateDate;

	private HashMap properties;

	private boolean subscribed;

	public Newsgroup(IServer server2, String newsgroup, String description) {
		this.server = server2;
		this.newsgroupName = newsgroup.trim();
		this.description = description.trim();
	}

	public int getArticleCount() {
		return articleCount;
	}

	public String getCreatedBy() {
		// TODO Auto-generated method stub
		return null;
	}

	public Calendar getDateCreated() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getHighWaterMark() {
		return highWaterMark;
	}

	public int getLowWaterMark() {
		return lowWaterMark;
	}

	public String getDescription() {
		return description;
	}

	public String getNewsgroupName() {
		return newsgroupName;
	}

	public String getURL() {
		return server.getURL() + "/?group=" + getNewsgroupName();
	}

	public IServer getServer() {
		return server;
	}

	public String toString() {
		return getServer().toString() + "::" + getNewsgroupName();
	}

	public int hashCode() {
		return toString().hashCode();
	}

	public boolean equals(Object obj) {
		if (obj instanceof Newsgroup) {
			return obj.toString().equals(toString());
		}
		return super.equals(obj);
	}

	public void setAttributes(int articleCount, int lowWaterMark,
			int highWaterMark) {
		if (this.articleCount != articleCount
				|| this.lowWaterMark != lowWaterMark
				|| this.highWaterMark != highWaterMark) {
			this.articleCount = articleCount;
			this.lowWaterMark = lowWaterMark;
			this.highWaterMark = highWaterMark;
		}
		this.lastUpdateDate = Calendar.getInstance();
	}

	public Calendar getLastChangeDate() {
		return lastUpdateDate;
	}

	public void adjustArticleCount(int newCount) {
		this.articleCount = newCount;
	}

	public void adjustHighWatermark(int newHighestNumber) {
		Debug.log(getClass(), "Changing high watermark from "
				+ getHighWaterMark() + " to " + newHighestNumber);
		this.highWaterMark = newHighestNumber;
	}

	public void adjustLowWatermark(int newLowestNumber) {
		Debug.log(getClass(), "Changing low watermark from "
				+ getLowWaterMark() + " to " + newLowestNumber);
		this.lowWaterMark = newLowestNumber;
	}

	public void setProperty(String key, String value) {
		if (value == null) {
			getProperties().remove(key);
			return;
		}
		getProperties().put(key, value);
	}

	public Map getProperties() {
		if (properties == null) {
			properties = new HashMap();
		}
		return properties;
	}

	public String getProperty(String key) {
		return (String) getProperties().get(key);
	}

	public boolean isSubscribed() {
		return subscribed;
	}

	public void setSubscribed(boolean subscribed) {
		this.subscribed = subscribed;

	}
}
