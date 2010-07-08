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

import java.util.HashMap;

import org.eclipse.ecf.protocol.nntp.model.IStore;
import org.eclipse.ecf.protocol.nntp.model.SALVO;
import org.eclipse.ecf.protocol.nntp.model.StoreEvent;

public class StoreStore {

	private static StoreStore factory;

	private HashMap stores = new HashMap();

	public void registerStore(IStore store) {
		stores.put(store.getDescription(), store);
		store.fireEvent(new StoreEvent(store, SALVO.EVENT_REGISTER_STORE));
	}

	public void unregisterStore(IStore store) {
		stores.remove(store.getDescription());
		store.fireEvent(new StoreEvent(store, SALVO.EVENT_UNREGISTER_STORE));
	}

	public IStore[] getStores() {
		return (IStore[]) stores.values().toArray(new IStore[0]);
	}

	public static StoreStore instance() {
		if (factory == null)
			factory = new StoreStore();
		return factory;
	}
}
