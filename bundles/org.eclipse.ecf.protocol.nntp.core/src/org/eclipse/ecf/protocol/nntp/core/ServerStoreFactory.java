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
package org.eclipse.ecf.protocol.nntp.core;

import org.eclipse.ecf.protocol.nntp.core.internal.ServerStoreFacade;
import org.eclipse.ecf.protocol.nntp.model.IServerStoreFacade;


public class ServerStoreFactory {

	private static ServerStoreFactory factory;

	private IServerStoreFacade facade;

	public IServerStoreFacade getServerStoreFacade() {
		
		if (facade == null) {
			facade = new ServerStoreFacade();
			facade.init();
		}
		return facade;
	}

	public static ServerStoreFactory instance() {
		if (factory == null)
			factory = new ServerStoreFactory();
		return factory;
	}

}
