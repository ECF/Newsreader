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
package org.eclipse.ecf.salvo.ui.internal.views;

import java.io.ByteArrayInputStream;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.james.mime4j.codec.DecoderUtil;
import org.apache.james.mime4j.parser.MimeStreamParser;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.ecf.protocol.nntp.core.Debug;
import org.eclipse.ecf.protocol.nntp.core.ServerStoreFactory;
import org.eclipse.ecf.protocol.nntp.model.IArticle;
import org.eclipse.ecf.protocol.nntp.model.IServerStoreFacade;
import org.eclipse.ecf.protocol.nntp.model.NNTPException;
import org.eclipse.ecf.protocol.nntp.model.SALVO;
import org.eclipse.ecf.salvo.ui.internal.MimeArticleContentHandler;
import org.eclipse.ecf.salvo.ui.internal.provider.SignatureProvider;
import org.eclipse.ecf.salvo.ui.internal.resources.ISalvoResource;
import org.eclipse.ecf.salvo.ui.tools.SelectionUtil;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class ReplyView {

	public static final String ID = "org.eclipse.ecf.salvo.ui.internal.views.replyView";

	private Text bodyText;

	@Inject
	MDirtyable dirty;

	private IArticle article;

	public ReplyView() {

	}

	@PreDestroy
	public void dispose() {
	}

	@PostConstruct
	public void createPartControl(
			Composite parent,
			MPart part,
			@Optional @Named(IServiceConstants.ACTIVE_SELECTION) ISalvoResource resource) {

		bodyText = new Text(parent, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL
				| SWT.MULTI);

		if (!(resource != null && resource.getObject() instanceof IArticle)) {
			bodyText.setText("Reply to what?");
			bodyText.setEditable(false);
			return;
		}

		// FIXME same code is used in MessageView

		article = (IArticle) resource.getObject();

		StringBuffer buffer = new StringBuffer();
		String[] body;
		try {
			body = (String[]) ServerStoreFactory.instance()
					.getServerStoreFacade().getArticleBody(article);
		} catch (Exception e1) {
			body = new String[] { e1.getMessage() };
		}
		for (String line : body) {
			buffer.append(line + SALVO.CRLF);
		}

		MimeArticleContentHandler handler = new MimeArticleContentHandler(
				article);
		MimeStreamParser parser = new MimeStreamParser();
		parser.setContentHandler(handler);

		try {
			parser.parse(new ByteArrayInputStream(buffer.toString().getBytes()));
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		buffer = new StringBuffer();
		buffer.append(" " + SALVO.CRLF);
		buffer.append(" " + SALVO.CRLF);
		buffer.append(SignatureProvider.getSignature(article.getNewsgroup()));
		buffer.append(" " + SALVO.CRLF);
		for (String line : handler.getBody().split(SALVO.CRLF)) {
			if (line.startsWith(">"))
				buffer.append(">" + line + SALVO.CRLF);
			else
				buffer.append("> " + line + SALVO.CRLF);
		}

		bodyText.setText(buffer.toString());
		final int textChangeHash = bodyText.getText().hashCode();
		bodyText.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {
				dirty.setDirty(false);
				System.out.println(textChangeHash + " - "
						+ bodyText.getText().hashCode());
				if (textChangeHash != bodyText.getText().hashCode()) {
					dirty.setDirty(true);
				}
			}
		});

		part.setDescription("Reply to: " + article.getNewsgroup());
		part.setLabel(DecoderUtil.decodeEncodedWords(article.getSubject()
				.startsWith("Re: ") ? article.getSubject() : "Re: "
				+ article.getSubject()));
	}

	@Focus
	public void setFocus() {
		bodyText.setFocus();
		bodyText.forceFocus();
	}

	@Persist
	public void doSave(IProgressMonitor monitor, final MPart part, Shell shell,
			UISynchronize syncer, final EPartService partService) {

		final StringBuffer buffer = new StringBuffer(bodyText.getText());

		monitor.beginTask("Posting", 43);

		for (int i = 0; i < 5; i++) {
			monitor.subTask("You have " + (5 - i) + " seconds to cancel");
			for (int i2 = 0; i2 < 10; i2++) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					monitor.setCanceled(true);
				}
				if (monitor.isCanceled()) {
					monitor.setCanceled(true);
					return;
				}
				monitor.worked(1);
			}
		}

		monitor.subTask("Posting to newsgroup "
				+ article.getNewsgroup().getNewsgroupName());
		monitor.worked(1);
		IServerStoreFacade serverStoreFacade = ServerStoreFactory.instance()
				.getServerStoreFacade();
		monitor.worked(1);

		try {
			serverStoreFacade.replyToArticle(part.getLabel(), article,
					buffer.toString());
		} catch (NNTPException e) {
			Debug.log(getClass(), e);
			MessageDialog.openError(
					shell,
					"Problem posting message",
					"The message could not be posted.\r\n" + "Due to "
							+ e.getMessage());
			monitor.setCanceled(true);
			return;
		}
		monitor.done();

		dirty.setDirty(false);
		syncer.asyncExec(new Runnable() {
			public void run() {
				partService.hidePart(part, true);
			}
		});
	}
}
