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

import org.eclipse.ecf.protocol.nntp.model.INewsgroup;
import org.eclipse.ecf.protocol.nntp.model.IServer;
import org.eclipse.ecf.protocol.nntp.model.IServerStoreFacade;
import org.eclipse.ecf.protocol.nntp.model.IStore;
import org.eclipse.ecf.protocol.nntp.model.NNTPException;
import org.eclipse.ecf.protocol.nntp.model.StoreException;

public final class UpdateRunner implements Runnable {
	private boolean threadRunning;

	public void run() {

		setThreadRunning(true);

		IServerStoreFacade facade = ServerStoreFactory.instance()
				.getServerStoreFacade();
		while (facade.getStores().length == 0 && isThreadRunning()) {
			facade = ServerStoreFactory.instance().getServerStoreFacade();
			try {
				Debug.log(getClass(), "Salvo Thread: Waiting for Store");
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				setThreadRunning(false);
				break;
			}
		}

		while (isThreadRunning()) {
			IServer[] subscribedServers = new IServer[0];
			IStore[] stores = facade.getStores();
			for (int i = 0; i < stores.length; i++) {
				IStore store = stores[i];
				subscribedServers = getSubscribedServers(store);
				for (int j = 0; j < subscribedServers.length; j++) {
					IServer server = subscribedServers[j];
					INewsgroup[] subscribedNewsgroups;
					try {
						subscribedNewsgroups = facade
								.getSubscribedNewsgroups(server);
						for (int k = 0; k < subscribedNewsgroups.length; k++) {
							INewsgroup group = subscribedNewsgroups[k];
							try {
								facade.updateAttributes(group);
							} catch (Exception e) {
								Debug.log(this.getClass(), e);
							}
						}
					} catch (StoreException e1) {
						Debug.log(this.getClass(), e1);
					}
					server.setDirty(false);
				}
			}
			try {
				Debug.log(getClass(),
						"Salvo Thread: Update finished sleeping 600 seconds");
				for (int i = 0; i < 600; i++)
					if (isThreadRunning() && serversClean(subscribedServers))
						Thread.sleep(1000);
					else
						break;
			} catch (InterruptedException e) {
				setThreadRunning(false);
				break;
			}
		}
	}

	private IServer[] getSubscribedServers(IStore store) {
		try {
			return store.getServers();
		} catch (NNTPException e) {
			Debug.log(getClass(), e);
			return new IServer[0];
		}
	}

	private boolean serversClean(IServer[] subscribedServers) {

		for (int j = 0; j < subscribedServers.length; j++) {
			IServer server = subscribedServers[j];
			if (server.isDirty())
				return false;
		}
		return true;
	}

	private void setThreadRunning(boolean threadRunning) {
		this.threadRunning = threadRunning;
	}

	public boolean isThreadRunning() {
		return threadRunning;
	}

	public void stop() {
		if (isThreadRunning())
			setThreadRunning(false);
	}

	public void start() {
		if (!isThreadRunning())
			new Thread(this, "Salvo newsreader update thread").start();
	}
}