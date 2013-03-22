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
package org.eclipse.ecf.salvo.ui.tools;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;

public class SelectionUtil {

	public static Object getFirstObjectFromSelection(ISelection selection, Class<?> clazz) {

		if (selection instanceof IStructuredSelection) {
			if (clazz.isInstance(((IStructuredSelection) selection).getFirstElement())) {
				return ((IStructuredSelection) selection).getFirstElement();
			}
		}
		return null;
	}

	public static Object getFirstObjectFromCurrentSelection(Class<?> clazz) {
		return getFirstObjectFromSelection(getSelection(), clazz);
	}

	public static ISelection getSelection() {
	//	return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService().getSelection();
		return null;
	}

	public static IStructuredSelection getStructuredSelection() {
//		ISelection sel = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService()
//				.getSelection();
//		if (sel instanceof IStructuredSelection) {
//			return (IStructuredSelection) sel;
//		}
		return StructuredSelection.EMPTY;
	}

	public static ITreeSelection getTreeSelection() {
//		ISelection sel = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService()
//				.getSelection();
//		if (sel instanceof ITreeSelection) {
//			return (ITreeSelection) sel;
//		}
		return TreeSelection.EMPTY;
	}

}
