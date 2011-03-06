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
package org.eclipse.ecf.protocol.nntp.store.filesystem.internal;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.ecf.protocol.nntp.core.ArticleFactory;
import org.eclipse.ecf.protocol.nntp.core.DateParser;
import org.eclipse.ecf.protocol.nntp.core.Debug;
import org.eclipse.ecf.protocol.nntp.core.NewsgroupFactory;
import org.eclipse.ecf.protocol.nntp.core.ServerFactory;
import org.eclipse.ecf.protocol.nntp.core.StringUtils;
import org.eclipse.ecf.protocol.nntp.model.AbstractCredentials;
import org.eclipse.ecf.protocol.nntp.model.IArticle;
import org.eclipse.ecf.protocol.nntp.model.ICredentials;
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
import org.eclipse.ecf.protocol.nntp.model.UnexpectedResponseException;

/**
 * This implementation of IStore uses the Java file system to store the
 * newsgroup data.
 * 
 * @author Wim Jongman
 * 
 */
public class Store implements IStore {

	// private static final String SALVO.SEPARATOR =
	// System.getProperty("file.separator");

	private static final String ID_DIR = SALVO.SEPARATOR + "ids";

	private static final String NUMBERS_DIR = SALVO.SEPARATOR + "numbers";

	/**
	 * Stores the last exception.
	 */
	private Exception lastException;

	private HashMap listeners;

	private HashMap storedServers;

	private HashMap subscribedGroups = new HashMap();

	private HashMap lastArticles = new HashMap();

	private HashMap firstArticles = new HashMap();

	private String root = "";

	private ISecureStore secureStore;

	public Store(String root) {
		if (!root.endsWith(SALVO.SEPARATOR))
			this.root = root + SALVO.SEPARATOR;
		this.root = root + SALVO.SEPARATOR;
	}

