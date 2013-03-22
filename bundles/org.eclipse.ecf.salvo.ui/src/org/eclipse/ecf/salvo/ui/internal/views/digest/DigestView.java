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

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.e4.ui.workbench.swt.modeling.EMenuService;
import org.eclipse.ecf.protocol.nntp.core.ArticleEventListnersFactory;
import org.eclipse.ecf.protocol.nntp.core.Debug;
import org.eclipse.ecf.protocol.nntp.core.ServerStoreFactory;
import org.eclipse.ecf.protocol.nntp.model.IArticleEvent;
import org.eclipse.ecf.protocol.nntp.model.IArticleEventListner;
import org.eclipse.ecf.protocol.nntp.model.IArticleEventListnersRegistry;
import org.eclipse.ecf.protocol.nntp.model.IServer;
import org.eclipse.ecf.protocol.nntp.model.IStore;
import org.eclipse.ecf.protocol.nntp.model.IStoreEvent;
import org.eclipse.ecf.protocol.nntp.model.IStoreEventListener;
import org.eclipse.ecf.protocol.nntp.model.NNTPException;
import org.eclipse.ecf.protocol.nntp.model.SALVO;
import org.eclipse.ecf.salvo.ui.internal.Activator;
import org.eclipse.ecf.salvo.ui.internal.dialogs.SelectServerDialog;
import org.eclipse.ecf.salvo.ui.tools.ImageUtils;
import org.eclipse.ecf.salvo.ui.tools.PreferencesUtil;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.layout.TreeColumnLayout;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;

/**
 * This ViewPart provides the Digest View of Salvo Digest View shows a digest of
 * articles the user interested in
 * 
 */
