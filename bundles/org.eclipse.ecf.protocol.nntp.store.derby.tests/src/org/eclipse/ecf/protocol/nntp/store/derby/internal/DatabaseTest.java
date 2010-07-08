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

import org.eclipse.ecf.protocol.nntp.model.SALVO;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class DatabaseTest {

	public static Database db;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		db = Database.createDatabase(SALVO.SALVO_HOME + SALVO.SEPARATOR
				+ "StoreTestDerby", true);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		db.dropDB();
		db.closeDB();
		assertFalse(db.existDB());
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testExistDB() {
		assertTrue(db.existDB());
	}

}
