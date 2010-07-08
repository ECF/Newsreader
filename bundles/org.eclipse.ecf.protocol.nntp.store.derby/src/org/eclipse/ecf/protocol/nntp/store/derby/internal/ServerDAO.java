/*******************************************************************************
 *  Copyright (c) 2010 Weltevree Beheer BV, Remain Software & Industrial-TSI
 *
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *     Wim Jongman - initial API and implementation
 *
 *
 *******************************************************************************/
package org.eclipse.ecf.protocol.nntp.store.derby.internal;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;

import org.eclipse.ecf.protocol.nntp.core.ServerFactory;
import org.eclipse.ecf.protocol.nntp.model.ICredentials;
import org.eclipse.ecf.protocol.nntp.model.IServer;
import org.eclipse.ecf.protocol.nntp.model.IStore;
import org.eclipse.ecf.protocol.nntp.model.NNTPException;
import org.eclipse.ecf.protocol.nntp.model.StoreException;

public class ServerDAO {

	private PreparedStatement getServer;
	private PreparedStatement insertServer;
	private PreparedStatement updateServer;
	private final Connection connection;
	private PreparedStatement deleteServer;
	private PreparedStatement getSubscribedServer;
	private final IStore store;

	public ServerDAO(Connection connection, IStore store) throws StoreException {
		this.connection = connection;
		this.store = store;
		prepareStatements();
	}

	private void prepareStatements() throws StoreException {

		try {
			getServer = connection
					.prepareStatement("select * from server where url like ?");

			getSubscribedServer = connection
					.prepareStatement("select * from server where subscribed =  ?");

			insertServer = connection
					.prepareStatement("insert into server (  url, port,"
							+ "userName,  "
							+ "email ,"
							+ "login ,"
							+ "secure , "
							+ "lastVisit, subscribed) values(?, ?, ?, ?, ?, ?, ?, ?)");

			updateServer = connection
					.prepareStatement("update server set url = ?, "
							+ "port = ?, "
							+ "userName = ?, "
							+ "email = ? ,"
							+ "login = ?,"
							+ "secure = ?, lastvisit = ?, subscribed = ? where url = ?");

			deleteServer = connection
					.prepareStatement("delete from server where url = ?");
		} catch (SQLException e) {
			throw new StoreException(e.getMessage(), e);
		}

	}

	public IServer[] getServer(String url) throws StoreException {
		try {
			getServer.setString(1, url);
			getServer.execute();
			ResultSet r = getServer.getResultSet();
			if (r == null)
				return new IServer[0];

			ArrayList result = new ArrayList();

			while (r.next()) {
				IServer server = ServerFactory.getCreateServer(getAddress(r),
						getPort(r), getCredentials(r), getSecure(r));
				server.setSubscribed(getSubscribed(r));
				result.add(server);

			}
			r.close();
			return (IServer[]) result.toArray(new IServer[0]);

		} catch (Exception e) {
			throw new StoreException(e.getMessage(), e);
		}
	}

	private boolean getSubscribed(ResultSet r) throws SQLException {
		return r.getString(8).equals("1");
	}

	private boolean getSecure(ResultSet r) throws SQLException {
		return r.getString(6).equals("1");
	}

	private ICredentials getCredentials(final ResultSet r) throws SQLException {

		final String user = r.getString(3);
		final String login = r.getString(5);
		final String email = r.getString(4);
		final String address = getAddress(r);

		return new ICredentials() {

			public String getUser() {
				return user;
			}

			public String getPassword() {
				return store.getSecureStore().get(address, "");
			}

			public String getOrganization() {
				return "weltevree";
			}

			public String getLogin() {
				return login;
			}

			public String getEmail() {
				return email;
			}
		};
	}

	private int getPort(ResultSet r) throws SQLException {
		return r.getInt(2);
	}

	private String getAddress(ResultSet r) throws SQLException {
		return r.getString(1).replace("nntp://", "").split(":")[0];
	}

	public void insertServer(IServer server) throws StoreException {

		try {
			deleteServer(server);
		} catch (Exception e1) {
			// swallow
		}

		try {
			insertServer.setString(1, server.getURL());
			insertServer.setInt(2, server.getPort());
			insertServer.setString(3, server.getServerConnection().getUser());
			insertServer.setString(4, server.getServerConnection().getEmail());
			insertServer.setString(5, server.getServerConnection().getLogin());
			insertServer.setString(6, server.isSecure() ? "1" : "0");
			insertServer.setDate(7, new Date(Calendar.getInstance()
					.getTimeInMillis()));
			insertServer.setString(8, server.isSubscribed() ? "1" : "0");
			insertServer.execute();

		} catch (SQLException e) {
			throw new StoreException(e.getMessage(), e);
		}
	}

	public void updateServer(IServer server) throws StoreException {
		try {
			updateServer.setString(1, server.getURL());
			updateServer.setInt(2, server.getPort());
			updateServer.setString(3, server.getServerConnection().getUser());
			updateServer.setString(4, server.getServerConnection().getEmail());
			updateServer.setString(5, server.getServerConnection().getLogin());
			updateServer.setString(6, server.isSecure() ? "1" : "0");
			updateServer.setDate(7, new Date(Calendar.getInstance()
					.getTimeInMillis()));
			updateServer.setString(8, server.isSubscribed() ? "1" : "0");
			updateServer.setString(9, server.getURL());
			updateServer.execute();

		} catch (SQLException e) {
			throw new StoreException(e.getMessage(), e);
		}
	}

	public void deleteServer(IServer server) throws StoreException {
		try {
			deleteServer.setString(1, server.getURL());
			deleteServer.execute();
		} catch (SQLException e) {
			throw new StoreException(e.getMessage(), e);
		}
	}

	public IServer[] getServers(boolean subscribed) throws NNTPException {

		synchronized (connection) {
			try {
				getSubscribedServer.setString(1, subscribed ? "1" : "0");
				getSubscribedServer.execute();
				ResultSet r = getSubscribedServer.getResultSet();
				if (r == null)
					return new IServer[0];

				ArrayList result = new ArrayList();

				while (r.next()) {
					IServer server = ServerFactory.getCreateServer(
							getAddress(r), getPort(r), getCredentials(r),
							getSecure(r));
					server.setSubscribed(true);
					result.add(server);
				}
				r.close();
				return (IServer[]) result.toArray(new IServer[0]);

			} catch (SQLException e) {
				throw new StoreException(e.getMessage(), e);
			}
		}
	}
}
