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
import org.eclipse.ecf.protocol.nntp.core.Debug;
import org.eclipse.ecf.protocol.nntp.model.INewsgroup;
import org.eclipse.ecf.salvo.ui.internal.editor.ArticlePanel;
import org.eclipse.ecf.salvo.ui.internal.editor.ArticlePanelInput;
import org.eclipse.ecf.salvo.ui.internal.resources.ISalvoResource;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;


public class OpenArticlePanel extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {

		if (HandlerUtil.getCurrentSelection(event) instanceof IStructuredSelection) {

			IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getCurrentSelection(event);

			for (Object obj : selection.toList()) {
				INewsgroup group = (INewsgroup) ((ISalvoResource) obj).getObject();

				try {
					HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().openEditor(
							new ArticlePanelInput(group), ArticlePanel.ID);
				} catch (PartInitException e) {
					Debug.log(this.getClass(), e.getMessage());
				}
			}
		}

		return null;
	}

}