public class DigestView implements IArticleEventListner, ServiceListener,
		IStoreEventListener {

	@Inject
	ESelectionService service;

	public static final String ID = "org.eclipse.ecf.salvo.ui.internal.views.digest.DigestView";
	private TreeViewer treeViewer;
	private Combo combo;
	private Action selectServerAction;
	private BundleContext context;
	private IArticleEventListnersRegistry articleEventListnerRegistry;

	public DigestView() {

		context = Activator.getDefault().getBundleContext();
		context.addServiceListener(this); // listening to store
											// register/unregister

		for (IStore store : ServerStoreFactory.instance()
				.getServerStoreFacade().getStores()) {
			store.addListener(this, SALVO.EVENT_SUBSCRIBE_UNSUBSCRIBE);
		}

		articleEventListnerRegistry = ArticleEventListnersFactory.instance()
				.getRegistry();
		articleEventListnerRegistry.addListener(this);

	}

	/**
	 * Create contents of the view part.
	 * 
	 * @param parent
	 *            Parent composite
	 */
	@PostConstruct
	public void createPartControl(Composite parent, EMenuService menuservice) {

		Composite container = new Composite(parent, SWT.None);
		container.setLayout(new GridLayout(2, false));

		new Label(container, SWT.NONE);
		{
			combo = new Combo(container, SWT.READ_ONLY);
			combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
					1, 1));

			combo.add("Show Threads I am following");
			combo.add("Show My Articles");

			combo.addSelectionListener(new SelectionListener() {

				public void widgetSelected(SelectionEvent arg0) {

					treeViewer.getTree().removeAll();
					selectContentProvider();
				}

				public void widgetDefaultSelected(SelectionEvent arg0) {
				}
			});

		}

		new Label(container, SWT.NONE);
		final Composite treeComposite = new Composite(container, SWT.NONE);
		treeComposite
				.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		{
			treeViewer = new TreeViewer(treeComposite, SWT.FULL_SELECTION
					| SWT.BORDER | SWT.VIRTUAL);
			Tree tree = treeViewer.getTree();
			tree.setLinesVisible(true);
			tree.setHeaderVisible(true);
			treeViewer
					.addSelectionChangedListener(new ISelectionChangedListener() {

						@Override
						public void selectionChanged(SelectionChangedEvent event) {
							service.setPostSelection(event.getSelection());
						}
					});

			TreeColumnLayout treeColumnLayout = new TreeColumnLayout();
			{
				final TreeColumn subjectTreeColumn = new TreeColumn(tree,
						SWT.NONE);
				subjectTreeColumn.setText("Subject");
				treeColumnLayout.setColumnData(subjectTreeColumn,
						new ColumnWeightData(75));
			}
			{
				final TreeColumn dateTreeColumn = new TreeColumn(tree, SWT.NONE);
				dateTreeColumn.setText("Date");
				treeColumnLayout.setColumnData(dateTreeColumn,
						new ColumnWeightData(25));
				dateTreeColumn.setMoveable(true);
			}
			treeComposite.setLayout(treeColumnLayout);

			treeViewer.setLabelProvider(new DigestViewTreeLabelProvider());
			treeViewer.setContentProvider(new MarkedArticlesContentProvider(
					treeViewer));

			if (null != getSelectedServer()) {
				treeViewer.setInput(getSelectedServer());
			}

			combo.select(0);

		}

		createActions();
		menuservice
				.registerContextMenu(parent,
						"org.eclipse.ecf.salvo.ui.internal.views.digest.DigestView.popup");
	}

	@PreDestroy
	public void dispose() {
	}

	/**
	 * Create the actions.
	 */
	private void createActions() {

		selectServerAction = new Action("Select Server") {
			public void run() {
				Shell shell = new Shell();
				SelectServerDialog selectServerDialog = new SelectServerDialog(
						shell);
				selectServerDialog.open();
				treeViewer.setInput(getSelectedServer());
			}
		};
		selectServerAction.setToolTipText("Select Server");
		selectServerAction.setImageDescriptor(ImageUtils.getInstance()
				.getImageDescriptor("selectServer.gif"));

	}

	@Focus
	public void setFocus() {
		combo.setFocus();
	}

	/**
	 * Get selected server
	 */
	private IServer getSelectedServer() {

		String selectedServerForDigest = PreferencesUtil.instance()
				.loadPluginSettings("selectedServerForDigest");

		if (selectedServerForDigest.equals("null")) {
			try {
				return ServerStoreFactory.instance().getServerStoreFacade()
						.getFirstStore().getServers()[0];
			} catch (NNTPException e) {
				Debug.log(getClass(), e);
			} catch (NullPointerException e) {
				Debug.log(getClass(), "No servers available");
			}
		}

		IServer[] servers;
		try {
			servers = ServerStoreFactory.instance().getServerStoreFacade()
					.getFirstStore().getServers();

			for (int i = 0, length = servers.length; i < length; i++) {
				if (servers[i].getID().equals(selectedServerForDigest)) {
					return servers[i];
				}
			}
			return servers[0];

		} catch (NNTPException e) {
			Debug.log(getClass(), e);
		} catch (NullPointerException e) {
			Debug.log(getClass(), "No servers available");
		} catch (ArrayIndexOutOfBoundsException e) {
			Debug.log(getClass(), "No servers available");
		}

		return null;
	}

	public void execute(IArticleEvent event) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				try {
					TreePath[] elements = treeViewer.getExpandedTreePaths();
					treeViewer.getTree().setRedraw(false);
					treeViewer.setInput(getSelectedServer());
					treeViewer.setExpandedTreePaths(elements);
					treeViewer.getTree().setRedraw(true);
				} catch (Exception e) { // For no expanded paths
					if (null != getSelectedServer()) {
						try {
							treeViewer.setInput(getSelectedServer());
						} catch (Exception e1) {
							Debug.log(getClass(), e);
						}
					}
				}
			}
		});

	}

	public void serviceChanged(ServiceEvent event) {

		if (event.getType() == ServiceEvent.REGISTERED
				| event.getType() == ServiceEvent.UNREGISTERING) {

			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					try {
						if (null != getSelectedServer()) {
							treeViewer.setInput(getSelectedServer());
						} else {
							treeViewer.getTree().removeAll();
						}
					} catch (Exception e) {
						// Occurs when shutting down eclipse
					}
				}
			});
		}
	}

	public void storeEvent(IStoreEvent event) {
		articleEventListnerRegistry.removeListner(this);
		selectContentProvider();
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				try {
					if (null != getSelectedServer()) {
						treeViewer.setInput(getSelectedServer());
					} else {
						treeViewer.getTree().removeAll();
					}
				} catch (Exception e) {
					Debug.log(getClass(), e);
				}
				articleEventListnerRegistry.addListener(getArticleListener());
			}
		});
	}

	/**
	 * select content provider for the view
	 */
	private void selectContentProvider() {
		if (combo.getSelectionIndex() == 1) {
			treeViewer.setContentProvider(new ThisUserArticlesContentProvider(
					treeViewer));
		} else {
			treeViewer.setContentProvider(new MarkedArticlesContentProvider(
					treeViewer));
		}
	}

	/**
	 * get new article listener
	 */
	private IArticleEventListner getArticleListener() {
		return this;
	}

}
