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
package org.eclipse.ecf.salvo.ui.internal;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ecf.protocol.nntp.core.Debug;
import org.eclipse.ecf.protocol.nntp.model.IArticle;
import org.eclipse.ecf.salvo.ui.internal.adapters.ArticlePropertySourceAdapter;
import org.eclipse.ecf.salvo.ui.internal.adapters.GenericPropertySourceAdapter;
import org.eclipse.ecf.salvo.ui.internal.resources.ISalvoResource;
import org.eclipse.ui.views.properties.IPropertySource;


public class SalvoAdapterFactory implements IAdapterFactory {

	private Class<?>[] adapterList = new Class[] { IPropertySource.class };

	@SuppressWarnings("unchecked")
	public Object getAdapter(Object adaptableObject, Class adapterType) {

		if (adapterType == IPropertySource.class) {
			if (adaptableObject instanceof ISalvoResource) {
				Object object = ((ISalvoResource) adaptableObject).getObject();
				if (object instanceof IArticle) {
					return new ArticlePropertySourceAdapter(object);
				}
				return new GenericPropertySourceAdapter(object);

			} else
				return new GenericPropertySourceAdapter(adaptableObject);
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	public Class[] getAdapterList() {
		Debug.log(getClass(), "getAdapterList" + "(" + ")");
		return adapterList;
	}

}
