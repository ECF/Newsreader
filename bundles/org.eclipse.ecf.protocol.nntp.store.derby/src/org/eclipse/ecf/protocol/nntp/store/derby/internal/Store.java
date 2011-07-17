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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.ecf.protocol.nntp.core.DateParser;
import org.eclipse.ecf.protocol.nntp.core.Debug;
import org.eclipse.ecf.protocol.nntp.core.StringUtils;
import org.eclipse.ecf.protocol.nntp.model.IArticle;
import org.eclipse.ecf.protocol.nntp.model.INewsgroup;
import org.eclipse.ecf.protocol.nntp.model.ISecureStore;
import org.eclipse.ecf.protocol.nntp.model.IServer;
import org.eclipse.ecf.protocol.nntp.model.IStore;
import org.eclipse.ecf.protocol.nntp.model.IStoreEvent;
import org.eclipse.ecf.protocol.nntp.model.IStoreEventListener;
import org.eclipse.ecf.protocol.nntp.model.NNTPConnectException;
import org.eclipse.ecf.protocol.nntp.model.NNTPException;
import org.eclipse.ecf.protocol.nntp.model.NNTPIOException;
import org.eclipse.ecf.protocol.nntp.model.SALVO;
import org.eclipse.ecf.protocol.nntp.model.StoreEvent;
import org.eclipse.ecf.protocol.nntp.model.StoreException;

/**
 * This implementation of IStore uses the Java file system to store the
 * newsgroup data.
 * 
 * @author Wim Jongman
 * 
 */
public class Store implements IStore {

	/**
	 * Stores the last exception.
	 */
	private HashMap listeners;

	private String root = "";

	private ISecureStore secureStore;

	private Database database;

	private ServerDAO serverDOA;

	private NewsgroupDAO groupDOA;

	private ArticleDAO articleDOA;

	public Store(String root) throws StoreException {
		if (!root.endsWith(SALVO.SEPARATOR))
			this.root = root + SALVO.SEPARATOR;
		boolean init = System
				.getProperty("org.eclipse.ecf.protocol.nntp.store.derby.init") != null;
		initDB(this.root, init);

	}

	/**
	 * Initializes the database.
	 * 
	 * @param root
	 *            the physical location of the database
	 * @param initialize
	 *            is true if you want to drop all existing data
	 * @throws StoreException
	 */
	public void initDB(String root, boolean initialize) throws StoreException {

		try {
			setDatabase(Database.createDatabase(root, initialize));
			serverDOA = new ServerDAO(getDatabase().getConnection(), this);
			groupDOA = new NewsgroupDAO(getDatabase().getConnection());
			articleDOA = new ArticleDAO(getDatabase().getConnection());
		} catch (Exception e) {
			throw new StoreException("Error occured intializing store ", e);
		}
	}

	/**
	 * Deletes the database.
	 * 
	 * @throws StoreException
	 */
	public void dropDB() throws StoreException {

		try {
			getDatabase().closeDB();
			getDatabase().dropDB();
		} catch (Exception e) {
			throw new StoreException("Error occured dropping store ", e);
		}
	}

	public void subscribeServer(final IServer server, final String passWord)
			throws StoreException {
		getSecureStore().put(server.getAddress(), passWord, true);
		server.setSubscribed(true);
		serverDOA.insertServer(server);
		fireEvent(new StoreEvent(server, SALVO.EVENT_ADD_SERVER));
	}

	public ISecureStore getSecureStore() {
		if (secureStore == null) {
			secureStore = new ISecureStore() {

				HashMap memory = new HashMap();

				public void remove(String key) {
					memory.remove(key);
				}

				public void put(String key, String value, boolean encrypt) {
					memory.put(key, value);
				}

				public String get(String key, String def) {
					return (String) (memory.get(key) == null ? def : memory
							.get(key));
				}

				public void clear() {
					memory.clear();
				}
			};
		}
		return secureStore;
	}

	// public Exception getLastException() {
	// return lastException;
	// }

