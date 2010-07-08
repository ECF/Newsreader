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

import org.eclipse.ecf.protocol.nntp.core.internal.Newsgroup;
import org.eclipse.ecf.protocol.nntp.core.internal.Server;
import org.eclipse.ecf.protocol.nntp.core.internal.ServerConnection;
import org.eclipse.ecf.protocol.nntp.model.ICredentials;
import org.eclipse.ecf.protocol.nntp.model.INewsgroup;
import org.eclipse.ecf.protocol.nntp.model.IServer;
import org.eclipse.ecf.protocol.nntp.model.IServerConnection;
import org.eclipse.ecf.protocol.nntp.model.NNTPException;

public class ServerFactory {

	private static HashMap servers = new HashMap();

	public static IServer getServer(String address, int port,
			ICredentials credentials, boolean secure) {

		return (IServer) servers.get(address + "::" + port + "::"
				+ credentials.getUser() + "::" + credentials.getEmail() + "::"
				+ credentials.getLogin() + "::" + secure);
	}

	public static IServer getCreateServer(String address, int port,
			ICredentials credentials, boolean secure) throws NNTPException {

		IServer server = (IServer) servers.get(address + "::" + port + "::"
				+ credentials.getUser() + "::" + credentials.getEmail() + "::"
				+ credentials.getLogin() + "::" + secure);

		if (server != null) {
			server.getServerConnection().setCredentials(credentials);
			server.setDirty(false);
			return server;
		}

		server = new Server(address, port, secure);
		IServerConnection connection = new ServerConnection(server);
		connection.setCredentials(credentials);
		if (servers.get(server.toString()) != null)
			return (IServer) servers.get(server.toString());

		try {
			server.init();
		} catch (Exception e) {
			// Swallow
		}

		servers.put(server.toString(), server);
		return server;
	}

	public static INewsgroup getGroup(IServer server, String newsGroup,
			String description) {
		return new Newsgroup(server, newsGroup, description);
	}

}
