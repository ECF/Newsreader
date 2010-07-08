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
package org.eclipse.ecf.protocol.nntp.core.internal;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.ecf.protocol.nntp.core.ServerFactory;
import org.eclipse.ecf.protocol.nntp.model.AbstractCredentials;
import org.eclipse.ecf.protocol.nntp.model.ICredentials;
import org.eclipse.ecf.protocol.nntp.model.IServer;
import org.eclipse.ecf.protocol.nntp.model.IServerConnection;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


public class ServerTest {

	final static String EMAIL = "wim.jongman@gmail.com";
	final static String LOGIN = "exquisitus";
	final static String PASSWORD = "flinder1f7";
	private static final int PORT = 119;
	private static IServer server;
	final static String SERVER = "news.eclipse.org";
	final static String USER = "Wim Jongman";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		ICredentials credentials = new AbstractCredentials(USER,EMAIL,LOGIN,PASSWORD) {};
		server = ServerFactory.getCreateServer(SERVER, PORT, credentials, true);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testHashCode() {
		// fail("Not yet implemented");
	}

	@Test
	public void testServer() {
		assertTrue(server != null);
	}

	@Test
	public void testGetPort() {
		assertTrue(server.getPort() == PORT);
	}

	@Test
	public void testGetAddress() {
		assertTrue(server.getAddress().equals(SERVER));
	}

	@Test
	public void testToString() {
		assertTrue(server
				.toString()
				.equals(
						"news.eclipse.org::119::Wim Jongman::wim.jongman@gmail.com::exquisitus::true"));
	}

	@Test
	public void testEqualsObject() {
		// fail("Not yet implemented");
	}

	@Test
	public void testIsSecure() {
		assertTrue(server.isSecure());
	}

	@Test
	public void testGetServerConnection() {
		assertTrue(server.getServerConnection() != null);
	}

	@Test
	public void testIsAnonymous() {
		assertFalse(server.isAnonymous());
	}

	@Test
	public void testInit() {
		// fail("Not yet implemented");
	}

	@Test
	public void testSetServerConnection() {
		IServerConnection connection = server.getServerConnection();
		server.setServerConnection(null);
		assertTrue(server.getServerConnection() == null);
		server.setServerConnection(connection);
	}

}
