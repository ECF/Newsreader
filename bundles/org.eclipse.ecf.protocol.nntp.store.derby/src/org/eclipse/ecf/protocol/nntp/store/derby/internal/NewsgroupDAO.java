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
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;

import org.eclipse.ecf.protocol.nntp.core.NewsgroupFactory;
import org.eclipse.ecf.protocol.nntp.model.INewsgroup;
import org.eclipse.ecf.protocol.nntp.model.IServer;
import org.eclipse.ecf.protocol.nntp.model.StoreException;

public class NewsgroupDAO {

	private PreparedStatement getNewsgroup;
	private PreparedStatement insertNewsgroup;
	private PreparedStatement updateNewsgroup;
	private final Connection connection;
	private PreparedStatement deleteNewsgroup;
	private PreparedStatement getNewsgroupByID;
	private PreparedStatement getSubscribedNewsgroup;

	public NewsgroupDAO(Connection connection) throws StoreException {
		this.connection = connection;
		prepareStatements();
	}

	private void prepareStatements() throws StoreException {

		try {
			getNewsgroup = connection
					.prepareStatement("select * from newsgroup where hostUrl like ? and groupname like ?");

			getNewsgroupByID = connection
					.prepareStatement("select * from newsgroup where id = ?");

			getSubscribedNewsgroup = connection
					.prepareStatement("select * from newsgroup where hosturl = ? and subscribed = ?");

			insertNewsgroup = connection
					.prepareStatement(
							"insert into newsgroup (  hosturl , groupname, subscribed,"
									+ "description, lastUpdate, lowWatermark, highWatermark, articleCount) values(?, ?, ?, ?, ?, ?, ?, ?)",
							Statement.RETURN_GENERATED_KEYS);

			updateNewsgroup = connection
					.prepareStatement("update newsgroup set HOSTurl = ?, "
							+ "groupname = ?, "
							+ "subscribed = ?, "
							+ "description = ? ,"
							+ "lastupdate = ?, lowWatermark = ?, highWatermark = ?, articleCount = ?"
							+ " where id = ?");

			deleteNewsgroup = connection
					.prepareStatement("delete from newsgroup where hosturl = ? and groupname = ?");
		} catch (SQLException e) {
			throw new StoreException(e.getMessage(), e);
		}
	}

	public INewsgroup[] getNewsgroup(IServer server, String groupName)
			throws StoreException {
		try {
			getNewsgroup.setString(1, server.getURL());
			getNewsgroup.setString(2, groupName);
			getNewsgroup.execute();
			ResultSet r = getNewsgroup.getResultSet();
			if (r == null)
				return new INewsgroup[0];
			ArrayList result = new ArrayList();
			addGroups(server, r, result);
			return (INewsgroup[]) result.toArray(new INewsgroup[0]);

		} catch (Exception e) {
			throw new StoreException(e.getMessage(), e);
		}
	}

	private void addGroups(IServer server, ResultSet r, ArrayList result)
			throws SQLException {
		while (r.next()) {
			INewsgroup group = NewsgroupFactory.createNewsGroup(server,
					getGroupName(r), getDescription(r));
			group.setProperty("DB_ID", getID(r) + "");
			group.setSubscribed(getSubscribed(r));
			group.setAttributes(getArticleCount(r), getLowWatermark(r),
					getHighWatermark(r));
			result.add(group);
		}
		r.close();

	}

	public INewsgroup[] getNewsgroup(IServer server, int id)
			throws StoreException {
		try {
			getNewsgroupByID.setInt(1, id);
			getNewsgroupByID.execute();
			ResultSet r = getNewsgroupByID.getResultSet();
			if (r == null)
				return new INewsgroup[0];

			ArrayList result = new ArrayList();
			addGroups(server, r, result);
			return (INewsgroup[]) result.toArray(new INewsgroup[0]);

		} catch (Exception e) {
			throw new StoreException(e.getMessage(), e);
		}
	}