	public void addListener(IStoreEventListener listener, int eventType) {

		if (listeners == null) {
			listeners = new HashMap();
		}

		synchronized (listeners) {

			ArrayList list = (ArrayList) listeners.get(new Integer(eventType));
			if (list == null) {
				list = new ArrayList();
				listeners.put(new Integer(eventType), list);
			}
			if (!list.contains(listener))
				list.add(listener);
		}
	}

	private void fireEvent(IStoreEvent event) {

		if (listeners == null || listeners.isEmpty())
			return;

		synchronized (listeners) {
			Set keys = listeners.keySet();
			for (Iterator iterator = keys.iterator(); iterator.hasNext();) {
				Integer key = (Integer) iterator.next();
				if ((event.getEventType() | key.intValue()) == key.intValue()) {
					Debug.log(getClass(), "Listeners found for event type "
							+ key.intValue() + " (" + event.getEventType()
							+ "): ");

					ArrayList list = (ArrayList) listeners.get(key);
					for (Iterator iterator2 = list.iterator(); iterator2
							.hasNext();) {
						IStoreEventListener listener = (IStoreEventListener) iterator2
								.next();
						Debug.log(getClass(), "Calling Listener "
								+ listener.getClass().getName());
						listener.storeEvent(event);
					}
				}
			}
		}
	}

	public void removeListener(IStoreEventListener listener) {

		if (listeners == null || listeners.isEmpty())
			return;

		Set keySet = listeners.keySet();
		for (Iterator iterator = keySet.iterator(); iterator.hasNext();) {
			Integer key = (Integer) iterator.next();
			ArrayList list = (ArrayList) listeners.get(key);
			if (list.contains(listener)) {
				list.remove(listener);
				if (list.isEmpty()) {
					listeners.remove(key);
				}
			}
		}
	}

	public void unsubscribeNewsgroup(INewsgroup group, boolean permanent)
			throws StoreException {
		if (permanent) {
			groupDOA.deleteNewsgroup(group);
			fireEvent(new StoreEvent(group, SALVO.EVENT_REMOVE_GROUP));
		} else {
			group.setSubscribed(false);
			groupDOA.updateNewsgroup(group);
			fireEvent(new StoreEvent(group, SALVO.EVENT_UNSUBSCRIBE_GROUP));
		}
	}

	public void unsubscribeServer(IServer server, boolean permanent)
			throws StoreException {
		if (permanent) {
			serverDOA.deleteServer(server);
			fireEvent(new StoreEvent(server, SALVO.EVENT_REMOVE_SERVER));
		} else {
			server.setSubscribed(false);
			serverDOA.updateServer(server);
			fireEvent(new StoreEvent(server, SALVO.EVENT_UNSUBSCRIBE_SERVER));
		}
	}

	public void subscribeNewsgroup(INewsgroup group) throws StoreException {

		INewsgroup oldGroup = groupDOA.getNewsgroup(group);
		group.setSubscribed(true);
		if (oldGroup == null) {
			groupDOA.insertNewsgroup(group);
			fireEvent(new StoreEvent(group, SALVO.EVENT_ADD_GROUP));
			fireEvent(new StoreEvent(group, SALVO.EVENT_SUBSCRIBE_GROUP));
		} else {
			group.setProperty("DB_ID", oldGroup.getProperty("DB_ID"));
			groupDOA.updateNewsgroup(group);
			fireEvent(new StoreEvent(group, SALVO.EVENT_SUBSCRIBE_GROUP));
		}
	}

	public INewsgroup[] getSubscribedNewsgroups(IServer server)
			throws StoreException {
		return groupDOA.getSubscribedNewsgroups(server, true);
	}

	public void updateAttributes(INewsgroup newsgroup) throws StoreException {
		Debug.log(getClass(), "Updating attributes of group " + newsgroup);
		groupDOA.updateNewsgroup(newsgroup);
		fireEvent(new StoreEvent(newsgroup, SALVO.EVENT_CHANGE_GROUP));
	}

