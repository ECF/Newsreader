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

import org.eclipse.ecf.protocol.nntp.model.IArticle;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;


public class ArticlePropertySourceAdapter extends GenericPropertySourceAdapter {

	public ArticlePropertySourceAdapter(Object object) {
		super(object);
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {

		ArrayList<IPropertyDescriptor> result = new ArrayList<IPropertyDescriptor>(Arrays.asList(super
				.getPropertyDescriptors()));
		IArticle article = (IArticle) getObject();
		String[] headers = article.getHeaderAttributes();
		for (String header : headers) {
			result.add(new PropertyDescriptor(header, header));

		}
		return result.toArray(new IPropertyDescriptor[0]);
	}

	@Override
	public Object getPropertyValue(Object id) {
		if (id instanceof Method) {
			return super.getPropertyValue(id);
		}
		return ((IArticle) getObject()).getHeaderAttributeValue(id.toString());
	}
}
