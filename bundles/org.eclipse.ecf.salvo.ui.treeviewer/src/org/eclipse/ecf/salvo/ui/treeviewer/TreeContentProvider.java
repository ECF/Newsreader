package org.eclipse.ecf.salvo.ui.treeviewer;

import org.eclipse.ecf.salvo.ui.internal.resources.ISalvoResource;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

class TreeContentProvider implements ITreeContentProvider {
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	public void dispose() {
	}

	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof ISalvoResource) {
			return ((ISalvoResource) parentElement).getChildren().toArray();
		}
		return new Object[0];
	}

	public Object getParent(Object element) {
		if (element instanceof ISalvoResource) {
			return ((ISalvoResource) element).getParent();
		}

		return null;
	}

	public boolean hasChildren(Object element) {
		return getChildren(element).length > 0;
	}
}