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
package org.eclipse.ecf.salvo.ui.internal.provider;

import org.eclipse.core.runtime.Platform;
import org.eclipse.ecf.protocol.nntp.core.Debug;
import org.eclipse.ecf.protocol.nntp.model.IArticle;
import org.eclipse.ecf.protocol.nntp.model.INewsgroup;
import org.eclipse.ecf.protocol.nntp.model.IStoreEvent;
import org.eclipse.ecf.protocol.nntp.model.IStoreEventListener;
import org.eclipse.ecf.salvo.ui.internal.resources.ISalvoResource;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.swt.widgets.Display;

public class SalvoDecorator implements ILightweightLabelDecorator,
		IStoreEventListener {

	public void decorate(Object element, IDecoration decoration) {

		Debug.log(this.getClass(), "Decorating object " + element);

		// Get the resource
		// 
		ISalvoResource resource = null;
		if (element instanceof ISalvoResource)
			resource = (ISalvoResource) element;
		else

			resource = (ISalvoResource) Platform.getAdapterManager()
					.getAdapter(element, ISalvoResource.class);

		if (resource == null)
			return;

		//
		// Get the decorations
		if (resource.getObject() instanceof INewsgroup)
			decorate((INewsgroup) resource.getObject(), decoration);

		if (resource.getObject() instanceof IArticle)
			Debug.log(getClass(), "ja hoor een article");
	}

	private void decorate(INewsgroup newsgroup, IDecoration decoration) {
		Debug.log(this.getClass(), "Decorating newsgroup: "
				+ newsgroup.getNewsgroupName() + " - "
				+ newsgroup.getArticleCount());
		decoration.addSuffix(" (" + newsgroup.getArticleCount() + ")");
	}

	public void addListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

	public void dispose() {
		// TODO Auto-generated method stub

	}

	public boolean isLabelProperty(Object element, String property) {
		// TODO Auto-generated method stub
		return false;
	}

	public void removeListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

	public void storeEvent(IStoreEvent event) {
//		Display.getDefault().asyncExec(new Runnable() {
//			public void run() {
//				Debug.log(this.getClass(),
//						"Caught store event, firing a decorator event");
//				PlatformUI.getWorkbench().getDecoratorManager().update(
//						"org.eclipse.ecf.salvo.ui.decorator1");
//			}
//		});

	}

}
