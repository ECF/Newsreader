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

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.ecf.salvo.ui.internal.provider.IChildProvider;
import org.eclipse.ecf.salvo.ui.internal.provider.InfertileChildProvider;
import org.eclipse.ui.IActionFilter;


public abstract class AbstractSalvoResource extends PlatformObject implements IActionFilter, ISalvoResource {

	private String name;

	private Collection<ISalvoResource> children;

	private Object object;

	private IChildProvider childProvider;

	private ISalvoResource parent;

	public AbstractSalvoResource(String name, Object object) {
		this.object = object;
		this.setName(name);
	}

	public AbstractSalvoResource(String name) {
		this.setName(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ecf.salvo.ui.internal.resources.ISalvoResource#getChildren()
	 */
	public Collection<ISalvoResource> getChildren() {
		// if (children == null)
		children = getChildProvider().getChildren();
		return children;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ecf.salvo.ui.internal.resources.ISalvoResource#addChild(com
	 * .weltevree.salvo.ui.internal.resources.SalvoResource)
	 */
	public void addChild(ISalvoResource child) {
		if (children == null) {
			children = new ArrayList<ISalvoResource>();
		}
		children.add(child);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ecf.salvo.ui.internal.resources.ISalvoResource#setName(java
	 * .lang.String)
	 */
	public void setName(String name) {
		this.name = name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.salvo.ui.internal.resources.ISalvoResource#getName()
	 */
	public String getName() {
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.salvo.ui.internal.resources.ISalvoResource#getObject()
	 */
	public Object getObject() {
		return object;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ecf.salvo.ui.internal.resources.ISalvoResource#setChildProvider
	 * (org.eclipse.ecf.salvo.ui.internal.provider.IChildProvider)
	 */
	public void setChildProvider(IChildProvider childProvider) {
		this.childProvider = childProvider;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ecf.salvo.ui.internal.resources.ISalvoResource#getChildProvider
	 * ()
	 */
	public IChildProvider getChildProvider() {
		if (childProvider == null) {
			childProvider = new InfertileChildProvider();
		}
		return childProvider;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.salvo.ui.internal.resources.ISalvoResource#getParent()
	 */
	public ISalvoResource getParent() {
		return parent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ecf.salvo.ui.internal.resources.ISalvoResource#setParent(com
	 * .weltevree.salvo.ui.internal.resources.ISalvoResource)
	 */
	public void setParent(ISalvoResource parent) {
		this.parent = parent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ecf.salvo.ui.internal.resources.ISalvoResource#hasChildren()
	 */
	public boolean hasChildren() {
		// if (children == null)
		// return true;
		return getChildren().size() > 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object getAdapter(Class adapter) {
		if (getObject() != null && getObject().getClass() == adapter) {
			return getObject();
		}
		// TODO Auto-generated method stub
		return super.getAdapter(adapter);
	}

	public boolean testAttribute(Object target, String name, String value) {

		if (name.equals(IResourceActionFilter.CLASS))
			if (getClass().getName().equals(value))
				return true;

		return getName().equals(name);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ISalvoResource)
			return toString().equals(obj.toString());
		return false;
	}

	@Override
	public String toString() {
		if (getObject() == null)
			return getObject().toString();
		return getObject().getClass().getSimpleName() + "::" + getObject().toString();
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

}
