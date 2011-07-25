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
package org.eclipse.ecf.salvo.ui.notifications;

import org.eclipse.ecf.protocol.nntp.core.ArticleEventListnersFactory;
import org.eclipse.ecf.protocol.nntp.model.IArticleEvent;
import org.eclipse.ecf.protocol.nntp.model.IArticleEventListner;
import org.eclipse.ecf.protocol.nntp.model.IArticleEventListnersRegistry;
import org.eclipse.mylyn.internal.provisional.commons.ui.AbstractNotificationPopup;
import org.eclipse.swt.widgets.Display;

public class NotificationHandler implements IArticleEventListner {

	private Display display;

	public NotificationHandler(Display display) {
		this.display = display;
		IArticleEventListnersRegistry articleEventListnerRegistry = ArticleEventListnersFactory
				.instance().getRegistry();
		articleEventListnerRegistry.addListener(this);
	}

	public void execute(final IArticleEvent event) {

		Display.getDefault().asyncExec(new Runnable() {

			public void run() {
				AbstractNotificationPopup popup = new SalvoNotificationPopup(
						display, event.getArticles());
				popup.open();
			}
		});

	}
}
