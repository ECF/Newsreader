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
package org.eclipse.ecf.protocol.nntp.core.internal;

import java.util.ArrayList;

import org.eclipse.ecf.protocol.nntp.model.IArticleEventListnersRegistry;
import org.eclipse.ecf.protocol.nntp.model.IArticleEvent;
import org.eclipse.ecf.protocol.nntp.model.IArticleEventListner;


public class ArticleEventListnersRegistry implements IArticleEventListnersRegistry{

	private ArrayList<IArticleEventListner> listeners;
	
	public void addListener(IArticleEventListner listener) {
		if (listeners == null) {
			listeners = new ArrayList<IArticleEventListner>();
		}

		synchronized (listeners) {
			listeners.add(listener);
		}
	}

	public void removeListner(IArticleEventListner listener) {
		listeners.remove(listener);
	}
	
	public void fireEvent(IArticleEvent event) {
		if (listeners == null || listeners.isEmpty())
			return;
		
		synchronized (listeners) {
			for (IArticleEventListner listener : listeners) {
				listener.execute(event);
			}
		}
	}

}
