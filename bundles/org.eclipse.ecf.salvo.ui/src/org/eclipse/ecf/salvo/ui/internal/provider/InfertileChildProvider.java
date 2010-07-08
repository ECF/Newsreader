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

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.ecf.salvo.ui.internal.resources.ISalvoResource;


public class InfertileChildProvider implements IChildProvider {

	public Collection<ISalvoResource> getChildren() {
		return new ArrayList<ISalvoResource>();
	}

	public ISalvoResource getParent() {
		return null;
	}

}
