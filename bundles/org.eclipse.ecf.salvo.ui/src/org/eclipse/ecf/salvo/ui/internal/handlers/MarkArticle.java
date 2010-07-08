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
package org.eclipse.ecf.salvo.ui.internal.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ecf.protocol.nntp.core.ServerStoreFactory;
import org.eclipse.ecf.protocol.nntp.model.IArticle;
import org.eclipse.ecf.protocol.nntp.model.IServerStoreFacade;
import org.eclipse.ecf.protocol.nntp.model.StoreException;
import org.eclipse.ecf.salvo.ui.internal.resources.ISalvoResource;
import org.eclipse.ecf.salvo.ui.tools.SelectionUtil;

public class MarkArticle extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {

		ISalvoResource res = (ISalvoResource) SelectionUtil
				.getFirstObjectFromCurrentSelection(ISalvoResource.class);
		if (res != null)
			if (res.getObject() instanceof IArticle) {
				IArticle article = (IArticle) res.getObject();
				article.setMarked(!article.isMarked());
				IServerStoreFacade serverStoreFacade = ServerStoreFactory
						.instance().getServerStoreFacade();
				try {
					serverStoreFacade.updateArticle(article);
				} catch (StoreException e) {
					return new ExecutionException(e.getMessage(), e);
				}
			}
		return null;
	}
}
