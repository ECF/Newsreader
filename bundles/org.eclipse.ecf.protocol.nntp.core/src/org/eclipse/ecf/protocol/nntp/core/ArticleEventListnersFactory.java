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
package org.eclipse.ecf.protocol.nntp.core;

import org.eclipse.ecf.protocol.nntp.core.internal.ArticleEventListnersRegistry;

public class ArticleEventListnersFactory {

	private static ArticleEventListnersFactory instance;
	private ArticleEventListnersRegistry registry;

	public static ArticleEventListnersFactory instance() {
		if (instance == null) {
			instance = new ArticleEventListnersFactory();
		}
		return instance;
	}
	
	public ArticleEventListnersRegistry getRegistry () {
		if (registry == null) {
			registry = new ArticleEventListnersRegistry();
		}
		return registry;
	}

}
