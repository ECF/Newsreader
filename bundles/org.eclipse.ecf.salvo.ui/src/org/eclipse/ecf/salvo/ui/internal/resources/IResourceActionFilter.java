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
package org.eclipse.ecf.salvo.ui.internal.resources;

import org.eclipse.ui.IActionFilter;

public interface IResourceActionFilter extends IActionFilter {

	/**
	 * Queries the class name of the embedded object. It compares the name of
	 * the class to the class.getName() method return value.
	 */
	public static final String CLASS = "class";

}
