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
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.eclipse.ecf.protocol.nntp.core.Debug;
import org.eclipse.ecf.protocol.nntp.model.StoreException;

public class Database {

	private final Connection connection;

	public Database(Connection connection) throws StoreException {
		this(connection, false);
	}

	protected Database(Connection connection, boolean initialize)
			throws StoreException {
		this.connection = connection;
		if (initialize || !existDB()) {
			dropDB();
		}
		createDB();
	}

	public void createDB() throws StoreException {

		try {

			if (existDB())
				return;

			Statement s = connection.createStatement();

			String servers = "CREATE TABLE Server  " + "( "
					+ " url VARCHAR(256) NOT NULL, " + "port decimal,"
					+ "userName varchar(64) not null,"
					+ "email varchar(64) not null,"
					+ "login varchar(64) not null," + "secure char, "
					+ "lastVisit Date, subscribed char, "
					+ "primary key (url))";

			String newsgroups = "CREATE TABLE Newsgroup  "
					+ "( "
					+ "ID INTEGER NOT NULL  primary key GENERATED always as identity (start with 1, increment by 1) ,"
					+ " hostUrl VARCHAR(256) constraint server_fk references server on delete cascade on update restrict, "
					+ " groupName VARCHAR(64) NOT NULL, " 
					+ "subscribed char, "
					+ "description varchar(128)," 
					+ "lastUpdate Date, " 
					+ "lowWatermark int, " +
							"highWatermark int, " +
							"articleCount int)";

			String newsGroupIndex = "CREATE UNIQUE INDEX newsgroupl1 ON Newsgroup(hosturl,groupname)";

			String article = "CREATE TABLE Article  "
					+ "( "
					+ "ID INTEGER NOT NULL  primary key GENERATED always as identity (start with 1, increment by 1) ,"
					+ " messageID varchar(256),"
					+ " uri VARCHAR(256) constraint artbyuri UNIQUE,"
					+ " newsgroupID integer constraint newsgroup_fk references newsgroup on delete cascade on update restrict, "
					+ "articleNumber integer,  " + "isMarked char,"
					+ "isRead char" + ")";

			// String articleIndex =
			// "CREATE UNIQUE INDEX articlebyuri ON article (uri)";
			// String articleIndex2 =
			// "CREATE UNIQUE INDEX articlebyid ON Article (messageID)";
			String articleIndex3 = "CREATE UNIQUE INDEX Articlebynumber ON Article (newsgroupID, articleNumber desc)";

			String articleReply = "CREATE TABLE ArticleReply  "
					+ "( "
					+ " articleid int constraint article_fk_reply references article on delete cascade on update restrict,"
					+ " messageIDRepliedTo varchar(256))";

			String articleReplyIndex = "CREATE INDEX articleReplyByID ON articlereply (articleid)";
			String articleReplyIndex2 = "CREATE INDEX articleReplyByReply ON articlereply (messageIDRepliedTo)";

			String articleHeader = "CREATE TABLE ArticleHeader  "
					+ "( "
					+ "articleid INTEGER constraint article_fk references article on delete cascade on update restrict, "
					+ " attribute varchar(256)," + " value VARCHAR(1024),"
					+ "primary key (articleid, attribute))";

			String articleBody = "CREATE TABLE ArticleBody  "
					+ "( "
					+ "articleid INTEGER constraint article_fk2 references article on delete cascade on update restrict, "
					+ "body CLOB(256 K)," + "primary key (articleid))";

			s.execute(servers);
			s.execute(newsgroups);
			s.execute(newsGroupIndex);
			s.execute(article);
			// s.execute(articleIndex);
			// s.execute(articleIndex2);
			s.execute(articleIndex3);
			s.execute(articleReply);
			s.execute(articleReplyIndex);
			s.execute(articleReplyIndex2);
			s.execute(articleHeader);
			s.execute(articleBody);

		} catch (Exception e) {
			throw new StoreException("Error initializing Store", e);
		}
	}

	public void closeDB() throws StoreException {
		try {
			connection.close();
		} catch (SQLException e) {
			throw new StoreException(e.getMessage(), e);
		}
	}

	public boolean existDB() {

		try {
			Statement s = connection.createStatement();
			boolean execute = s.execute("select * from Server");
			return execute;
		} catch (Exception e) {
			return false;
		}
	}

	public void dropDB() throws StoreException {

		if (existDB()) {
			Statement s = null;
			boolean throwMe = false;
			Exception eMe = null;

			try {
				s = connection.createStatement();
			} catch (SQLException e) {
				throwMe = true;
				eMe = e;
			}

			try {
				s.execute("drop table articlereply");
			} catch (Exception e) {
				throwMe = true;
				eMe = e;
			}
			try {
				s.execute("drop table articlebody");
			} catch (Exception e) {
				throwMe = true;
				eMe = e;
			}
			try {
				s.execute("drop table articleheader");
			} catch (Exception e) {
				throwMe = true;
				eMe = e;
			}
			// try {
			// s.execute("drop index articlebyuri");
			// } catch (Exception e) {
			// throwMe = true;
			// eMe = e;
			// }
			// try {
			// s.execute("drop index articlebyid");
			// } catch (Exception e) {
			// throwMe = true;
			// eMe = e;
			// }
			try {
				s.execute("drop index Articlebynumber");
			} catch (Exception e) {
				throwMe = true;
				eMe = e;
			}
			try {
				s.execute("drop table article");
			} catch (Exception e) {
				throwMe = true;
				eMe = e;
			}
			try {
				s.execute("drop index newsgroupl1");
			} catch (Exception e) {
				throwMe = true;
				eMe = e;
			}
			try {
				s.execute("drop table newsgroup");
			} catch (Exception e) {
				throwMe = true;
				eMe = e;
			}
			try {
				s.execute("drop TABLE Server");
			} catch (Exception e) {
				throwMe = true;
				eMe = e;
			}

			if (throwMe)
				Debug.log(getClass(), eMe);
//				throw new StoreException(eMe.getMessage(), eMe);
		}
	}

	public static Database createDatabase(String root, boolean initialize)
			throws StoreException {
		System.setProperty("derby.system.home", root);
		String connectionURL = "jdbc:derby:Salvo;create=true";
		try {
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
			Connection connection = DriverManager.getConnection(connectionURL,
					"salvo", "salvo");

			return new Database(connection, initialize);
		} catch (Exception e) {
			throw new StoreException("Problem creating store", e);
		}
	}

	public Connection getConnection() {
		return connection;
	}
}
