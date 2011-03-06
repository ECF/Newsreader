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

import java.util.HashMap;

import org.eclipse.ecf.protocol.nntp.model.ISecureStore;
import org.eclipse.ecf.protocol.nntp.model.IStoreFactory;
import org.eclipse.ecf.protocol.nntp.model.SALVO;
import org.eclipse.ecf.protocol.nntp.store.derby.StoreFactory;
import org.eclipse.ecf.protocol.nntp.store.tests.AbstractStoreTest;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

public class StoreTest extends AbstractStoreTest {

	private IStoreFactory sf;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		sf = new StoreFactory();
		setStore(sf.createStore(SALVO.SALVO_HOME + SALVO.SEPARATOR
				+ "StoreTestDerby"));
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
		sf.deleteStore();
	}

}
