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

import org.apache.james.mime4j.codec.DecoderUtil;
import org.apache.james.mime4j.parser.MimeStreamParser;
import org.eclipse.core.runtime.IProgressMonitor;
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
import org.eclipse.jface.commands.ActionHandler;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISaveablePart;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.ViewPart;

public class ReplyView extends ViewPart implements ISaveablePart {

	private Text bodyText;

	private boolean dirty = false;

	private IArticle article;

	public ReplyView() {

	}

	@Override
	public void dispose() {
	}

	@Override
	public void createPartControl(Composite parent) {

		bodyText = new Text(parent, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);

		ISalvoResource resource = (ISalvoResource) SelectionUtil
				.getFirstObjectFromCurrentSelection(ISalvoResource.class);

		if (resource != null && resource.getObject() instanceof IArticle) {

			// FIXME same code is used in MessageView

			article = (IArticle) resource.getObject();

			StringBuffer buffer = new StringBuffer();
			String[] body;
			try {
				body = (String[]) ServerStoreFactory.instance().getServerStoreFacade()
						.getArticleBody(article);
			} catch (Exception e1) {
				body = new String[] { e1.getMessage() };
			}
			for (String line : body) {
				buffer.append(line + SALVO.CRLF);
			}

			MimeArticleContentHandler handler = new MimeArticleContentHandler(article);
			MimeStreamParser parser = new MimeStreamParser();
			parser.setContentHandler(handler);

			try {
				parser.parse(new ByteArrayInputStream(buffer.toString().getBytes()));
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			// for (Control child : parent.getChildren()) {
			// child.dispose();
			// }

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
					dirty = false;
					System.out.println(textChangeHash + " - " + bodyText.getText().hashCode());
					if (textChangeHash != bodyText.getText().hashCode()) {
						dirty = true;
					}
					firePropertyChange(PROP_DIRTY);
				}
			});

			setContentDescription("Reply to: " + article.getNewsgroup());
			setPartName(DecoderUtil.decodeEncodedWords(article.getSubject().startsWith("Re: ") ? article
					.getSubject() : "Re: " + article.getSubject()));
		}

		IHandlerService handlerService = (IHandlerService) getViewSite().getService(IHandlerService.class);
		handlerService.activateHandler("org.eclipse.ui.file.save", new ActionHandler(ActionFactory.SAVE
				.create(getSite().getWorkbenchWindow())));

		// getViewSite().getActionBars().setGlobalActionHandler("org.eclipse.ui.file.save",
		// ActionFactory.SAVE.create(getSite().getWorkbenchWindow()));

	}

	@Override
	public void setFocus() {
		bodyText.setFocus();
		bodyText.forceFocus();
	}

	public void doSave(IProgressMonitor monitor) {

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

		monitor.subTask("Posting to newsgroup " + article.getNewsgroup().getNewsgroupName());
		monitor.worked(1);
		IServerStoreFacade serverStoreFacade = ServerStoreFactory.instance().getServerStoreFacade();
		monitor.worked(1);

		try {
			serverStoreFacade.replyToArticle(article, buffer.toString());
		} catch (NNTPException e) {
			Debug.log(getClass(), e);
			MessageDialog.openError(getViewSite().getShell(), "Problem posting message",
					"The message could not be posted.\r\n" + "Due to " + e.getMessage());
			monitor.setCanceled(true);
			return;
		}
		monitor.done();
		dirty = false;
		firePropertyChange(PROP_DIRTY);
		final ReplyView view = this;
		getViewSite().getShell().getDisplay().asyncExec(new Runnable() {
			public void run() {
				getViewSite().getPage().hideView(view);
			}
		});
	}

	public void doSaveAs() {
	}

	public boolean isDirty() {
		return dirty;
	}

	public boolean isSaveAsAllowed() {
		return false;
	}

	public boolean isSaveOnCloseNeeded() {
		return true;
	}
}
