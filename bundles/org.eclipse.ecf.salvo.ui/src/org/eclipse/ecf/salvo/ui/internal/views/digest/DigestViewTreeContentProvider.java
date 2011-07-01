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

import org.eclipse.jface.viewers.ILazyTreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ecf.protocol.nntp.core.Debug;
/**
 * This provides the content for Digest View tree
 * @author isuru
 * 
 * Plese note that this functionality is still under construction
 *
 */
class DigestViewTreeContentProvider implements ILazyTreeContentProvider {

	public void dispose() {
		Debug.log(this.getClass(), "Not implemented yet");
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		Debug.log(this.getClass(), "Not implemented yet");
	}

	public Object getParent(Object element) {
		Debug.log(this.getClass(), "Not implemented yet");
		return null;
	}

	public void updateChildCount(Object element, int currentChildCount) {
		Debug.log(this.getClass(), "Not implemented yet");
	}

	public void updateElement(Object parent, int index) {
		Debug.log(this.getClass(), "Not implemented yet");
	}
	
}