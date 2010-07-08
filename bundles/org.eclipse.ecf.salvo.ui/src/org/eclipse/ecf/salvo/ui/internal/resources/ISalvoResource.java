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

import java.util.Collection;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ecf.salvo.ui.internal.provider.IChildProvider;


public interface ISalvoResource extends IAdaptable {

	public abstract Collection<ISalvoResource> getChildren();

	public abstract void addChild(ISalvoResource child);

	public abstract void setName(String name);

	public abstract String getName();

	public abstract Object getObject();

	public abstract void setChildProvider(IChildProvider childProvider);

	public abstract IChildProvider getChildProvider();

	public abstract ISalvoResource getParent();

	public abstract void setParent(ISalvoResource parent);

	public abstract boolean hasChildren();

}