	private ArrayList getServersFromDisk() {

		ArrayList result = new ArrayList();

		File file = new File(getStoreHome() + "servers.txt");
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				Debug.log(getClass(), e);
				lastException = e;
				return result;
			}
		}

		FileReader reader = null;
		try {
			reader = new FileReader(file);
			StringBuffer lines = new StringBuffer();
			while (reader.ready()) {
				lines.append((char) reader.read());
			}

			StringTokenizer tizer = new StringTokenizer(lines.toString(), "\n");

			while (tizer.hasMoreTokens()) {
				String line = tizer.nextToken();
				if (!line.trim().startsWith("#")) {
					result.add(line);
				}
			}

		} catch (Exception e) {
			Debug.log(getClass(), e);
			lastException = e;
			return result;
		}

		finally {

			if (reader != null)
				try {
					reader.close();
				} catch (IOException e) {
					lastException = e;
					return null;
				}
		}

		return result;
	}

	private void loadSubscribedGroups(IServer server) {

		File file = new File(getStoreHome() + server.getAddress()
				+ SALVO.SEPARATOR + "groups.txt");
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				lastException = e;
			}
		}

		FileReader reader = null;
		try {
			reader = new FileReader(file);
			StringBuffer lines = new StringBuffer();
			while (reader.ready()) {
				lines.append((char) reader.read());
			}

			StringTokenizer tizer = new StringTokenizer(lines.toString(), "\n");

			while (tizer.hasMoreTokens()) {
				INewsgroup group = NewsgroupFactory.createNewsGroup(server,
						tizer.nextToken(), "");
				getSubscribedNewsgroups(server);
				((ArrayList) subscribedGroups.get(server.getAddress()))
						.add(group);
				Debug.log(this.getClass(), "Adding group subscription: "
						+ group.toString());
			}

		} catch (Exception e) {
			Debug.log(this.getClass(), e.getMessage());
			lastException = e;
		}

		finally {

			if (reader != null)
				try {
					reader.close();
				} catch (IOException e) {
					lastException = e;
				}
		}
	}

	/**
	 * Writes the subscribed groups in the groups.txt file in the server home
	 * directory. If the server directory does not exist it is created.
	 * 
	 * @param server
	 * @throws StoreException
	 */
	private void writeSubscribedGroups(IServer server) throws StoreException {

		// Create the server in this database and create a reference to the
		// newsgroups in groups.txt
		File file = new File(getServerHome(server) + SALVO.SEPARATOR
				+ "groups.txt");
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				Debug.log(getClass(), e);
				throw new StoreException(e.getMessage(), e);
			}
		}

		// Write the name of the newsgroup into the file, create a directory
		// for the newsgroup and create the group info file.
		try {
			FileWriter writer = new FileWriter(file);
			INewsgroup[] subscribedNewsgroups = getSubscribedNewsgroups(server);
			for (int i = 0; i < subscribedNewsgroups.length; i++) {
				INewsgroup group = subscribedNewsgroups[i];
				writer.write(group.getNewsgroupName() + "\n");
			}
			writer.close();

		} catch (Exception e) {
			Debug.log(getClass(), e);
			lastException = e;
		}
	}

	private void writeSubscribedServers() {

		File home = new File(getStoreHome());
		if (!home.exists())
			home.mkdirs();

		File file = new File(getStoreHome() + "servers.txt");
		if (file.exists())
			file.delete();

		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				Debug.log(getClass(), e);
				lastException = e;
			}
		}

		try {
			FileWriter writer = new FileWriter(file);
			Collection servers = storedServers.values();
			for (Iterator iterator = servers.iterator(); iterator.hasNext();) {
				IServer server = (IServer) iterator.next();
				writer.write(server.toString() + "\n");
			}
			writer.close();

		} catch (Exception e) {
			Debug.log(getClass(), e);
			lastException = e;
		}

	}

	public void subscribeServer(final IServer server, final String passWord) {
		server.setSubscribed(true);
		getServers();
		getSecureStore().put(server.getAddress(), passWord, true);
		storedServers.put(server.getAddress(), server);
		writeSubscribedServers();
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

	public Exception getLastException() {
		return lastException;
	}

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

	/**
	 * Returns the home directory of this server and creates it if it does not
	 * exist.
	 * 
	 * @param server
	 * @return
	 */
	private String getServerHome(IServer server) {

		File file = new File(getStoreHome() + server.getAddress());
		if (!file.exists())
			file.mkdirs();
		return file.getAbsolutePath();

	}

	/**
	 * Returns the home directory of this store.
	 * 
	 * @param server
	 * @return
	 */
	private String getStoreHome() {
		return (root);
	}

	/**
	 * Returns the home directory of this newsgroup and creates it if it does
	 * not exist.
	 * 
	 * @param server
	 * @return
	 */
	private String getNewsgroupHome(INewsgroup newsgroup) {

		String home = getServerHome(newsgroup.getServer()) + SALVO.SEPARATOR
				+ newsgroup.getNewsgroupName();

		File file = new File(home);
		if (!file.exists()) {
			file.mkdirs();
		}

		File numberDir = new File(home + NUMBERS_DIR);
		if (!numberDir.exists()) {
			numberDir.mkdirs();
		}

		File idDir = new File(home + ID_DIR);
		if (!idDir.exists()) {
			idDir.mkdirs();
		}

		return file.getAbsolutePath();

	}

	public void unsubscribeNewsgroup(INewsgroup group, boolean permanent)
			throws StoreException {

		group.setSubscribed(false);
		unsubscribeNewsgroup(group);
		writeSubscribedGroups(group.getServer());

		// remove newsgroup directory from disk
		if (permanent) {
			File file = new File(getNewsgroupHome(group));
			if (file.exists()) {
				if (file.isDirectory()) {
					clearDirectory(file);
					file.delete();
				}
			}
		}

		fireEvent(new StoreEvent(group, SALVO.EVENT_REMOVE_GROUP));
	}

	private void clearDirectory(File file) {
		File[] files = file.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				clearDirectory(files[i]);
			}
			if (!files[i].delete())
				files[i].deleteOnExit();
		}
	}

	public void unsubscribeServer(IServer server, boolean permanent)
			throws StoreException {
		// Disconnect first
		server.setSubscribed(false);
		try {
			server.getServerConnection().disconnect();
		} catch (NNTPConnectException e1) {
			Debug.log(getClass(), e1);
		}

		// Unsubscribe all groups
		if (permanent) {

			INewsgroup[] subscribedNewsgroups = getSubscribedNewsgroups(server);
			for (int i = 0; i < subscribedNewsgroups.length; i++) {
				INewsgroup group = subscribedNewsgroups[i];
				unsubscribeNewsgroup(group, permanent);
				fireEvent(new StoreEvent(group, SALVO.EVENT_REMOVE_GROUP));
			}

			// remove server directory from disk
			File file = new File(getServerHome(server));
			if (file.exists())
				file.delete();

			// remove this server from the preferences
			getSecureStore().remove(server.getAddress());

			// Remove the server from the list
			storedServers.remove(server.getAddress());
		}

		// Remove it from the servers file
		writeSubscribedServers();

		// Notify
		fireEvent(new StoreEvent(server, SALVO.EVENT_REMOVE_SERVER));

	}

	public void subscribeNewsgroup(INewsgroup group) throws StoreException {

		group.setSubscribed(true);

		getSubscribedNewsgroups(group.getServer());

		if (((ArrayList) subscribedGroups.get(group.getServer().getAddress()))
				.contains(group)) {
			Debug.log(this.getClass(), "Group " + group + " already added");
		}

		((ArrayList) subscribedGroups.get(group.getServer().getAddress()))
				.add(group);
		writeSubscribedGroups(group.getServer());
		fireEvent(new StoreEvent(group, SALVO.EVENT_ADD_GROUP));
		Debug.log(this.getClass(), "Group " + group + " added.");
	}

	private void unsubscribeNewsgroup(INewsgroup group) {
		if (subscribedGroups.get(group.getServer().getAddress()) != null)
			((ArrayList) subscribedGroups.get(group.getServer().getAddress()))
					.remove(group);
	}

	public INewsgroup[] getSubscribedNewsgroups(IServer server) {

		if (subscribedGroups.get(server.getAddress()) == null)
			subscribedGroups.put(server.getAddress(), new ArrayList());
		return (INewsgroup[]) ((ArrayList) subscribedGroups.get(server
				.getAddress())).toArray(new INewsgroup[0]);
	}

	public void updateAttributes(INewsgroup newsgroup) {

		Debug.log(getClass(), "Updating attributes of group " + newsgroup);

		try {

			// Get the old newsgroup from disk
			INewsgroup oldGroup = null;
			File serialized = new File(getNewsgroupHome(newsgroup)
					+ SALVO.SEPARATOR + "Newsgroup.data");
			oldGroup = NewsgroupFactory.createNewsGroup(serialized);

			// Write back if changed
			if (oldGroup == null
					|| (newsgroup.getLastChangeDate().getTimeInMillis() != oldGroup
							.getLastChangeDate().getTimeInMillis())) {
				FileOutputStream f_out = new FileOutputStream(
						getNewsgroupHome(newsgroup) + SALVO.SEPARATOR
								+ "Newsgroup.data");
				ObjectOutputStream obj_out = new ObjectOutputStream(f_out);
				obj_out.writeObject(newsgroup);
				obj_out.close();

				// Send store event
				fireEvent(new StoreEvent(newsgroup, SALVO.EVENT_CHANGE_GROUP));
			}
		} catch (Exception e) {
			Debug.log(getClass(), e);
			lastException = e;
		}

	}

	public IServer[] getServers() {
		if (storedServers != null && !storedServers.isEmpty())
			return (IServer[]) storedServers.values().toArray(new IServer[0]);

		storedServers = new HashMap();

		ArrayList serversOnDisk = getServersFromDisk();

		for (Iterator iterator = serversOnDisk.iterator(); iterator.hasNext();) {

			String line = (String) iterator.next();
			StringTokenizer tizer = new StringTokenizer(line, "::");
			String address = tizer.nextToken();
			int port = Integer.valueOf(tizer.nextToken()).intValue();
			String user = tizer.nextToken();
			String email = tizer.nextToken();
			String logIn = tizer.nextToken();
			boolean secure = Boolean.getBoolean(tizer.nextToken());
			boolean subscribed = Boolean.getBoolean(tizer.nextToken());

			// Do we already have a server initialized?
			ICredentials credentials = new AbstractCredentials(user, email,
					logIn, null);
			IServer server = null;
			server = ServerFactory
					.getServer(address, port, credentials, secure);


			// If not then we must get a password from the secure store
			if (server == null) {
				String pass = getSecureStore().get(address, "");
				if (pass == null)
					return (IServer[]) storedServers.values().toArray(
							new IServer[0]);
				credentials = new AbstractCredentials(user, email, logIn, pass);
				try {
					server = ServerFactory.getCreateServer(address, port,
							credentials, secure);
				} catch (NNTPException e) {
					Debug.log(getClass(), e);
				}
			}

			server.setSubscribed(subscribed);
			loadSubscribedGroups(server);
			storedServers.put(server.getAddress(), server);
			fireEvent(new StoreEvent(server, SALVO.EVENT_RELOAD));
		}

		return (IServer[]) storedServers.values().toArray(new IServer[0]);
	}

	public IArticle[] getArticles(INewsgroup newsgroup, int from, int to) {

		// If this range is not in store then return null
		IArticle article = getArticle(newsgroup, to);
		if (article == null)
			return null;

		article = getArticle(newsgroup, from);
		if (article == null)
			return null;

		// Read all files
		File numbersDir = new File(getNumbersHome(newsgroup));
		if (!numbersDir.exists()) {
			numbersDir.mkdir();
			return null;
		}

		String fromString = formatNumber(from);
		String toString = formatNumber(to);

		ArrayList files = new ArrayList(to - from);
		File[] numFiles = numbersDir.listFiles();
		for (int i = 0; i < numFiles.length; i++) {

			File file = numFiles[i];
			if (file.getName().compareTo(fromString) >= 0
					&& file.getName().compareTo(toString) <= 0) {
				files.add(file.getName());
			}
		}

		String[] sortedFiles = (String[]) files
				.toArray(new String[files.size()]);
		Arrays.sort(sortedFiles, new Comparator() {
			public int compare(Object arg0, Object arg1) {
				return ((String) arg0).compareTo(arg1) * -1;
			}
		});

		// Fill an article array
		int timer = Debug.timerStart(this.getClass());
		ArrayList result = new ArrayList();
		for (int i = 0; i < sortedFiles.length; i++) {

			String file = sortedFiles[i];
			result.add(getArticle(newsgroup, Integer.parseInt(file)));
		}
		Debug.timerStop(getClass(), timer);

		return (IArticle[]) result.toArray(new IArticle[0]);

	}

	/**
	 * Returns the home directory of the numbers dir.
	 * 
	 * @param newsgroup
	 * @return
	 */
	private String getNumbersHome(INewsgroup newsgroup) {
		return getNewsgroupHome(newsgroup) + NUMBERS_DIR;
	}

	/**
	 * Returns the home directory of the id dir.
	 * 
	 * @param newsgroup
	 * @return
	 */
	private String getIdsHome(INewsgroup newsgroup) {
		return getNewsgroupHome(newsgroup) + ID_DIR;
	}

	/**
	 * Gets the article with this number from the newsgroup store.
	 * 
	 * @param newsgroup
	 * @param number
	 * @return the article or null if it does not exist
	 */
	public IArticle getArticle(INewsgroup newsgroup, int number) {

		File file = new File(getArticleFilenameByNumber(newsgroup, number));
		if (!file.exists())
			return null;
		return getArticle(newsgroup, file);
	}

	/**
	 * Returns the name of the file that contains the article information.
	 * 
	 * @param newsgroup
	 * @param number
	 *            the article number
	 * @return
	 */
	private String getArticleFilenameByNumber(INewsgroup newsgroup, int number) {
		return getNumbersHome(newsgroup) + SALVO.SEPARATOR
				+ formatNumber(number);
	}

	/**
	 * Returns the name of the file that contains the article information.
	 * 
	 * @param newsgroup
	 * @param id
	 *            the article ID
	 * @return
	 */
	private String getArticleFilenameById(INewsgroup newsgroup, String id) {
		String id2 = id.replace('<', ' ');
		id2 = id2.replace('>', ' ');
		return getIdsHome(newsgroup) + SALVO.SEPARATOR + id2.trim();
	}

	/**
	 * Does what storeArticles should do. This was internalized so that it can
	 * also be called by update methods.
	 * 
	 * @param articles
	 * @return false if not success else true
	 */
	private boolean internalStoreArticles(Collection articles) {
		FileWriter fileWriter = null;
		File file = null;

		try {

			for (Iterator iterator = articles.iterator(); iterator.hasNext();) {
				IArticle article = (IArticle) iterator.next();

				String articleFilename = getArticleFilename(article);
				file = new File(articleFilename);

				// Delete an existing file
				if (file.exists())
					file.delete();

				// Serialize this article object
				FileOutputStream f_out = new FileOutputStream(file);
				ObjectOutputStream obj_out = new ObjectOutputStream(f_out);
				obj_out.writeObject(article);
				obj_out.close();

				// Replace the complete filename with the last part of the
				// filename
				articleFilename = file.getName();

				// Create the number file for this article. It contains the name
				// of the article file.
				file = new File(getArticleFilenameByNumber(
						article.getNewsgroup(), article.getArticleNumber()));
				if (file.exists())
					file.delete();
				fileWriter = new FileWriter(file);
				fileWriter.write(articleFilename);
				fileWriter.close();

				// Create the id file for this article. It contains the name
				// of the article file.
				file = new File(getArticleFilenameById(article.getNewsgroup(),
						article.getMessageId()));
				if (file.exists())
					file.delete();
				fileWriter = new FileWriter(file);
				fileWriter.write(articleFilename);
				fileWriter.close();

				// Create a folder for the body and the follow up articles.
				file = new File(getArticleDirectory(article.getNewsgroup(),
						article.getMessageId()));
				if (!file.exists())
					file.mkdirs();

				// If this article is a follow up, store it in the main article
				String reference = article.getLastReference();
				if (reference != null) {
					// Create a folder for the body and the follow up articles
					// if it does not yet exist.
					file = new File(getArticleDirectory(article.getNewsgroup(),
							reference));
					if (!file.exists())
						file.mkdirs();

					// Serialize this article object
					f_out = new FileOutputStream(getFollowUpFilename(article,
							reference));
					obj_out = new ObjectOutputStream(f_out);
					obj_out.writeObject(article);
					obj_out.close();
				}

				// Set as first or last if needed
				setFirstOrLastArticle(article);

			}
		} catch (Exception e) {
			Debug.log(getClass(), e);
			lastException = e;
			return false;
		}

		finally {

			try {
				if (fileWriter != null)
					fileWriter.close();
			} catch (IOException e) {
				Debug.log(getClass(), e);
				lastException = e;
			}
		}
		return true;
	}

	/**
	 * Returns the name of the directory where the article content is stored.
	 * 
	 * @param newsgroup
	 * @param messageId
	 * @return
	 */
	private String getArticleDirectory(INewsgroup newsgroup, String messageId) {
		String id2 = messageId.replace('<', ' ');
		id2 = id2.replace('>', ' ');
		return getNewsgroupHome(newsgroup) + SALVO.SEPARATOR + id2.trim();
	}

	/**
	 * Returns the name of the followup article file name given the article and
	 * the parent
	 * 
	 * @param article
	 * @param parentId
	 * @return
	 */
	private String getFollowUpFilename(IArticle article, String parentId) {
		String id2 = parentId.replace('<', ' ');
		id2 = id2.replace('>', ' ');

		String id3 = article.getMessageId().replace('<', ' ');
		id3 = id3.replace('>', ' ');

		return getNewsgroupHome(article.getNewsgroup()) + SALVO.SEPARATOR
				+ id2.trim() + SALVO.SEPARATOR + id3.trim() + ".data";
	}

	/**
	 * Possibly replace the first or/and last articles in the store with the
	 * passed article.
	 * 
	 * @param article
	 */
	private void setFirstOrLastArticle(IArticle article) {

		IArticle oldArticle = (IArticle) firstArticles.get(article
				.getNewsgroup().getURL());
		if (oldArticle == null
				|| oldArticle.getArticleNumber() > article.getArticleNumber()) {
			firstArticles.put(article.getNewsgroup().getURL(), article);
		}

		oldArticle = (IArticle) lastArticles.get(article.getNewsgroup()
				.getURL());
		if (oldArticle == null
				|| oldArticle.getArticleNumber() < article.getArticleNumber()) {
			lastArticles.put(article.getNewsgroup().getURL(), article);
		}

	}

	/**
	 * 
	 * Gets the filename used to store the article.
	 * 
	 * <pre>
	 * nnnnnn.A.&lt;I&gt;.data 
	 * nnnnnn = Article number in newsgroup 
	 * A = A new post and B is follow up
	 * </pre>
	 * 
	 * If the article is a follow up then it is written again like this
	 * 
	 * <pre>
	 * R.nnnnnn.oooooo.data 
	 * R=R 
	 * nnnnnn = number of article that is the original post 
	 * oooooo = nummber of article that is the reply
	 * </pre>
	 * 
	 * @param article
	 * @return the filename
	 */
	private String getArticleFilename(IArticle article) {

		String[] refs = article.getReferences();
		StringBuffer fileName = new StringBuffer();
		fileName.append(getNewsgroupHome(article.getNewsgroup())
				+ SALVO.SEPARATOR);
		fileName.append(formatNumber(article.getArticleNumber()));
		if (refs.length == 0)
			fileName.append(".A.");
		else
			fileName.append(".B.");
		String id = article.getMessageId().replace('<', ' ');
		id = id.replace('>', ' ');
		fileName.append(id.trim());
		fileName.append(".data");

		return fileName.toString();
	}

	/**
	 * Pads until we get six digit string.
	 * 
	 * @param number
	 * @return
	 */
	private String formatNumber(int number) {

		if (number < 10)
			return "00000" + number;
		if (number < 100)
			return "0000" + number;
		if (number < 1000)
			return "000" + number;
		if (number < 10000)
			return "00" + number;
		if (number < 100000)
			return "0" + number;
		return "" + number;
	}

	public IArticle getFirstArticle(INewsgroup newsgroup) {

		if (firstArticles.get(newsgroup.getURL()) != null) {
			return (IArticle) firstArticles.get(newsgroup.getURL());
		}

		File infoDir = new File(getNumbersHome(newsgroup));
		if (!infoDir.exists()) {
			infoDir.mkdir();
			return null;
		}

		File first = new File("999999");
		File[] listFiles = infoDir.listFiles();
		for (int i = 0; i < listFiles.length; i++) {

			File file = listFiles[i];
			if (file.getName().compareTo(first.getName()) < 0) {
				first = file;
			}
		}

		if (first.getName().equals("999999")) {
			return null;
		}

		IArticle firstArticle = getArticle(newsgroup, first);
		firstArticles.put(newsgroup.getURL(), firstArticle);
		return firstArticle;
	}

	/**
	 * Returns the article found in the infoFile file.
	 * 
	 * @param infoFile
	 * @return
	 */
	private IArticle getArticle(INewsgroup newsgroup, File infoFile) {
		FileReader reader = null;
		try {
			reader = new FileReader(infoFile);
			int size = Integer.parseInt(infoFile.length() + "");
			char[] fileNameChar = new char[size];
			reader.read(fileNameChar);
			reader.close();
			Object obj = ArticleFactory.createArticle(new File(
					getNewsgroupHome(newsgroup) + SALVO.SEPARATOR
							+ String.valueOf(fileNameChar)));
			if (obj instanceof IArticle) {
				IArticle article = (IArticle) obj;
				article.setNewsgroup(newsgroup);
				return article;
			}
		} catch (Exception e) {
			Debug.log(getClass(), e);
			lastException = e;
		} finally {
			try {
				if (reader != null)
					reader.close();
			} catch (IOException e) {
			}
		}
		return null;
	}

	public IArticle getLastArticle(INewsgroup newsgroup) {

		if (lastArticles.get(newsgroup.getURL()) != null) {
			return (IArticle) lastArticles.get(newsgroup.getURL());
		}

		File infoDir = new File(getNumbersHome(newsgroup));
		if (!infoDir.exists()) {
			infoDir.mkdir();
			return null;
		}

		File last = new File("000000");
		File[] listFiles = infoDir.listFiles();
		for (int i = 0; i < listFiles.length; i++) {

			File file = listFiles[i];
			if (file.getName().compareTo(last.getName()) > 0) {
				last = file;
			}
		}

		if (last.getName().equals("000000")) {
			return null;
		}

		IArticle lastArticle = getArticle(newsgroup, last);
		lastArticles.put(newsgroup.getURL(), lastArticle);
		return lastArticle;
	}

	public String[] getArticleBody(IArticle article) {

		// Get the body from disk
		try {
			File serialized = new File(getArticleDirectory(
					article.getNewsgroup(), article.getMessageId())
					+ SALVO.SEPARATOR + "body.data");
			if (serialized.exists()) {
				FileInputStream f_in = new FileInputStream(serialized);
				ObjectInputStream obj_in = new ObjectInputStream(f_in);
				Object obj = obj_in.readObject();
				obj_in.close();
				if (obj instanceof String[]) {
					return (String[]) obj;
				}
			}
		} catch (Exception e) {
			Debug.log(getClass(), e);
			lastException = e;
		}

		return new String[0];
	}

	public void storeArticleBody(IArticle article, String[] body) {

		try {
			File file = new File(getArticleDirectory(article.getNewsgroup(),
					article.getMessageId()));
			if (!file.exists())
				file.mkdirs();
			file = new File(file.getAbsolutePath() + SALVO.SEPARATOR
					+ "body.data");

			// FIXME Should we assume that bodies are immutable?
			if (file.exists())
				return;

			// Serialize this article body
			FileOutputStream f_out = new FileOutputStream(file);
			ObjectOutputStream obj_out = new ObjectOutputStream(f_out);
			obj_out.writeObject(body);
			obj_out.close();
		} catch (Exception e) {
			Debug.log(getClass(), e);
		}
	}

	public IArticle[] getFollowUps(IArticle article) {

		Collection result = new ArrayList();

		// Get the follow ups from disk
		File articleDir = new File(getArticleDirectory(article.getNewsgroup(),
				article.getMessageId()));
		if (!articleDir.exists())
			return (IArticle[]) result.toArray(new IArticle[0]);
		File[] listFiles = articleDir.listFiles(new FileFilter() {

			public boolean accept(File pathname) {
				if (pathname.getAbsolutePath().endsWith("data"))
					return true;
				return false;
			}
		});

		for (int j = 0; j < listFiles.length; j++) {

			File serialized = listFiles[j];
			if (serialized.exists()) {
				Object obj = ArticleFactory.createArticle(serialized);
				if (obj instanceof IArticle) {
					IArticle newArticle = (IArticle) obj;
					newArticle.setNewsgroup(article.getNewsgroup());
					newArticle.setParent(article);
					result.add(newArticle);
				}
			}
		}

		return (IArticle[]) result.toArray(new IArticle[0]);

	}

	public void updateArticle(IArticle article) {
		storeArticles(new IArticle[] { article });
		fireEvent(new StoreEvent(article, SALVO.EVENT_CHANGE_GROUP));
	}

	public String getDescription() {
		return "Local Filesystem Storage";
	}

	public void storeArticles(IArticle[] articles) {

		boolean result = internalStoreArticles(Arrays.asList(articles));

		// Send store event
		if (result)
			fireEvent(new StoreEvent(articles, SALVO.EVENT_CHANGE_GROUP));

	}

	public void setWaterMarks(INewsgroup newsgroup) throws NNTPIOException,
			NNTPConnectException {
		updateAttributes(newsgroup);

	}

	public void setSecureStore(ISecureStore secureStore) {
		this.secureStore = secureStore;
	}

	public IArticle getArticle(String URL) throws NNTPIOException,
			UnexpectedResponseException, NNTPException {
		int articleNumber;
		String newsgroup;
		String server;

		try {
			newsgroup = StringUtils.split(
					StringUtils.split(URL, "&article")[0], "=")[1];
			server = StringUtils.split(URL, "/?group")[0];
			articleNumber = Integer.parseInt(StringUtils.split(URL, "=")[2]);
		} catch (Exception e) {
			throw new NNTPException("Error parsing URL " + URL, e);
		}

		IServer[] servers = getServers();
		for (int i = 0; i < servers.length; i++) {
			if (servers[i].getURL().equals(server)) {
				INewsgroup[] groups = getSubscribedNewsgroups(servers[i]);
				for (int j = 0; j < groups.length; j++) {
					if (groups[j].getNewsgroupName().equals(newsgroup))
						return getArticle(groups[j], articleNumber);
				}
				return null;
			}
		}

		return null;

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

		int result = 0;

		IArticle[] followUps = getFollowUps(article);
		for (int i = 0; i < followUps.length; i++) {
			IArticle followUp = followUps[i];
			result = result + delete(followUp);
		}

		File file = new File(getArticleFilename(article));
		if (file.exists())
			file.delete();
		if (file.exists())
			throw new NNTPIOException("File " + file.getAbsolutePath()
					+ " could not be purged");

		file = new File(getArticleFilenameById(article.getNewsgroup(),
				article.getMessageId()));
		if (file.exists())
			file.delete();
		if (file.exists())
			throw new NNTPIOException("File " + file.getAbsolutePath()
					+ " could not be purged");

		file = new File(getArticleFilenameByNumber(article.getNewsgroup(),
				article.getArticleNumber()));
		if (file.exists())
			file.delete();
		if (file.exists())
			throw new NNTPIOException("File " + file.getAbsolutePath()
					+ " could not be purged");

		if (!article.isReply()) {
			File directory = new File(getArticleDirectory(
					article.getNewsgroup(), article.getMessageId()));
			if (directory.exists()) {
				File[] files = directory.listFiles();
				for (int i = 0; i < files.length; i++) {
					if (files[i].exists())
						files[i].delete();
					if (files[i].exists())
						throw new NNTPIOException("File "
								+ files[i].getAbsolutePath()
								+ " could not be purged");
				}
				directory.delete();
				if (directory.exists())
					throw new NNTPIOException("File "
							+ directory.getAbsolutePath()
							+ " could not be purged");

			}
		}

		if (getFirstArticle(article.getNewsgroup()).equals(article)) {
			clearFirstArticle(article.getNewsgroup().getURL());
		}

		if (getLastArticle(article.getNewsgroup()).equals(article)) {
			clearLastArticle(article.getNewsgroup().getURL());
		}

		fireEvent(new StoreEvent(article, SALVO.EVENT_REMOVE_ARTICLE));

		return ++result;
	}

	private void clearFirstArticle(String newsgroupURL) {
		firstArticles.remove(newsgroupURL);
	}

	private void clearLastArticle(String newsgroupURL) {
		lastArticles.remove(newsgroupURL);
	}

	public int getListenerCount() {
		if (listeners == null)
			return 0;
		return listeners.size();
	}
}
