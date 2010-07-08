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

import org.apache.james.mime4j.codec.DecoderUtil;
import org.eclipse.ecf.protocol.nntp.core.Debug;
import org.eclipse.ecf.protocol.nntp.core.NewsgroupFactory;
import org.eclipse.ecf.protocol.nntp.core.ServerFactory;
import org.eclipse.ecf.protocol.nntp.core.ServerStoreFactory;
import org.eclipse.ecf.protocol.nntp.core.StoreStore;
import org.eclipse.ecf.protocol.nntp.model.IArticle;
import org.eclipse.ecf.protocol.nntp.model.ICredentials;
import org.eclipse.ecf.protocol.nntp.model.INewsgroup;
import org.eclipse.ecf.protocol.nntp.model.IServer;
import org.eclipse.ecf.protocol.nntp.model.IServerStoreFacade;
import org.eclipse.ecf.protocol.nntp.model.SALVO;
import org.eclipse.ecf.protocol.nntp.store.filesystem.StoreFactory;

/**
 * This snippet demonstrates how to read news from a server with a corresponding
 * store. There is no subscription of the server it is created for one time use.
 * However, the information retrieved from the server is cached into the store.
 * The first time you call this program it will has to fetch the information
 * from the server. The next time the articles are fetched from the local store.
 * 
 * @author Wim Jongman
 * 
 */
public class Snippet002 {

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

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		Debug.debug = false;

		// Fill in a store
		StoreStore.instance().registerStore(
				new StoreFactory().createStore(SALVO.SALVO_HOME
						+ SALVO.SEPARATOR + "snippet002"));

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

		// Read messages
		IArticle[] articles = serverStoreFacade.getArticles(group,
				group.getLowWaterMark(), 200);

		for (int i = 0; i < articles.length; i++) {
			if (!articles[i].isReply()) {
				System.out.println(getSubject(articles[i]) + "  ("
						+ articles[i].getFullUserName() + ")");

				printReplies(articles[i], 1);

			}
		}
	}

	/**
	 * Prints replies recursively until exhausted.
	 * 
	 * @param article
	 * @param invocation
	 * @throws Exception
	 */
	private static void printReplies(IArticle article, int invocation)
			throws Exception {

		IArticle[] replies = serverStoreFacade.getFollowUps(article);

		if (replies.length == 0)
			return;

		for (int j = 0; j < replies.length; j++) {
			for (int t = 0; t < invocation; t++) {
				System.out.print("..");
			}
			System.out.println(getSubject(replies[j]) + "  ("
					+ replies[j].getFullUserName() + ")");
			printReplies(replies[j], (invocation + 1));
		}
	}

	private static String getSubject(IArticle article) {
		return DecoderUtil.decodeEncodedWords(article.getSubject());
	}
}
