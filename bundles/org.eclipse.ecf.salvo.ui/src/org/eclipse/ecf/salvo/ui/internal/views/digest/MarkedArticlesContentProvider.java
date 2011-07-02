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
package org.eclipse.ecf.salvo.ui.internal.views.digest;

import org.eclipse.ecf.protocol.nntp.core.Debug;
import org.eclipse.ecf.protocol.nntp.core.ServerStoreFactory;
import org.eclipse.ecf.protocol.nntp.model.IArticle;
import org.eclipse.ecf.protocol.nntp.model.INewsgroup;
import org.eclipse.ecf.protocol.nntp.model.IServer;
import org.eclipse.ecf.protocol.nntp.model.IServerStoreFacade;
import org.eclipse.ecf.protocol.nntp.model.NNTPException;
import org.eclipse.ecf.protocol.nntp.model.StoreException;
import org.eclipse.jface.viewers.ILazyTreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;

/**
 * This provides the MarkedArticles for Digest View tree
 * 
 * @author isuru
 * 
 *         Plese note that this functionality is still under construction
 * 
 */
class MarkedArticlesContentProvider implements ILazyTreeContentProvider {

	private TreeViewer viewer;
	private IServerStoreFacade serverStoreFacade;

	public MarkedArticlesContentProvider(TreeViewer viewer) {
		this.viewer = viewer;
		serverStoreFacade = ServerStoreFactory.instance()
				.getServerStoreFacade();
	}

	public void dispose() {
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		Debug.log(this.getClass(), "Not implemented yet");
	}

	public Object getParent(Object element) {

		if (element instanceof INewsgroup) {
			return ((INewsgroup) element).getServer();
		} else if (element instanceof IArticle) {
			return ((IArticle) element).getNewsgroup();
		}

		return null;
	}

	public void updateChildCount(Object element, int currentChildCount) {

		int length = 0;

		if (element instanceof INewsgroup) {

			INewsgroup node = (INewsgroup) element;
			length = serverStoreFacade.getMarkedArticles(node).length;

		} else if (element instanceof IServer) {

			IServer node = (IServer) element;
			try {
				length = serverStoreFacade.getSubscribedNewsgroups(node).length;
			} catch (StoreException e) {
				Debug.log(getClass(), e);
			}

		} else if (element instanceof IArticle) {
			length = currentChildCount;
		}

		viewer.setChildCount(element, length);

	}

	public void updateElement(Object parent, int index) {

		Object element = null;

		if (parent instanceof IServer) {

			try {
				element = serverStoreFacade
						.getSubscribedNewsgroups((IServer) parent)[index];
				viewer.replace(parent, index, element);
				updateChildCount(element, -1);

			} catch (StoreException e) {
				Debug.log(getClass(), e);
			}

		} else if (parent instanceof INewsgroup) {

			element = serverStoreFacade.getMarkedArticles((INewsgroup) parent)[index];
			viewer.replace(parent, index, element);
			try {
				updateChildCount(
						element,
						serverStoreFacade.getFollowUps((IArticle) element).length);
			} catch (NNTPException e) {
				Debug.log(getClass(), e);
			}

		} else if (parent instanceof IArticle) {

			IArticle parentArticle = (IArticle) parent;

			try {
				element = serverStoreFacade
						.getFollowUps((IArticle) parentArticle)[index];
				viewer.replace(parent, index, element);
				updateChildCount(
						element,
						serverStoreFacade.getFollowUps((IArticle) element).length);
			} catch (NNTPException e) {
				Debug.log(getClass(), e);
			}

		}

	}

}