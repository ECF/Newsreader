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

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ITreeContentProvider;

public class DigestView extends ViewPart {
	
	public static final String ID = "org.eclipse.ecf.salvo.ui.internal.views.digest.DigestView"; //$NON-NLS-1$
	private final FormToolkit toolkit = new FormToolkit(Display.getCurrent());
	
	public DigestView() {
	}

	/**
	 * Create contents of the view part.
	 * 
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {
		Composite container = toolkit.createComposite(parent, SWT.NONE);
		toolkit.paintBordersFor(container);
		container.setLayout(new GridLayout(1, false));
		{
			Combo combo = new Combo(container, SWT.READ_ONLY);
			combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
			toolkit.adapt(combo);
			toolkit.paintBordersFor(combo);
			
			combo.add("Show My Articles");
			combo.add("Show Marked Articles");
			combo.select(0);
			
		}
		{
			TreeViewer treeViewer = new TreeViewer(container, SWT.BORDER |SWT.VIRTUAL);
			Tree tree = treeViewer.getTree();
			tree.setLinesVisible(true);
			tree.setHeaderVisible(true);
			tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1,
					1));
			toolkit.paintBordersFor(tree);
			{
				TreeViewerColumn treeViewerColumn = new TreeViewerColumn(
						treeViewer, SWT.NONE);
				TreeColumn trclmnSubject = treeViewerColumn.getColumn();
				trclmnSubject.setWidth(399);
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
			treeViewer.setContentProvider(new DigestViewTreeContentProvider());
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
		// Create the actions
	}

	/**
	 * Initialize the toolbar.
	 */
	private void initializeToolBar() {
		IToolBarManager tbm = getViewSite().getActionBars().getToolBarManager();
	}

	/**
	 * Initialize the menu.
	 */
	private void initializeMenu() {
		IMenuManager manager = getViewSite().getActionBars().getMenuManager();
	}

	@Override
	public void setFocus() {
		// Set the focus
	}

}