	public INewsgroup[] getSubscribedNewsgroups(IServer server,
			boolean subscribed) throws StoreException {
		try {
			getSubscribedNewsgroup.setString(1, server.getURL());
			getSubscribedNewsgroup.setString(2, subscribed ? "1" : "0");
			getSubscribedNewsgroup.execute();
			ResultSet r = getSubscribedNewsgroup.getResultSet();
			if (r == null)
				return new INewsgroup[0];

			ArrayList result = new ArrayList();
			addGroups(server, r, result);
			return (INewsgroup[]) result.toArray(new INewsgroup[0]);

		} catch (Exception e) {
			throw new StoreException(e.getMessage(), e);
		}
	}

	private boolean getSubscribed(ResultSet r) throws SQLException {
		return r.getString(4).equals("1");
	}

	private String getDescription(ResultSet r) throws SQLException {
		return r.getString(5);
	}

	private int getID(ResultSet r) throws SQLException {
		return r.getInt(1);
	}

	private String getGroupName(ResultSet r) throws SQLException {
		return r.getString(3);
	}

	private int getLowWatermark(ResultSet r) throws SQLException {
		return r.getInt(7);
	}

	private int getHighWatermark(ResultSet r) throws SQLException {
		return r.getInt(8);
	}

	private int getArticleCount(ResultSet r) throws SQLException {
		return r.getInt(9);
	}

	public void insertNewsgroup(INewsgroup newsgroup) throws StoreException {
		try {
			insertNewsgroup.setString(1, newsgroup.getServer().getURL());
			insertNewsgroup.setString(2, newsgroup.getNewsgroupName());
			insertNewsgroup.setString(3, newsgroup.isSubscribed() ? "1" : "0");
			insertNewsgroup.setString(4, newsgroup.getDescription());
			insertNewsgroup.setDate(5, new Date(Calendar.getInstance()
					.getTimeInMillis()));
			insertNewsgroup.setInt(6, newsgroup.getLowWaterMark());
			insertNewsgroup.setInt(7, newsgroup.getHighWaterMark());
			insertNewsgroup.setInt(8, newsgroup.getArticleCount());

			insertNewsgroup.execute();
			ResultSet result = insertNewsgroup.getGeneratedKeys();
			if (result.next())
				newsgroup.setProperty("DB_ID", result.getInt(1) + "");

		} catch (SQLException e) {
			throw new StoreException(e.getMessage(), e);
		}
	}

	public void updateNewsgroup(INewsgroup newsgroup) throws StoreException {
		try {
			updateNewsgroup.setString(1, newsgroup.getServer().getURL());
			updateNewsgroup.setString(2, newsgroup.getNewsgroupName());
			updateNewsgroup.setString(3, newsgroup.isSubscribed() ? "1" : "0");
			updateNewsgroup.setString(4, newsgroup.getDescription());
			updateNewsgroup.setDate(5, new Date(Calendar.getInstance()
					.getTimeInMillis()));
			updateNewsgroup.setInt(6, newsgroup.getLowWaterMark());
			updateNewsgroup.setInt(7, newsgroup.getHighWaterMark());
			updateNewsgroup.setInt(8, newsgroup.getArticleCount());
			updateNewsgroup.setInt(9, Integer.parseInt(newsgroup
					.getProperty("DB_ID")));
			updateNewsgroup.execute();

		} catch (SQLException e) {
			throw new StoreException(e.getMessage(), e);
		}
	}

	public void deleteNewsgroup(INewsgroup newsgroup) throws StoreException {
		try {
			deleteNewsgroup.setString(1, newsgroup.getServer().getURL());
			deleteNewsgroup.setString(2, newsgroup.getNewsgroupName());
			deleteNewsgroup.execute();
		} catch (SQLException e) {
			throw new StoreException(e.getMessage(), e);
		}
	}

	public INewsgroup getNewsgroup(INewsgroup group) throws StoreException {
		INewsgroup[] result = getNewsgroup(group.getServer(), group
				.getNewsgroupName());
		if (result.length == 1) {
			return result[0];
		}
		return null;
	}
}
