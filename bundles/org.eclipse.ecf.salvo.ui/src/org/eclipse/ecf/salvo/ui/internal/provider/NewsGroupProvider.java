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
import org.eclipse.ecf.protocol.nntp.model.IServer;
import org.eclipse.ecf.salvo.ui.internal.resources.ISalvoResource;
import org.eclipse.ecf.salvo.ui.internal.resources.SalvoResourceFactory;
import org.eclipse.jface.dialogs.MessageDialog;


public class NewsGroupProvider implements IChildProvider {

	private ISalvoResource parent;

	public NewsGroupProvider(ISalvoResource parent) {
		this.parent = parent;
	}

	public Collection<ISalvoResource> getChildren() {

		ArrayList<ISalvoResource> result = new ArrayList<ISalvoResource>();

		if (parent.getObject() instanceof IServer) {

			IServer server = (IServer) parent.getObject();
			INewsgroup[] list;
			try {
				list = server.getServerConnection().getNewsgroups();
			} catch (Exception e) {
				MessageDialog.openError(null, "Error", e.getMessage());
				return null;
			}
			Leveler firstLeveler = new Leveler(server.getAddress());
			for (INewsgroup newsgroup : list) {
				firstLeveler.storeGroup(server, newsgroup);
			}

			Collection<Object> intRes = firstLeveler.getChildren(server);
			for (Iterator<Object> iterator = intRes.iterator(); iterator
					.hasNext();) {
				Object object = (Object) iterator.next();

				if (object instanceof Leveler) {
					Leveler leveler = (Leveler) object;
					String name = leveler.getLevelText() + ".*";
					ISalvoResource salvoResource = SalvoResourceFactory
							.getResource(name, leveler);
					salvoResource.setChildProvider(new LevelerChildProvider(
							leveler));
					result.add(salvoResource);
				}

				if (object instanceof INewsgroup) {
					INewsgroup group = (INewsgroup) object;
					String name = group.getNewsgroupName();
					ISalvoResource salvoResource = SalvoResourceFactory
							.getResource(name, group);
					result.add(salvoResource);
				}
			}
		}

		return result;
	}

	public ISalvoResource getParent() {
		return parent;
	}

}
