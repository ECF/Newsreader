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
package org.eclipse.ecf.protocol.nntp.model;

public class ArticleEvent implements IArticleEvent{
	
	private IArticle[] articles;
	private boolean isFireNotification;
	
	public ArticleEvent(IArticle[] articles, boolean isFireNotification) {
		this.articles = articles;
		this.isFireNotification = isFireNotification;
	}

	public IArticle[] getArticles(){
		return articles;
		
	}

	public boolean isFireNotification() {
		return isFireNotification;
	}	

}
