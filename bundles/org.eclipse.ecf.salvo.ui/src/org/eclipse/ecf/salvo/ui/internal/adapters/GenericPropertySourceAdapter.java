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
package org.eclipse.ecf.salvo.ui.internal.adapters;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;

/**
 * This generic inspector shows the value of all getter methods in the passed
 * object.
 * 
 * @author Wim Jongman
 * 
 */
public class GenericPropertySourceAdapter implements IPropertySource {

	private Object object;

	private ArrayList<IPropertyDescriptor> descriptors;

	public GenericPropertySourceAdapter(Object object) {
		super();
		this.object = object;
	}

	public Object getEditableValue() {
		return null;
	}

	public IPropertyDescriptor[] getPropertyDescriptors() {

		if (object == null)
			return null;

		if (descriptors != null)
			return descriptors.toArray(new IPropertyDescriptor[0]);

		descriptors = new ArrayList<IPropertyDescriptor>();

		Method[] methods = object.getClass().getMethods();
		for (Method method : methods) {
			if (method.getParameterTypes().length == 0) {
				if (method.getName().startsWith("get")) {
					if (method.getReturnType() != null) {
						descriptors.add(new PropertyDescriptor(method, method.getName().replaceFirst("get",
								"")));
					}
				}
				if (method.getName().startsWith("is")) {
					if (method.getReturnType() != null) {
						descriptors.add(new PropertyDescriptor(method, method.getName().replaceFirst("is",
						"")));
					}
				}
			}
		}

		return descriptors.toArray(new IPropertyDescriptor[0]);
	}

	public Object getPropertyValue(Object id) {

		try {
			Object returnValue = ((Method) id).invoke(object, new Object[0]);

			if (returnValue instanceof Object[])
				return Arrays.toString((Object[]) returnValue);

			return returnValue == null ? "null" : returnValue.toString();
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	public boolean isPropertySet(Object id) {
		return false;
	}

	public void resetPropertyValue(Object id) {

	}

	public void setPropertyValue(Object id, Object value) {
	}
	
	public Object getObject() {
		return object;
	}
}
