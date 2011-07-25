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

import org.apache.james.mime4j.codec.DecoderUtil;
import org.eclipse.ecf.protocol.nntp.model.IArticle;
import org.eclipse.mylyn.internal.provisional.commons.ui.AbstractNotificationPopup;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

public class SalvoNotificationPopup extends AbstractNotificationPopup {

	private IArticle[] articles;
	
	public SalvoNotificationPopup(Display display, IArticle[] articles) {
		super(display);
		this.articles = articles;
		setFadingEnabled(true);
		//setDelayClose(2000);
		scheduleAutoClose();
	}

	@Override
	protected void createTitleArea(Composite parent) {
		((GridData) parent.getLayoutData()).heightHint = 24;

		Label titleLabel = new Label(parent, SWT.NONE);
		titleLabel.setText("New Articles Recieved");
		titleLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));
		titleLabel.setCursor(parent.getDisplay().getSystemCursor(SWT.CURSOR_HAND));

		Label closeButton = new Label(parent, SWT.NONE);
		closeButton.setText("x");
		closeButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		closeButton.setCursor(parent.getDisplay().getSystemCursor(SWT.CURSOR_HAND));
		closeButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				closeFade();
			}
		});
	}

	@Override
	protected void createContentArea(Composite parent) {
		for (int i = 0; i < articles.length; i++) {
			Label label = new Label(parent, SWT.None);
			label.setText(DecoderUtil.decodeEncodedWords(articles[i].getSubject()));
			label.setBackground(parent.getBackground());
		}
	}

	@Override
	protected String getPopupShellTitle() {
		return "New Article";
	}
}