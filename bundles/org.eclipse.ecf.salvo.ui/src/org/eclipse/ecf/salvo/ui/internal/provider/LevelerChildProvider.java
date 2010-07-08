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
import java.util.Iterator;

import org.eclipse.ecf.protocol.nntp.model.INewsgroup;
import org.eclipse.ecf.salvo.ui.internal.resources.ISalvoResource;
import org.eclipse.ecf.salvo.ui.internal.resources.SalvoResourceFactory;


public class LevelerChildProvider implements IChildProvider {

	private final Leveler leveler;

	public LevelerChildProvider(Leveler leveler) {
		this.leveler = leveler;
	}

	public Collection<ISalvoResource> getChildren() {

		ArrayList<ISalvoResource> result = new ArrayList<ISalvoResource>();

		Collection<Object> intRes = leveler.getChildren();

		for (Iterator<Object> iterator = intRes.iterator(); iterator.hasNext();) {
			Object object = (Object) iterator.next();

			if (object instanceof Leveler) {
				Leveler leveler = (Leveler) object;
				String name = leveler.getLevelText() + ".*";
				ISalvoResource salvoResource = SalvoResourceFactory.getResource(name, leveler);
				salvoResource.setChildProvider(new LevelerChildProvider(leveler));
				result.add(salvoResource);
			}

			if (object instanceof INewsgroup) {
				INewsgroup group = (INewsgroup) object;
				String name = group.getNewsgroupName();
				ISalvoResource salvoResource = SalvoResourceFactory.getResource(name, group);
				result.add(salvoResource);
			}

		}

		return result;

	}

	public ISalvoResource getParent() {
		// TODO Auto-generated method stub
		return null;
	}

}
