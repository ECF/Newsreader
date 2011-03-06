/*******************************************************************************
 *  Copyright (c) 2010 Weltevree Beheer BV, Nederland
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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;

import org.eclipse.ecf.protocol.nntp.model.ICredentials;
import org.eclipse.ecf.protocol.nntp.model.ISecureStore;
import org.eclipse.ecf.protocol.nntp.model.IStore;
import org.eclipse.ecf.protocol.nntp.model.IStoreFactory;
import org.eclipse.ecf.protocol.nntp.model.SALVO;
import org.eclipse.ecf.protocol.nntp.store.derby.StoreFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class DatabaseTest {

	public static Database db;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
//		db = Database.createDatabase(SALVO.SALVO_HOME + SALVO.SEPARATOR
//				+ "StoreTestDerby", true);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
//		db.dropDB();
//		assertFalse(db.existDB());
	}

	@Before
	public void setUp() throws Exception {
		StoreFactory sf = new StoreFactory();
		IStore store = sf.createStore(SALVO.SALVO_HOME + SALVO.SEPARATOR
				+ "StoreTestDerby");
		db = ((Store) store).getDatabase();
		store.setSecureStore(new ISecureStore() {
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

		// setUpBeforeClass();
		ICredentials iCredentials = new ICredentials() {

			public String getUser() {
				return "Wim Jongman";
			}

			public String getPassword() {
				return "flinder1f7";
			}

			public String getOrganization() {
				return "organization";
			}

			public String getLogin() {
				return "exquisitus";
			}

			public String getEmail() {
				return "wim.jongman@gmail.com";
			}
		};

	}

	@After
	public void tearDown() throws Exception {
		db.closeDB();

	}

	@Test
	public void testExistDB() {
		assertTrue(db.existDB());
	}

}
