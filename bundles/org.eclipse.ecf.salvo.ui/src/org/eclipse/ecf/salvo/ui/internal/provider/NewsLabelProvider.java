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
package org.eclipse.ecf.salvo.ui.internal.provider;

import java.util.HashSet;

import org.eclipse.ecf.protocol.nntp.core.Debug;
import org.eclipse.ecf.salvo.ui.internal.Activator;
import org.eclipse.ecf.salvo.ui.internal.resources.ISalvoResource;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.navigator.ICommonContentExtensionSite;
import org.eclipse.ui.navigator.ICommonLabelProvider;
import org.eclipse.ui.navigator.IDescriptionProvider;

public class NewsLabelProvider implements IDescriptionProvider,
		IStyledLabelProvider, ICommonLabelProvider {

	private final HashSet<Object> checkedItems;

	public NewsLabelProvider(HashSet<Object> checkedItems) {
		this.checkedItems = checkedItems;
	}

	public NewsLabelProvider() {
		checkedItems = null;
	}

	public Image getImage(Object element) {
		if(checkedItems != null && checkedItems.contains(element))
			return Activator.getDefault().getImageRegistry().get(
			"follow.gif");
		return null;
	}

	public StyledString getStyledText(Object element) {

		ISalvoResource res = (ISalvoResource) element;
		Debug.log(this.getClass(), res.getName());

		StyledString ss = new StyledString(res.getName(), StyledString.QUALIFIER_STYLER);

		return ss;
	}

	public void addListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

	public void dispose() {
		// TODO Auto-generated method stub

	}

	public boolean isLabelProperty(Object element, String property) {
		// TODO Auto-generated method stub
		return false;
	}

	public void removeListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

	public String getDescription(Object anElement) {
		return ((ISalvoResource) anElement).getName();
	}

	public void init(ICommonContentExtensionSite config) {
		// TODO Auto-generated method stub

	}

	public String getText(Object element) {
		return getDescription(element);
	}

	public void restoreState(IMemento memento) {
		// TODO Auto-generated method stub

	}

	public void saveState(IMemento memento) {
		// TODO Auto-generated method stub

	}

}
