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
package org.eclipse.ecf.samples.nntp;

import org.eclipse.ecf.protocol.nntp.core.Debug;
import org.eclipse.ecf.protocol.nntp.core.NewsgroupFactory;
import org.eclipse.ecf.protocol.nntp.core.ServerFactory;
import org.eclipse.ecf.protocol.nntp.core.ServerStoreFactory;
import org.eclipse.ecf.protocol.nntp.core.StoreStore;
import org.eclipse.ecf.protocol.nntp.model.ICredentials;
import org.eclipse.ecf.protocol.nntp.model.INewsgroup;
import org.eclipse.ecf.protocol.nntp.model.IServer;
import org.eclipse.ecf.protocol.nntp.model.IServerStoreFacade;
import org.eclipse.ecf.protocol.nntp.model.IStore;
import org.eclipse.ecf.protocol.nntp.model.SALVO;
import org.eclipse.ecf.protocol.nntp.store.filesystem.StoreFactory;

/**
 * This snippet demonstrates how to subscribe a server and a group in a store.
 * First the store is created and the server is fetched from the store, if it
 * does not exist it is created and the subscribed in the store together with
 * the newsgroup.
 * <p>
 * Then then the articles are read two times with statistics information. Then
 * the store is removed emptied by unsubscribing and removing the server and
 * group.
 * </p>
 * 
 * @author Wim Jongman
 * 
 */
public class Snippet003 {

	// Provide credentials
	static ICredentials credentials = new ICredentials() {

		public String getUser() {
			return "Foo Bar";
		}

		public String getPassword() {
			return "flinder1f7";
		}

		public String getOrganization() {
			return "eclipse.org";
		}

		public String getLogin() {
			return "exquisitus";
		}

		public String getEmail() {
			return "foo.bar@eclipse.org";
		}
	};
	private static IServerStoreFacade serverStoreFacade;
	private static boolean firstTime = true;

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		Debug.debug = false;

		// Create a store in the store store.
		IStore store = new StoreFactory().createStore(SALVO.SALVO_HOME
				+ SALVO.SEPARATOR + "snippet003");
		StoreStore.instance().registerStore(store);

		// Get the interface between server and store
		serverStoreFacade = ServerStoreFactory.instance()
				.getServerStoreFacade();

		// Create a server
		IServer server = ServerFactory.getCreateServer("news.eclipse.org", 119,
				credentials, true);

		// Attach a newsgroup to the server
		INewsgroup group = NewsgroupFactory.createNewsGroup(server,
				"eclipse.technology.ecf", "Eclipse Test");
		server.getServerConnection().setWaterMarks(group);

		// Subscribe the server and the group
		serverStoreFacade.subscribeServer(server, credentials.getPassword());
		serverStoreFacade.subscribeNewsgroup(group);

		// Log and fetch
		long clock = System.currentTimeMillis();
		serverStoreFacade.getArticles(group, group.getLowWaterMark(), 200);
		clock = System.currentTimeMillis() - clock;
		if (firstTime)
			System.out.print("#1: Getting 200 messages took ");
		else
			System.out.print("#2: Getting 200 messages took ");
		System.out.println(clock + " milliseconds.");

		// And again
		if (firstTime) {
			firstTime = false;
			Snippet003.main(args);
			store.unsubscribeServer(server, true);
		}

	}

}