	public IServer[] getServers() throws NNTPException {
		IServer[] subsc = serverDOA.getServers(true);
		IServer[] unsubsc = serverDOA.getServers(false);
		IServer[] result = new IServer[subsc.length + unsubsc.length];
		int counter = 0;
		for (int i = 0; i < subsc.length; i++) {
			result[counter++] = subsc[i];

		}
		for (int i = 0; i < unsubsc.length; i++) {
			result[counter++] = unsubsc[i];
		}
		return result;
	}

	public IArticle[] getArticles(INewsgroup newsgroup, int from, int to)
			throws StoreException {
		if (getLastArticle(newsgroup) == null
				|| getLastArticle(newsgroup).getArticleNumber() < to)
			return null;
		return articleDOA.getArticles(newsgroup, from, to);
	}

	/**
	 * Gets the article with this number from the newsgroup store.
	 * 
	 * @param newsgroup
	 * @param number
	 * @return the article or null if it does not exist
	 * @throws StoreException
	 */
	public IArticle getArticle(INewsgroup newsgroup, int number)
			throws StoreException {

		IArticle[] article = articleDOA.getArticles(newsgroup, number, number);
		if (article.length == 1)
			return article[0];
		else
			return null;
	}

	/**
	 * Does what storeArticles should do. This was internalized so that it can
	 * also be called by update methods.
	 * 
	 * @param articles
	 * @return false if not success else true
	 * @throws StoreException
	 */
	private void internalStoreArticles(Collection articles)
			throws StoreException {

		long t = System.currentTimeMillis();
		for (Iterator iterator = articles.iterator(); iterator.hasNext();) {
			IArticle article = (IArticle) iterator.next();

			if (!articleDOA.hasArticle(article)) {
				articleDOA.insertArticle(article);
			}
		}
		System.out.println((System.currentTimeMillis() - t) / 1000);
	}

	public IArticle getFirstArticle(INewsgroup newsgroup) throws StoreException {
		return articleDOA.getOldestArticle(newsgroup);
	}

	public IArticle getLastArticle(INewsgroup newsgroup) throws StoreException {
		return articleDOA.getNewestArticle(newsgroup);
	}

	public String[] getArticleBody(IArticle article) throws StoreException {
		return articleDOA.getArticleBody(article);
	}

	public void storeArticleBody(IArticle article, String[] body)
			throws StoreException {
		articleDOA.deleteArticleBody(article);
		articleDOA.insertArticleBody(article, body);
	}

	public IArticle[] getFollowUps(IArticle article) throws StoreException {

		return articleDOA.getFollowUps(article);

	}

	public void updateArticle(IArticle article) throws StoreException {
		articleDOA.updateArticle(article);
		fireEvent(new StoreEvent(article, SALVO.EVENT_CHANGE_GROUP));
	}

	public String getDescription() {
		return "Derby Storage";
	}

	public void storeArticles(IArticle[] articles) throws StoreException {
		internalStoreArticles(Arrays.asList(articles));
		fireEvent(new StoreEvent(articles, SALVO.EVENT_CHANGE_GROUP));
	}

	public void setWaterMarks(INewsgroup newsgroup) throws NNTPIOException,
			NNTPConnectException, StoreException {
		groupDOA.updateNewsgroup(newsgroup);
		fireEvent(new StoreEvent(newsgroup, SALVO.EVENT_CHANGE_GROUP));

	}

	public void setSecureStore(ISecureStore secureStore) {
		this.secureStore = secureStore;
	}

	public IArticle getArticle(String URL) throws NNTPException {
		String groupName = StringUtils.split(
				StringUtils.split(URL, "&article")[0], "=")[1];
		String serverURL = StringUtils.split(URL, "/?group")[0];
		IServer server = serverDOA.getServer(serverURL)[0];
		INewsgroup group = groupDOA.getNewsgroup(server, groupName)[0];
		return articleDOA.getArticle(group, URL);

	}

