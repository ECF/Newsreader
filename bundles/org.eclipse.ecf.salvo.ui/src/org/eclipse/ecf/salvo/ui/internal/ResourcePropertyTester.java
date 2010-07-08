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

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.ecf.protocol.nntp.core.Debug;
import org.eclipse.ecf.protocol.nntp.model.IArticle;
import org.eclipse.ecf.protocol.nntp.model.INewsgroup;
import org.eclipse.ecf.protocol.nntp.model.IServer;
import org.eclipse.ecf.salvo.ui.internal.resources.ISalvoResource;


public class ResourcePropertyTester extends PropertyTester {

	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {

		Debug.log(getClass(), property);

		if (property.equals("class") && receiver.getClass().getName().endsWith(expectedValue.toString()))
			return true;

		if (receiver instanceof ISalvoResource) {

			ISalvoResource resource = (ISalvoResource) receiver;

			if (property.equals("isNewsgroup") && resource.getObject() instanceof INewsgroup)
				return true;

			if (property.equals("isServer") && resource.getObject() instanceof IServer)
				return true;

			if (property.equals("isArticle") && resource.getObject() instanceof IArticle)
				return true;

			if (property.equals("isSubscribable")
					&& (resource.getObject() instanceof INewsgroup || resource.getObject() instanceof IServer))
				return true;

			if (property.equals("hasNewsgroup")
					&& (resource.getObject() instanceof INewsgroup || resource.getObject() instanceof IArticle))
				return true;
			
		}

		return false;
	}

}
