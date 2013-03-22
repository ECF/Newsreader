package org.eclipse.ecf.salvo.ui.treeviewer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.e4.ui.workbench.swt.modeling.EMenuService;
import org.eclipse.ecf.protocol.nntp.core.Debug;
import org.eclipse.ecf.protocol.nntp.core.ServerStoreFactory;
import org.eclipse.ecf.protocol.nntp.core.StoreStore;
import org.eclipse.ecf.protocol.nntp.model.INewsgroup;
import org.eclipse.ecf.protocol.nntp.model.IServer;
import org.eclipse.ecf.protocol.nntp.model.IStore;
import org.eclipse.ecf.protocol.nntp.model.IStoreEvent;
import org.eclipse.ecf.protocol.nntp.model.IStoreEventListener;
import org.eclipse.ecf.protocol.nntp.model.SALVO;
import org.eclipse.ecf.salvo.ui.internal.provider.NewsLabelProvider;
import org.eclipse.ecf.salvo.ui.internal.provider.SalvoDecorator;
import org.eclipse.ecf.salvo.ui.internal.provider.SubscribedServerProvider;
import org.eclipse.ecf.salvo.ui.internal.resources.ISalvoResource;
import org.eclipse.ecf.salvo.ui.internal.resources.SalvoResourceFactory;
import org.eclipse.jface.layout.TreeColumnLayout;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnWeightData;

public class ServerViewer implements ISelectionChangedListener,
		IStoreEventListener {
	private Tree tree;

	@Inject
	ESelectionService selectionService;

	private TreeViewer treeViewer;

	@Inject
	public ServerViewer() {
	}

	@PostConstruct
	public void postConstruct(Composite parent, EMenuService menuService) {

		Composite composite = new Composite(parent, SWT.NONE);
		TreeColumnLayout tcl_composite = new TreeColumnLayout();
		composite.setLayout(tcl_composite);

		treeViewer = new TreeViewer(composite, SWT.BORDER);
		tree = treeViewer.getTree();
		tree.setLinesVisible(true);

		TreeViewerColumn treeViewerColumn = new TreeViewerColumn(treeViewer,
				SWT.NONE);
		TreeColumn trclmnColumn = treeViewerColumn.getColumn();
		tcl_composite.setColumnData(trclmnColumn, new ColumnWeightData(1,
				ColumnWeightData.MINIMUM_WIDTH, true));
		trclmnColumn.setText("column");
		treeViewer.setLabelProvider(new NewsLabelProvider());
		treeViewer.setContentProvider(new TreeContentProvider());

		treeViewer.addSelectionChangedListener(this);
		treeViewer.setInput(getInitialInput());

		menuService.registerContextMenu(tree,
				"org.eclipse.ecf.salvo.ui.treeviewer.popupmenu");
	}

	protected IAdaptable getInitialInput() {
		StoreStore.instance().addListener(this, SALVO.EVENT_STORE);
		ISalvoResource root = SalvoResourceFactory.getResource("root", "root");
		root.setChildProvider(new SubscribedServerProvider());
		for (IStore store : ServerStoreFactory.instance()
				.getServerStoreFacade().getStores())
			store.addListener(this, SALVO.EVENT_ALL_EVENTS);

		return root;
	}

	@PreDestroy
	public void preDestroy() {
	}

	@Focus
	public void onFocus() {
		tree.setFocus();
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		Object object = ((TreeSelection) event.getSelection())
				.getFirstElement();
		selectionService.setSelection(object);
	}

	@Override
	public void storeEvent(final IStoreEvent event) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if (event.getEventObject() instanceof INewsgroup
						|| event.getEventObject() instanceof IServer
						|| event.getEventObject() instanceof IStore) {
					Debug.log(this.getClass(), "Event: " + event.getEventType());
					TreePath[] elements = treeViewer.getExpandedTreePaths();
					tree.setRedraw(false);
					treeViewer.refresh();
					treeViewer.setExpandedTreePaths(elements);
					tree.setRedraw(true);
				}
			}
		});
	}
}