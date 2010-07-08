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

import org.eclipse.ecf.protocol.nntp.core.ServerStoreFactory;
import org.eclipse.ecf.protocol.nntp.model.INewsgroup;
import org.eclipse.ecf.protocol.nntp.model.IServer;
import org.eclipse.ecf.protocol.nntp.model.StoreException;
import org.eclipse.ecf.salvo.ui.internal.resources.ISalvoResource;
import org.eclipse.ecf.salvo.ui.internal.resources.SalvoResourceFactory;

public class SubscribedNewsGroupProvider implements IChildProvider {

	private ISalvoResource parent;

	public SubscribedNewsGroupProvider(ISalvoResource parent) {
		this.parent = parent;
	}

	public Collection<ISalvoResource> getChildren() {

		ArrayList<ISalvoResource> result = new ArrayList<ISalvoResource>();

		if (parent.getObject() instanceof IServer) {

			IServer server = (IServer) parent.getObject();
			INewsgroup[] groups;
			try {
				groups = ServerStoreFactory.instance().getServerStoreFacade()
						.getSubscribedNewsgroups(server);
			} catch (StoreException e) {
				return result;
			}

			for (INewsgroup group : groups) {
				String name = group.getNewsgroupName();
				ISalvoResource salvoResource = SalvoResourceFactory
						.getResource(name, group);
				result.add(salvoResource);
			}
		}
		return result;
	}

	public ISalvoResource getParent() {
		return parent;
	}

}
