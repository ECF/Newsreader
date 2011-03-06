/*******************************************************************************
 *  Copyright (c) 2010 Weltevree Beheer BV, Remain Software & Industrial-TSI
 *                                                                      
 * All rights reserved. This program and the accompanying materials     
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at             
 * http://www.eclipse.org/legal/epl-v10.html                            
 *                                                                      
 * Contributors:                                                        
 *    Wim Jongman - Initial API and implementation
 *******************************************************************************/
package org.eclipse.ecf.protocol.nntp.core.internal;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ecf.protocol.nntp.core.Debug;
import org.eclipse.ecf.protocol.nntp.core.StringUtils;
import org.eclipse.ecf.protocol.nntp.model.IArticle;
import org.eclipse.ecf.protocol.nntp.model.INewsgroup;
import org.eclipse.ecf.protocol.nntp.model.IServer;
import org.eclipse.ecf.protocol.nntp.model.SALVO;

public class Article implements IArticle {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int articleNumber;

	private HashMap headerAttributes = new HashMap();

	private IArticle parent;

	private transient INewsgroup newsgroup;

	private boolean read;

	private boolean posted;

	private boolean isReplyRead = true;

	private HashMap properties;

	private boolean isCommenting;

	private boolean marked;

	public Article(int articleNumber, INewsgroup newsgroup, boolean posted) {
		this.articleNumber = articleNumber;
		this.newsgroup = newsgroup;
		this.posted = posted;
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + articleNumber;
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Article other = (Article) obj;
		if (articleNumber != other.articleNumber)
			return false;
		return true;
	}

	public int getArticleNumber() {
		return articleNumber;
	}

	public String getDate() {
		return (String) headerAttributes.get("Date:");
	}

	public String getFrom() {
		return (String) headerAttributes.get("From:");

		// FIXME mime4j wanted here. Do clever things like injecting a runtime
		// decoder
		// return DecoderUtil.decodeEncodedWords();
	}

	public String getHeaderAttributeValue(String attribute) {
		return (String) headerAttributes.get(attribute);
	}

	public String[] getHeaderAttributeValues() {
		return (String[]) headerAttributes.values().toArray(
				new String[headerAttributes.size()]);
	}

	public String getMessageId() {
		return (String) headerAttributes.get("Message-ID:");
	}

	public INewsgroup getNewsgroup() {
		return newsgroup;
	}

	public IArticle getParent() {
		return parent;
	}

	public String[] getReferences() {
		String[] result = StringUtils.split2(
				getHeaderAttributeValue("References:").trim(), SALVO.SPACE);

		if (result.length == 0)
			return new String[0];
		return result;
	}

	public IServer getServer() {
		return newsgroup.getServer();
	}

	public String getSubject() {
		return getHeaderAttributeValue("Subject:");
	}

	public String getXRef() {
		return getHeaderAttributeValue("Xref:");
	}

	public void setHeaderAttributeValue(String element, String value) {
		String[] splitis = StringUtils.split(element, ":");
		String[] valuesSplit = StringUtils.split(value, splitis[0] + ":");

		if (splitis.length > 1 && splitis[1].equals("full")) {
			headerAttributes.put(splitis[0] + ":", valuesSplit[1]);
		} else {
			headerAttributes.put(element, value.trim());
		}

	}

	public String getFormattedSize() {
		return (getSize() / 1024) + "KB";
	}

	public int getSize() {
		try {
			return Integer.valueOf(
					((String) headerAttributes.get("Bytes:")).trim())
					.intValue();
		} catch (Exception e) {
			Debug.log(getClass(), "Bytes in article not filled: "
					+ headerAttributes.get("Bytes:"));
			return 0;
		}
	}

	public String getFullUserName() {

		//
		// Simplistic implementation of rfc850
		//

		String[] split = StringUtils.split(getFrom(), "(");
		if (split.length > 1)
			return split[1].replace(')', ' ').trim();

		split = StringUtils.split(getFrom(), "<");
		if (split.length > 1)
			return split[0].trim();

		return getFrom();

	}

	public boolean isRead() {
		return read;
	}

	public void setRead(boolean read) {
		this.read = read;
	}

	public boolean isPosted() {
		return posted;
	}

	public void setNewsgroup(INewsgroup newsgroup) {
		this.newsgroup = newsgroup;
	}

	public void setParent(IArticle article) {
		parent = article;
	}

	public String getLastReference() {
		int size = getReferences().length;
		if (size == 0)
			return null;
		return getReferences()[size - 1];
	}

	public String[] getHeaderAttributes() {
		return (String[]) headerAttributes.keySet().toArray(
				new String[headerAttributes.size()]);
	}

	public boolean isMine() {
		return getFullUserName().equals(
				getServer().getServerConnection().getUser());
	}

	public boolean isReplyRead() {
		return isReplyRead;
	}

	public void setReplyRead(boolean read) {
		isReplyRead = read;
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

	public void setProperty(String key, String value) {
		if (value == null) {
			getProperties().remove(key);
			return;
		}
		getProperties().put(key, value);
	}

	public void setThreadAttributes(IArticle[] thread) {
		setReplyRead(true);
		for (int i = 0; i < thread.length; i++) {
			if (!thread[i].isRead()) {
				setReplyRead(false);
				break;
			}
		}

		setCommenting(false);
		for (int i = 0; i < thread.length; i++) {
			if (thread[i].isMine()) {
				setCommenting(true);
				break;
			}
		}
	}

	public void setCommenting(boolean isCommenting) {
		this.isCommenting = isCommenting;
	}

	public boolean isCommenting() {
		return isCommenting;
	}

	public boolean isMarked() {
		return this.marked;
	}

	public void setMarked(boolean marked) {
		this.marked = marked;
	}

	public boolean isReply() {
		return getReferences().length > 0;
	}

	public String getURL() {
		return getNewsgroup().getURL() + "&article=" + getArticleNumber();
	}
}
