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

/**
 * This provides the Labels for Digest View tree
 * @author isuru
 * 
 * Plese note that this functionality is still under construction
 *
 */
import org.apache.james.mime4j.codec.DecoderUtil;
import org.eclipse.ecf.protocol.nntp.core.DateParser;
import org.eclipse.ecf.protocol.nntp.model.IArticle;
import org.eclipse.ecf.protocol.nntp.model.INewsgroup;
import org.eclipse.ecf.salvo.ui.internal.resources.ISalvoResource;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.graphics.Color;

class DigestViewTreeLabelProvider implements ITableLabelProvider, ITableColorProvider {

	private static Color newsgroupForgroundColor;
	private static Color newsgroupBackgroundColor;
	private static Color mineColor;

	static {
		newsgroupForgroundColor = Display.getDefault().getSystemColor(SWT.COLOR_DARK_BLUE);
		mineColor = Display.getDefault().getSystemColor(SWT.COLOR_MAGENTA);
		newsgroupBackgroundColor = Display.getDefault().getSystemColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT);
	}
	
	public void addListener(ILabelProviderListener arg0) {
	}

	public void dispose() {
	}

	public boolean isLabelProperty(Object arg0, String arg1) {
		return false;
	}

	public void removeListener(ILabelProviderListener arg0) {
	}

	public Image getColumnImage(Object arg0, int arg1) {
		return null;
	}

	/**
	 * Get Label for a item in a specific column
	 * 
	 * @param element
	 *            Tree item
	 * @param columnIndex
	 *            index of the column
	 * 
	 * @return text label for item
	 */
	public String getColumnText(Object element, int columnIndex) {

		if (element instanceof INewsgroup) {
			INewsgroup node = (INewsgroup) element;

			if (columnIndex == 0) {
				return node.getNewsgroupName();
			} else {
				return "";
			}

		} else if (element instanceof ISalvoResource
				&& ((ISalvoResource) element).getObject() instanceof IArticle) {

			IArticle node = (IArticle) ((ISalvoResource) element).getObject();

			if (columnIndex == 0) {
				return DecoderUtil.decodeEncodedWords(node.getSubject());
			} else {
				return DateParser.parseRFC822(node.getDate()).toString();
			}
		}
		return null;
	}

	public Color getBackground(Object element, int columnIndex) {
		
		if (element instanceof INewsgroup) {
			return newsgroupBackgroundColor;
		} 
		return null;
	}

	public Color getForeground(Object element, int columnIndex) {
		
		if (element instanceof INewsgroup) {

			return newsgroupForgroundColor;

		} else if (element instanceof ISalvoResource
				&& ((ISalvoResource) element).getObject() instanceof IArticle){
			
			IArticle article = (IArticle) ((ISalvoResource) element).getObject();
			
			if (article.isMine()) {
				return mineColor;
			}
		}
		return null;
	}

}