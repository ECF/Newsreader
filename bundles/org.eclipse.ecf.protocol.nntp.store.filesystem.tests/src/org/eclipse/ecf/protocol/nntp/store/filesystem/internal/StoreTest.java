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
package org.eclipse.ecf.protocol.nntp.store.filesystem.internal;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;

import org.eclipse.ecf.protocol.nntp.model.ISecureStore;
import org.eclipse.ecf.protocol.nntp.model.IServer;
import org.eclipse.ecf.protocol.nntp.model.SALVO;
import org.eclipse.ecf.protocol.nntp.store.filesystem.StoreFactory;
import org.eclipse.ecf.protocol.nntp.store.tests.AbstractStoreTest;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class StoreTest extends AbstractStoreTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {

		setStore(new StoreFactory().createStore(SALVO.SALVO_HOME
				+ SALVO.SEPARATOR + "StoreTestFileSystem"));

		IServer[] subscribedServers = getStore().getSubscribedServers();
		for (int i = 0; i < subscribedServers.length; i++) {
			IServer iServer = subscribedServers[i];
			getStore().unsubscribeServer(iServer, true);
		}

		getStore().setSecureStore(new ISecureStore() {
			HashMap<String, String> mappie = new HashMap<String, String>();

			public void remove(String key) {
				mappie.remove(key);
			}

			public void put(String key, String value, boolean encrypt) {
				mappie.put(key, value);
			}

			public String get(String key, String def) {
				return mappie.get(key).equals(null) ? def : mappie.get(key);
			}

			public void clear() {
				mappie.clear();
			}
		});
	}

	@After
	public void tearDown() throws Exception {
		IServer[] subscribedServers = getStore().getSubscribedServers();
		for (int i = 0; i < subscribedServers.length; i++) {
			IServer iServer = subscribedServers[i];
			getStore().unsubscribeServer(iServer, true);
		}
	}

	@Test
	public void testStore() {
		assertTrue(getStore() != null);
	}
}