	public int purge(Calendar date, int number) throws NNTPIOException {

		int result = 0;

		try {
			IServer[] servers = getServers();
			for (int i = 0; i < servers.length; i++) {
				INewsgroup[] groups = getSubscribedNewsgroups(servers[i]);

				for (int j = 0; j < groups.length; j++) {
					IArticle candidate = getFirstArticle(groups[j]);

					while (candidate != null) {
						Date postDate = DateParser.parseDate(candidate
								.getDate());

						if (postDate == null) {
							result += delete(candidate);
						}

						else if (postDate.before(date.getTime())) {
							result += delete(candidate);
						}

						if (number > 0 && result == number)
							return result;
						candidate = getFirstArticle(groups[j]);
					}
				}
			}
		} catch (NNTPException e) {
			throw new NNTPIOException("Error during purge", e);
		}

		return result;
	}

	public int delete(IArticle article) throws NNTPIOException {
		try {
			int result = 0;
			IArticle[] followUps = getFollowUps(article);
			for (int i = 0; i < followUps.length; i++) {
				IArticle followUp = followUps[i];
				result = result + delete(followUp);
			}
			articleDOA.deleteArticle(article);
			fireEvent(new StoreEvent(article, SALVO.EVENT_REMOVE_ARTICLE));
			return ++result;
		} catch (StoreException e) {
			throw new NNTPIOException(e.getMessage(), e);
		}
	}

	private void setDatabase(Database database) {
		this.database = database;
	}

	public Database getDatabase() {
		return database;
	}

	@Override
	public int getListenerCount() {
		if (listeners == null)
			return 0;
		return listeners.size();
	}

	/**
	 * Get articles of a particular user
	 * 
	 * @param newsgroup
	 *            Newsgroup
	 * @param userId
	 *            Full username
	 * @return articles of a particular user in the particular Newsgroup
	 * 
	 */
	public IArticle[] getArticlesByUserId(INewsgroup newsgroup, String userId) {

		ArrayList<IArticle> result = new ArrayList<IArticle>();

		try {
			Integer[] articleIds = articleDOA.getArticleIdsFromUser(userId);

			for (int i : articleIds) {
				IArticle article = articleDOA.getArticleById(newsgroup, i);
				if (article != null) {
					result.add(article);
				}
			}
			return (IArticle[]) result.toArray(new IArticle[0]);

		} catch (StoreException e) {
			Debug.log(getClass(), e);
		}
		return null;
	}

	/**
	 * 
	 * Get marked articles for a particular newsgroup
	 * 
	 * @param newsgroup
	 *            Newsgroup
	 * @return marked articles for a particular newsgroup
	 */
	public IArticle[] getMarkedArticles(INewsgroup newsgroup) {

		try {
			return articleDOA.getMarkedArticles(newsgroup);
		} catch (StoreException e) {
			Debug.log(getClass(), e);
		}
		return null;

	}

	/**
	 * 
	 * Get all marked articles
	 * 
	 * @return marked articles for all newsgroups
	 */
	public IArticle[] getAllMarkedArticles(IServer server) {

		try {
			INewsgroup[] newsgroups = getSubscribedNewsgroups(server);

			ArrayList<IArticle> result = new ArrayList<IArticle>();

			for (INewsgroup newsgroup : newsgroups) {
				IArticle[] markedArticlesForANewsgroup = articleDOA
						.getMarkedArticles(newsgroup);

				for (IArticle article : markedArticlesForANewsgroup) {
					result.add(article);
				}

			}
			return (IArticle[]) result.toArray(new IArticle[0]);

		} catch (StoreException e) {
			Debug.log(getClass(), e);
		}
		return null;
	}

	/**
	 * Get article from messageId
	 * 
	 * @param newsgroup
	 *            Newsgroup
	 * @param msgId message Id of article
	 * @return article which has the particular message id
	 * 
	 */
	public IArticle getArticleByMsgId(INewsgroup newsgroup, String msgId) {

		try {
			return articleDOA.getArticleByMsgId(newsgroup, msgId);
		} catch (StoreException e) {
			Debug.log(getClass(), e);
		}
		return null;
		
	}
	
}
