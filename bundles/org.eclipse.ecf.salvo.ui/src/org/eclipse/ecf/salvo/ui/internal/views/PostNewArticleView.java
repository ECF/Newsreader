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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ecf.protocol.nntp.core.ServerStoreFactory;
import org.eclipse.ecf.protocol.nntp.model.IArticle;
import org.eclipse.ecf.protocol.nntp.model.INewsgroup;
import org.eclipse.ecf.protocol.nntp.model.IServerStoreFacade;
import org.eclipse.ecf.protocol.nntp.model.NNTPException;
import org.eclipse.ecf.protocol.nntp.model.SALVO;
import org.eclipse.ecf.salvo.ui.internal.preferences.PreferenceModel;
import org.eclipse.ecf.salvo.ui.internal.provider.SignatureProvider;
import org.eclipse.ecf.salvo.ui.internal.resources.ISalvoResource;
import org.eclipse.ecf.salvo.ui.tools.SelectionUtil;
import org.eclipse.jface.commands.ActionHandler;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISaveablePart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.internal.WorkbenchPage;
import org.eclipse.ui.part.ViewPart;

public class PostNewArticleView extends ViewPart implements ISaveablePart {

	private Text bodyText;

	private boolean dirty = false;

	private Text subjectText;

	private boolean once;

	private INewsgroup newsgroup;

	private Point location;

	public PostNewArticleView() {

	}

	@Override
	public void dispose() {
	}

	@Override
	public void createPartControl(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		{
			Composite group = new Composite(composite, SWT.NONE);
			group.setLayout(new GridLayout(2, false));
			group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
			{
				Label lblSubject = new Label(group, SWT.NONE);
				lblSubject.setBounds(0, 0, 55, 15);
				lblSubject.setText("Subject:");
			}
			{
				subjectText = new Text(group, SWT.BORDER);
				subjectText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
				subjectText.addKeyListener(new KeyAdapter() {

					@Override
					public void keyReleased(KeyEvent e) {
						setPartName(subjectText.getText());
						dirty = true;
						firePropertyChange(PROP_DIRTY);
					}
				});
			}
		}
		bodyText = new Text(composite, SWT.BORDER | SWT.MULTI | SWT.WRAP);
		bodyText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		bodyText.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {
				dirty = true;
				firePropertyChange(PROP_DIRTY);
			}
		});

		location = composite.getShell().getLocation();

		ISalvoResource resource = (ISalvoResource) SelectionUtil
				.getFirstObjectFromCurrentSelection(ISalvoResource.class);

		if (resource != null && resource.getObject() instanceof IArticle) {
			newsgroup = ((IArticle) resource.getObject()).getNewsgroup();
		} else if (resource != null && resource.getObject() instanceof INewsgroup) {
			newsgroup = (INewsgroup) resource.getObject();
		}

		IHandlerService handlerService = (IHandlerService) getViewSite().getService(IHandlerService.class);
		handlerService.activateHandler("org.eclipse.ui.file.save", new ActionHandler(ActionFactory.SAVE
				.create(getSite().getWorkbenchWindow())));

		bodyText.setText(SALVO.CRLF + SignatureProvider.getSignature(newsgroup));

	}

	@Override
	public void setFocus() {
		if (!once) {
			once = true;
			IViewReference ref = getSite().getPage().findViewReference(
					"org.eclipse.ecf.salvo.ui.internal.views.postNewArticleView", "1");
			if (PreferenceModel.instance.getUseDetachedView()) {
				((WorkbenchPage) getSite().getPage()).getActivePerspective().getPresentation()
						.detachPart(ref);
				getViewSite().getShell().setSize(600, 450);
				getViewSite().getShell().setLocation(location.x + 100, location.y + 100);
			} else
				((WorkbenchPage) getSite().getPage()).getActivePerspective().getPresentation()
						.attachPart(ref);

		}
		subjectText.setFocus();
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

		monitor.subTask("Posting to newsgroup " + newsgroup.getNewsgroupName());
		monitor.worked(1);
		IServerStoreFacade serverStoreFacade = ServerStoreFactory.instance().getServerStoreFacade();
		monitor.worked(1);

		try {
			serverStoreFacade.postNewArticle(new INewsgroup[] { newsgroup }, subjectText.getText(), buffer
					.toString());
		} catch (NNTPException e) {
			MessageDialog.openError(getViewSite().getShell(), "Problem posting message",
					"The message could not be posted. \n\r" + e.getMessage());
		}
		monitor.done();
		dirty = false;
		firePropertyChange(PROP_DIRTY);
		final PostNewArticleView view = this;
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
