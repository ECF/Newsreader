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
package org.eclipse.ecf.salvo.ui.internal.editor;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.e4.ui.workbench.modeling.ISelectionListener;
import org.eclipse.e4.ui.workbench.swt.modeling.EMenuService;
import org.eclipse.ecf.protocol.nntp.model.IArticle;
import org.eclipse.ecf.protocol.nntp.model.INewsgroup;
import org.eclipse.ecf.salvo.ui.internal.Activator;
import org.eclipse.ecf.salvo.ui.internal.resources.ISalvoResource;
import org.eclipse.jface.layout.TreeColumnLayout;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IWorkbenchPart;

/**
 * This editor fills a lazy table tree with the article information and to the
 * left of this table a static table is attached that contains information icons
 * like follow and attachments indicators.
 * 
 * @author wim.jongman@gmail.com
 * 
 */
public class ArticlePanel implements ISelectionListener,
		ISelectionChangedListener {

	public class ArticlePanelSideTableMeasureListener implements Listener {

		public void handleEvent(Event event) {
			event.height = tree.getItemHeight();
		}
	}

	/**
	 * Calculate the index match between the static side table and the dynamic
	 * main table and then annotate the article in the side table.
	 * 
	 * @author wim.jongman@gmail.com
	 * 
	 */
	private final class ArticlePanelTreePaintListener implements PaintListener {

		public void paintControl(PaintEvent e) {

			if (tree.getTopItem() == null)
				return;

			try {

				// Stop & start drawing of the main tree seems to trigger
				// flooding of paint events.
				// tree.setRedraw(false);

				// Stop the sidetable from flickering
				sideTable.setVisible(false);

				// Calculate the current visible top and bottom indices in the
				// main tree.
				int itemHeight = tree.getItemHeight();
				Rectangle clientArea = tree.getClientArea();
				int offset = tree.getTopItem().getBounds().y;

				// Walk over the current visible articles and set/remove the
				// icons in the side tree
				int counter = 0;
				while (true) {

					// Get the item at the next visible location
					Point point = new Point(0, counter++ * itemHeight);
					TreeItem itemd = null;
					if (clientArea.height > point.y)
						itemd = tree.getItem(new Point(0, point.y + offset));
					if (itemd == null || itemd.getData() == null)
						return;
					IArticle article = (IArticle) ((ISalvoResource) itemd
							.getData()).getObject();

					// Make the side tree just as long as the (resized) main
					// tree
					sideTable.setItemCount(counter);

					// Set some icons if required
					sideTable.getItem(counter - 1).setImage(1,
							getImage(article, 1));

					sideTable.getItem(counter - 1).setImage(2,
							getImage(article, 2));
				}
			} finally {

				// int s = (tree.getVerticalBar().getSelection() / 2) * 2;
				// if (s == tree.getVerticalBar().getSelection()) {
				// sideTable.setItemCount(sideTable.getItemCount() + 1);
				// sideTable.getItem(sideTable.getItemCount() - 1).setText(1,
				// (sideTable.getItemCount() - 1) + "");
				// sideTable.showItem(sideTable.getItem(sideTable
				// .getItemCount() - 1));
				// }

				sideTable.layout();
				sideTable.pack(true);
				sideTable.setVisible(true);

				// tree.setVisible(true);
			}
		}

		/**
		 * Decorate this row.
		 * 
		 * @param article
		 * @param row
		 * @return
		 */
		private Image getImage(IArticle article, int row) {

			if (row == 1)
				if (!article.isReply() && article.isCommenting()
						|| article.isMine())
					return Activator.getDefault().getImageRegistry()
							.get("messages.gif");

			if (row == 2)
				if (article.isMarked())
					return Activator.getDefault().getImageRegistry()
							.get("follow.gif");

			return null;
		}
	}

	public ArticlePanel() {
	}

	private Tree sideTable;

	private TreeViewer treeViewer;

	public static final String ID = "org.eclipse.ecf.salvo.ui.articlepanel";

	private Tree tree;

	private INewsgroup savedNewsgroup;

	@Inject
	private ESelectionService selectionService;

	private MPart part;

	@Persist
	public void doSave(IProgressMonitor monitor) {
	}

	@PostConstruct
	public void createPartControl(Composite parent, EMenuService menuService,
			INewsgroup newsgroup, MPart part) {
		this.part = part;
		final GridLayout gridLayout = new GridLayout();
		gridLayout.horizontalSpacing = 0;
		gridLayout.verticalSpacing = 0;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		parent.setLayout(gridLayout);

		final ToolBar toolBar = new ToolBar(parent, SWT.NONE);

		final ToolItem newItemToolItem = new ToolItem(toolBar, SWT.PUSH);
		newItemToolItem.setText("New item");

		final ToolItem newItemToolItem_1 = new ToolItem(toolBar, SWT.PUSH);
		newItemToolItem_1.setText("New item");

		// composite_1.setSize(494, 174);

		TreeColumnLayout layout1 = new TreeColumnLayout();
		TreeColumnLayout layout2 = new TreeColumnLayout();

		final Composite tableComposite = new Composite(parent, SWT.NONE);
		tableComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				true));
		final GridLayout gridLayout_1 = new GridLayout();
		gridLayout_1.numColumns = 2;
		gridLayout_1.verticalSpacing = 0;
		gridLayout_1.marginWidth = 0;
		gridLayout_1.marginHeight = 0;
		gridLayout_1.horizontalSpacing = 0;
		tableComposite.setLayout(gridLayout_1);

		// do something with the result

		final Composite tree1Composite = new Composite(tableComposite, SWT.NONE);
		tree1Composite.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false,
				true));

		sideTable = new Tree(tree1Composite, SWT.NONE);
		sideTable.setLinesVisible(true);
		sideTable.setHeaderVisible(true);
		sideTable.addListener(SWT.MeasureItem,
				new ArticlePanelSideTableMeasureListener());

		final TreeColumn column1 = new TreeColumn(sideTable, SWT.NONE);
		layout1.setColumnData(column1, new ColumnWeightData(00));

		final TreeColumn column2 = new TreeColumn(sideTable, SWT.NONE);
		column2.setResizable(false);
		column2.setWidth(18);
		layout1.setColumnData(column2, new ColumnWeightData(50));

		final TreeColumn column3 = new TreeColumn(sideTable, SWT.NONE);
		column3.setResizable(false);
		column3.setWidth(18);
		layout1.setColumnData(column3, new ColumnWeightData(50));

		tree1Composite.setLayout(layout1);
		column1.setWidth(0);

		final Composite tree2Composite = new Composite(tableComposite, SWT.NONE);
		tree2Composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				true));
		final GridLayout gridLayout_3 = new GridLayout();
		gridLayout_3.marginWidth = 0;

		treeViewer = new TreeViewer(tree2Composite, SWT.VIRTUAL
				| SWT.FULL_SELECTION);
		tree = treeViewer.getTree();
		tree.setLinesVisible(true);
		treeViewer.setUseHashlookup(true);
		treeViewer.setLabelProvider(new ArticlePaneLabelProvider());
		treeViewer.setContentProvider(new ArticlePanelContentProvider());
		tree.setHeaderVisible(true);
		tree.addPaintListener(new ArticlePanelTreePaintListener());

		final TreeColumn newColumnTreeColumn_2 = new TreeColumn(tree, SWT.NONE);
		newColumnTreeColumn_2.setText("subject");
		layout2.setColumnData(newColumnTreeColumn_2, new ColumnWeightData(50));

		final TreeColumn newColumnTreeColumn_3 = new TreeColumn(tree, SWT.NONE);
		newColumnTreeColumn_3.setText("from");
		layout2.setColumnData(newColumnTreeColumn_3, new ColumnWeightData(25));
		newColumnTreeColumn_3.setMoveable(true);

		final TreeColumn newColumnTreeColumn_4 = new TreeColumn(tree, SWT.NONE);
		newColumnTreeColumn_4.setText("sent");
		layout2.setColumnData(newColumnTreeColumn_4, new ColumnPixelData(100));
		newColumnTreeColumn_4.setMoveable(true);

		final TreeColumn newColumnTreeColumn_5 = new TreeColumn(tree, SWT.NONE);
		newColumnTreeColumn_5.setText("size");
		layout2.setColumnData(newColumnTreeColumn_5, new ColumnPixelData(40));
		newColumnTreeColumn_5.setMoveable(true);

		tree2Composite.setLayout(layout2);

		selectionService.addPostSelectionListener(this);
		menuService.registerContextMenu(treeViewer,
				"org.eclipse.ecf.salvo.ui.articlepanel.popupmenu");

		treeViewer.addSelectionChangedListener(this);

		setInput(newsgroup);

	}

	@PreDestroy
	public void dispose(ESelectionService selectionService) {
		selectionService.removePostSelectionListener(this);
	}

	@Focus
	public void setFocus() {
		tree.setFocus();
	}

	private void setInput(INewsgroup group) {

		// TODO Spin of a Job that loads the articles in the newsgroup
		// new NewsgroupArticleLoaderJob(group).schedule();

		if (group != savedNewsgroup) {
			savedNewsgroup = group;
			part.setLabel(group.getNewsgroupName());
			treeViewer.getTree().clearAll(true);
			treeViewer.setInput(group);
		}
	}

	public void selectionChanged(IWorkbenchPart part, ISelection selection) {

	}

	public void updateArticle(ISalvoResource resource) {
		treeViewer.update(resource, null);
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		Object object = ((TreeSelection) event.getSelection())
				.getFirstElement();
		selectionService.setSelection(object);
	}

	@Override
	public void selectionChanged(MPart part, Object selection) {
		if (part == this.part) {
			return;
		}

		if (selection instanceof INewsgroup) {
			setInput((INewsgroup) selection);
		}
	}
}
