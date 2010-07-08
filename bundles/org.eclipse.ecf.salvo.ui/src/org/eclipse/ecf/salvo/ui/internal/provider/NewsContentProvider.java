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

import org.eclipse.ecf.protocol.nntp.model.IStoreEvent;
import org.eclipse.ecf.protocol.nntp.model.IStoreEventListener;
import org.eclipse.ecf.salvo.ui.internal.resources.ISalvoResource;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.navigator.ICommonContentExtensionSite;
import org.eclipse.ui.navigator.ICommonContentProvider;


public class NewsContentProvider implements ICommonContentProvider, IStoreEventListener {

	private Viewer viewer;

	private ISalvoResource newInput;

	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof ISalvoResource) {
			return ((ISalvoResource) parentElement).getChildren().toArray();
		}

		return null;
	}

	public Object getParent(Object element) {
		if (element instanceof ISalvoResource) {
			return ((ISalvoResource) element).getParent();
		}
		return null;
	}

	public boolean hasChildren(Object element) {
		if (element instanceof ISalvoResource) {
			return ((ISalvoResource) element).hasChildren();
		}
		return false;
	}

	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof ISalvoResource) {
			return ((ISalvoResource) inputElement).getChildren().toArray();
		}

		return null;
	}

	public void dispose() {
//		ServerStoreFactory.instance().getServerStoreFacade().getStore().removeListener(this);
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (newInput != null) {
			this.viewer = viewer;
			this.newInput = (ISalvoResource) newInput;
//			ServerStoreFactory.instance().getServerStoreFacade().getStore().addListener(
//					this,
//					SALVO.EVENT_ADD_GROUP | SALVO.EVENT_REMOVE_GROUP | SALVO.EVENT_CHANGE_GROUP
//							| SALVO.EVENT_ADD_SERVER | SALVO.EVENT_REMOVE_SERVER | SALVO.EVENT_CHANGE_SERVER);
		} else {
//			ServerStoreFactory.instance().getServerStoreFacade().getStore().removeListener(this);
		}

	}

	public void init(ICommonContentExtensionSite config) {
		// TODO Auto-generated method stub

	}

	public void restoreState(IMemento memento) {
		// TODO Auto-generated method stub

	}

	public void saveState(IMemento memento) {
		// TODO Auto-generated method stub

	}

	public void storeEvent(IStoreEvent event) {
		if (viewer instanceof TreeViewer && !viewer.getControl().isDisposed()) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					((TreeViewer) viewer).refresh(newInput);
				}
			});
		}
	}

}
