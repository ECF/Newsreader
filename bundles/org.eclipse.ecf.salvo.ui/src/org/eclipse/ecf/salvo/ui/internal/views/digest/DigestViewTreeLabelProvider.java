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
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

class DigestViewTreeLabelProvider implements ITableLabelProvider {
	

	public void addListener(ILabelProviderListener arg0) {
		// TODO Auto-generated method stub
		
	}

	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	public boolean isLabelProperty(Object arg0, String arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	public void removeListener(ILabelProviderListener arg0) {
		// TODO Auto-generated method stub
		
	}

	public Image getColumnImage(Object arg0, int arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		
		if (element instanceof INewsgroup) {
			INewsgroup node = (INewsgroup) element;
			
			if (columnIndex == 0) {
				return node.getNewsgroupName();
			} else {
				return "";
			}
			
		} else if (element instanceof IArticle){
		
			IArticle node = (IArticle) element;
			
			if (columnIndex == 0) {
				return DecoderUtil.decodeEncodedWords(node.getSubject());
			} else {
				return DateParser.parseRFC822(node.getDate()).toString();
			}
		}
		return null;
	}
	
}