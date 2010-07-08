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

/**
 * This class is used to deliver instances of {@link ISalvoResource} objects.
 * 
 * @author jongw
 * 
 */
public class SalvoResourceFactory {

	public static ISalvoResource getResource(String name, Object object) {
		return new SalvoUIResource(name, object);
	}
}
