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
package org.eclipse.ecf.salvo.ui.navigator.internal.views;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ecf.protocol.nntp.core.Debug;
import org.eclipse.ecf.protocol.nntp.core.ServerStoreFactory;
import org.eclipse.ecf.protocol.nntp.core.StoreStore;
import org.eclipse.ecf.protocol.nntp.model.INewsgroup;
import org.eclipse.ecf.protocol.nntp.model.IServer;
import org.eclipse.ecf.protocol.nntp.model.IStore;
import org.eclipse.ecf.protocol.nntp.model.IStoreEvent;
import org.eclipse.ecf.protocol.nntp.model.IStoreEventListener;
import org.eclipse.ecf.protocol.nntp.model.SALVO;
import org.eclipse.ecf.salvo.ui.internal.editor.ArticlePanel;
import org.eclipse.ecf.salvo.ui.internal.editor.ArticlePanelInput;
import org.eclipse.ecf.salvo.ui.internal.provider.SubscribedServerProvider;
import org.eclipse.ecf.salvo.ui.internal.resources.ISalvoResource;
import org.eclipse.ecf.salvo.ui.internal.resources.SalvoResourceFactory;
import org.eclipse.ecf.salvo.ui.tools.SelectionUtil;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.navigator.CommonNavigator;

public class GroupView extends CommonNavigator implements IStoreEventListener {
	public static final String ID = "org.eclipse.ui.salvo.application.view";

	@Override
	protected IAdaptable getInitialInput() {
		StoreStore.instance().addListener(this, SALVO.EVENT_STORE);
		ISalvoResource root = SalvoResourceFactory.getResource("root", "root");
		root.setChildProvider(new SubscribedServerProvider());
		for (IStore store : ServerStoreFactory.instance()
				.getServerStoreFacade().getStores())
			store.addListener(this, SALVO.EVENT_ALL_EVENTS);

		return root;
	}

	@Override
	protected void handleDoubleClick(DoubleClickEvent anEvent) {

		ISalvoResource group = (ISalvoResource) SelectionUtil
				.getFirstObjectFromSelection(anEvent.getSelection(),
						ISalvoResource.class);
		if (group != null && group.getObject() instanceof INewsgroup)
			try {
				getSite().getPage().openEditor(
						new ArticlePanelInput((INewsgroup) group.getObject()),
						ArticlePanel.ID);
				return;
			} catch (PartInitException e) {
			}
		super.handleDoubleClick(anEvent);
	}

	public void storeEvent(final IStoreEvent event) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if (event.getEventObject() instanceof INewsgroup
						|| event.getEventObject() instanceof IServer
						|| event.getEventObject() instanceof IStore) {
					Debug.log(this.getClass(), "Event: " + event.getEventType());
					TreePath[] elements = getCommonViewer()
							.getExpandedTreePaths();
					getCommonViewer().getTree().setRedraw(false);
					getCommonViewer().refresh();
					getCommonViewer().setExpandedTreePaths(elements);
					getCommonViewer().getTree().setRedraw(true);
				}
			}
		});
	}
}