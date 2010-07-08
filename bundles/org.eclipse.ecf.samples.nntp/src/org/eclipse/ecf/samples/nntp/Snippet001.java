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
import org.eclipse.ecf.protocol.nntp.model.IArticle;
import org.eclipse.ecf.protocol.nntp.model.ICredentials;
import org.eclipse.ecf.protocol.nntp.model.INewsgroup;
import org.eclipse.ecf.protocol.nntp.model.IServer;

/**
 * This snippet demonstrates how to read news from a server without a
 * corresponding store. This demonstration is slow because it is not smart in
 * it's processing. It first fetches a number of articles from the server and
 * then determines if each article is a main article (i.e. not a
 * follow-up/reply). If it is not, the article is printed and the the server is
 * visited again to get the follow-ups/replies to this article, et cetera.
 * 
 * <p>
 * In a smart client, you would have fetched the articles and remembered all the
 * articles. Then, to get a reference from a main article you would first
 * determine if the follow-up was already fetched and if not, fetch it.
 * </p>
 * 
 * @author Wim Jongman
 * 
 */
public class Snippet001 {

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
			return "foo.bar@foobar.org";
		}
	};

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		Debug.debug = false;

		// Create a server
		IServer server = ServerFactory.getCreateServer("news.eclipse.org", 119,
				credentials, true);

		// Attach a newsgroup to the server
		INewsgroup group = NewsgroupFactory.createNewsGroup(server,
				"eclipse.technology.ecf", "Eclipse Test");

		server.getServerConnection().setWaterMarks(group);

		// Read messages
		IArticle[] articles = server.getServerConnection().getArticles(group,
				group.getLowWaterMark(), 100);

		for (int i = 0; i < articles.length; i++) {
			if (!articles[i].isReply()) {
				System.out.println(getSubject(articles[i]) + "  ("
						+ articles[i].getFullUserName() + ")");

				printReplies(articles[i], 1);

			}
		}
	}

	/**
	 * Prints replies until exhausted. Could well only print one reference due
	 * to the xpat newsreader command bogusinity.
	 * 
	 * @param article
	 * @param invocation
	 * @throws Exception
	 */
	private static void printReplies(IArticle article, int invocation)
			throws Exception {

		IArticle[] replies = article.getServer().getServerConnection()
				.getFollowUps(article);

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
