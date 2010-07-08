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
package org.eclipse.ecf.protocol.nntp.core;

import static junit.framework.Assert.*;

import org.eclipse.ecf.protocol.nntp.model.SALVO;
import org.junit.Test;


public class SALVOTest {

	@Test
	public void logicalTest() {

		assertTrue((SALVO.EVENT_ADD | SALVO.EVENT_ADD_GROUP) == SALVO.EVENT_ADD);
		assertTrue((SALVO.EVENT_ADD | SALVO.EVENT_ADD_SERVER) == SALVO.EVENT_ADD);
		assertFalse((SALVO.EVENT_ADD | SALVO.EVENT_CHANGE_SERVER) == SALVO.EVENT_ADD);
		
		assertTrue((SALVO.EVENT_REMOVE | SALVO.EVENT_REMOVE_GROUP) == SALVO.EVENT_REMOVE);
		assertTrue((SALVO.EVENT_REMOVE | SALVO.EVENT_REMOVE_SERVER) == SALVO.EVENT_REMOVE);
		assertFalse((SALVO.EVENT_REMOVE | SALVO.EVENT_CHANGE_SERVER) == SALVO.EVENT_REMOVE);
		
		assertTrue((SALVO.EVENT_CHANGE | SALVO.EVENT_CHANGE_GROUP) == SALVO.EVENT_CHANGE);
		assertTrue((SALVO.EVENT_CHANGE | SALVO.EVENT_CHANGE_SERVER) == SALVO.EVENT_CHANGE);
		assertFalse((SALVO.EVENT_CHANGE | SALVO.EVENT_ADD_SERVER) == SALVO.EVENT_CHANGE);
		
	
	}
 
}
