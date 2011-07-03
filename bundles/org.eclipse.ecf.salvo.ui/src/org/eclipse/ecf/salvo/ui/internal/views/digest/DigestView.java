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
import org.eclipse.ecf.protocol.nntp.model.IServer;
import org.eclipse.ecf.protocol.nntp.model.NNTPException;
import org.eclipse.ecf.salvo.ui.internal.dialogs.SelectServerDialog;
import org.eclipse.ecf.salvo.ui.tools.ImageUtils;
import org.eclipse.ecf.salvo.ui.tools.PreferencesUtil;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.swt.widgets.Label;

/**
 * This ViewPart provides the Digest View of Salvo
 * Digest View shows a digest of articles the user interested in    
 * @author isuru
 * 
 * Plese note that this functionality is still under construction
 *
 */
public class DigestView extends ViewPart{
	
	public static final String ID = "org.eclipse.ecf.salvo.ui.internal.views.digest.DigestView"; //$NON-NLS-1$
	private final FormToolkit toolkit = new FormToolkit(Display.getCurrent());
	private TreeViewer treeViewer;
	private Combo combo;
	private Action selectServer; 
	
	public DigestView() {
	}

	/**
	 * Create contents of the view part.
	 * 
	 * @param parent Parent composite
	 */
	@Override
	public void createPartControl(Composite parent) {
		Composite container = toolkit.createComposite(parent, SWT.NONE);
		toolkit.paintBordersFor(container);
		container.setLayout(new GridLayout(2, false));
		new Label(container, SWT.NONE);
		{
			combo = new Combo(container, SWT.READ_ONLY);
			combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
			toolkit.adapt(combo);
			toolkit.paintBordersFor(combo);
			
			combo.add("Show My Articles");
			combo.add("Show Marked Articles");
			
			combo.addSelectionListener(new SelectionListener() {
				
				public void widgetSelected(SelectionEvent arg0) {
					if(combo.getSelectionIndex() == 0){
						treeViewer.setContentProvider(new ThisUserArticlesContentProvider(treeViewer));
					} else {
						treeViewer.setContentProvider(new MarkedArticlesContentProvider(treeViewer));
					}
					
				}
				
				public void widgetDefaultSelected(SelectionEvent arg0) {
				}
			});
			
		}
		new Label(container, SWT.NONE);
		{
			treeViewer = new TreeViewer(container, SWT.BORDER |SWT.VIRTUAL);
			Tree tree = treeViewer.getTree();
			tree.setLinesVisible(true);
			tree.setHeaderVisible(true);
			getSite().setSelectionProvider(treeViewer);
			tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1,
					1));
			toolkit.paintBordersFor(tree);
			{
				TreeViewerColumn treeViewerColumn = new TreeViewerColumn(
						treeViewer, SWT.NONE);
				TreeColumn trclmnSubject = treeViewerColumn.getColumn();
				trclmnSubject.setWidth(433);
				trclmnSubject.setText("Subject");
			}
			{
				TreeViewerColumn treeViewerColumn = new TreeViewerColumn(
						treeViewer, SWT.NONE);
				TreeColumn trclmnDate = treeViewerColumn.getColumn();
				trclmnDate.setWidth(100);
				trclmnDate.setText("Date");
			}
			
			treeViewer.setLabelProvider(new DigestViewTreeLabelProvider());
			treeViewer.setContentProvider(new MarkedArticlesContentProvider(treeViewer));
			treeViewer.setInput(getServer());
			combo.select(1);
			
		}

		createActions();
		initializeToolBar();
		initializeMenu();
	}

	public void dispose() {
		toolkit.dispose();
		super.dispose();
	}

	/**
	 * Create the actions.
	 */
	private void createActions() {
		selectServer = new Action("Select Server") {
            public void run() { 
            	Shell shell = new Shell();
            	  SelectServerDialog selectServerDialog = new SelectServerDialog(shell); 
                  selectServerDialog.open();
                  treeViewer.setInput(getServer()); 
               }
       };
       selectServer.setToolTipText("Select Server");
       selectServer.setImageDescriptor(ImageUtils.getInstance().getImageDescriptor(
				"addserver.gif")); // TODO: Change image descriptor
	}

	/**
	 * Initialize the toolbar.
	 */
	private void initializeToolBar() {
		IToolBarManager tbm = getViewSite().getActionBars().getToolBarManager();
		tbm.add(selectServer);
	}

	/**
	 * Initialize the menu.
	 */
	private void initializeMenu() {
		IMenuManager manager = getViewSite().getActionBars().getMenuManager();
		manager.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager arg0) {
			}
		});
		
		manager.add(selectServer);
	}

	@Override
	public void setFocus() {
	}
	
	public IServer getServer(){
		String selectedServerForDigest = PreferencesUtil.instance()
		.loadPluginSettings("selectedServerForDigest");
		
		IServer[] servers;
		try {
			servers = ServerStoreFactory.instance()
			.getServerStoreFacade().getFirstStore().getServers();

			for (int i=0,length = servers.length; i<length; i++){
				if (servers[i].getID().equals(selectedServerForDigest)){
					return servers[i];
				}
			}
			
		} catch (NNTPException e) {
			Debug.log(getClass(), e);
		}
			
		return null;
	}
}
