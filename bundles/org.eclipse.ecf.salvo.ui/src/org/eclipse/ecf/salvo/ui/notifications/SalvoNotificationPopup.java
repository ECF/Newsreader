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
import org.eclipse.ecf.salvo.ui.internal.Activator;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.mylyn.internal.provisional.commons.ui.AbstractNotificationPopup;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

public class SalvoNotificationPopup extends AbstractNotificationPopup {

	private IArticle[] articles;

	public SalvoNotificationPopup(Display display, IArticle[] articles) {
		super(display);
		this.articles = articles;
		setFadingEnabled(true);
		// setDelayClose(2000);
		scheduleAutoClose();
	}

	@Override
	protected void createTitleArea(Composite parent) {
		((GridData) parent.getLayoutData()).heightHint = 24;

		Label titleLabel = new Label(parent, SWT.NONE);
		titleLabel.setText(Integer.toString(articles.length)+" New Articles Recieved");
		titleLabel
				.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));
		titleLabel.setCursor(parent.getDisplay().getSystemCursor(
				SWT.CURSOR_HAND));

		Label closeButton = new Label(parent, SWT.NONE);
		closeButton.setText("x");
		closeButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		closeButton.setCursor(parent.getDisplay().getSystemCursor(
				SWT.CURSOR_HAND));
		closeButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				closeFade();
			}
		});
	}

	@Override
	protected void createContentArea(Composite parent) {

		Composite composite = new Composite(parent, SWT.NULL);
		composite.setLayout(new GridLayout(2, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));

		{
			Composite imageComposite = new Composite(composite, SWT.NULL);
			imageComposite.setLayoutData(new GridData(
					GridData.HORIZONTAL_ALIGN_BEGINNING));
			imageComposite.setLayout(new GridLayout(1, false));

			// Image
			Label lblImage = new Label(imageComposite, SWT.NONE);
			lblImage.setImage(Activator.getDefault().getImageRegistry()
					.get("news.gif"));
			lblImage.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
					false, 1, 1));

			// No of Articles
			Label lblNumOfArticles = new Label(imageComposite, SWT.NONE);

			Font font = JFaceResources.getBannerFont();
			FontData fd = font.getFontData()[0];
			fd.setStyle(SWT.BOLD);
			lblNumOfArticles.setFont(new Font(font.getDevice(), fd));
			lblNumOfArticles.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
					true, false, 1, 1));
			lblNumOfArticles.setText(Integer.toString(articles.length));
		}

		{
			Composite txtComposite = new Composite(composite, SWT.NULL);
			txtComposite.setLayoutData(new GridData(
					GridData.HORIZONTAL_ALIGN_END));
			txtComposite.setLayout(new GridLayout(1, false));

			if (articles.length < 10) {
				for (int i = 0; i < articles.length; i++) {
					Label dataLabel = new Label(txtComposite, SWT.None);
					dataLabel.setText(DecoderUtil.decodeEncodedWords(articles[i]
							.getSubject()));
					dataLabel.setBackground(parent.getBackground());
				}
			} else {
				for (int i = 0; i < 10; i++) {
					Label dataLabel = new Label(txtComposite, SWT.None);
					dataLabel.setText(DecoderUtil.decodeEncodedWords(articles[i]
							.getSubject()));
					dataLabel.setBackground(parent.getBackground());
				}

				Label lastDataLabel = new Label(txtComposite, SWT.None);
				lastDataLabel.setText("and " + (articles.length - 10)
						+ " other articles received.....");
				lastDataLabel.setBackground(parent.getBackground());
				
				Font font = JFaceResources.getDefaultFont();
				FontData fd = font.getFontData()[0];
				fd.setStyle(SWT.BOLD | SWT.ITALIC);
				lastDataLabel.setFont(new Font(font.getDevice(), fd));
			}

		}

	}

	@Override
	protected String getPopupShellTitle() {
		return "New Article";
	}
}