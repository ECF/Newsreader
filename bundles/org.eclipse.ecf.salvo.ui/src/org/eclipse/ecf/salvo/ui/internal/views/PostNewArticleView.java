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

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.ecf.protocol.nntp.core.ServerStoreFactory;
import org.eclipse.ecf.protocol.nntp.model.IArticle;
import org.eclipse.ecf.protocol.nntp.model.INewsgroup;
import org.eclipse.ecf.protocol.nntp.model.IServerStoreFacade;
import org.eclipse.ecf.protocol.nntp.model.NNTPException;
import org.eclipse.ecf.protocol.nntp.model.SALVO;
import org.eclipse.ecf.salvo.ui.internal.preferences.PreferenceModel;
import org.eclipse.ecf.salvo.ui.internal.provider.SignatureProvider;
import org.eclipse.ecf.salvo.ui.internal.resources.ISalvoResource;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class PostNewArticleView {

	public static final String ID = "org.eclipse.ecf.salvo.ui.internal.views.postNewArticleView";

	private Text bodyText;

	@Inject
	MDirtyable dirty;

	@Inject
	UISynchronize sync;

	private Text subjectText;

	private boolean once;

	private INewsgroup newsgroup;

	private Point location;

	private ISalvoResource activeResource;

	public PostNewArticleView() {

	}

	@PreDestroy
	public void dispose() {
	}

	@PostConstruct
	public void createPartControl(Composite parent, final MPart part,
			final MDirtyable dirty) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		{
			Composite group = new Composite(composite, SWT.NONE);
			group.setLayout(new GridLayout(2, false));
			group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
					1, 1));
			{
				Label lblSubject = new Label(group, SWT.NONE);
				lblSubject.setBounds(0, 0, 55, 15);
				lblSubject.setText("Subject:");
			}
			{
				subjectText = new Text(group, SWT.BORDER);
				subjectText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
						true, false, 1, 1));
				subjectText.addKeyListener(new KeyAdapter() {

					@Override
					public void keyReleased(KeyEvent e) {
						part.setLabel(subjectText.getText());
						dirty.setDirty(true);
					}
				});
			}
		}
		bodyText = new Text(composite, SWT.BORDER | SWT.MULTI | SWT.WRAP);
		bodyText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1,
				1));
		bodyText.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {
				dirty.setDirty(true);
			}
		});

		location = composite.getShell().getLocation();


		if (activeResource != null && activeResource.getObject() instanceof IArticle) {
			newsgroup = ((IArticle) activeResource.getObject()).getNewsgroup();
		} else if (activeResource != null
				&& activeResource.getObject() instanceof INewsgroup) {
			newsgroup = (INewsgroup) activeResource.getObject();
		}

		bodyText.setText(SALVO.CRLF + SignatureProvider.getSignature(newsgroup));

	}

	@Focus
	public void setFocus(MPart part, EPartService partService,
			EModelService service) {

		// A hack to detach the view from the workbench
		if (!once) {
			once = true;
			if (PreferenceModel.instance.getUseDetachedView()) {
				service.detach(part, 100, 100, 300, 300);
			}
		}
		subjectText.setFocus();
	}

	@Persist
	public void doSave(IProgressMonitor monitor, Shell shell, final MPart part,
			final EPartService partService) {

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
		IServerStoreFacade serverStoreFacade = ServerStoreFactory.instance()
				.getServerStoreFacade();
		monitor.worked(1);

		try {
			serverStoreFacade.postNewArticle(new INewsgroup[] { newsgroup },
					subjectText.getText(), buffer.toString());
		} catch (NNTPException e) {
			MessageDialog.openError(shell, "Problem posting message",
					"The message could not be posted. \n\r" + e.getMessage());
		}
		monitor.done();
		dirty.setDirty(false);
		final PostNewArticleView view = this;
		sync.asyncExec(new Runnable() {
			public void run() {
				partService.hidePart(part, true);
			}
		});

	}

	@Inject
	public void selectionChanged(
			@Optional @Named(IServiceConstants.ACTIVE_SELECTION) ISalvoResource resource) {
		this.activeResource = resource;
	}
}