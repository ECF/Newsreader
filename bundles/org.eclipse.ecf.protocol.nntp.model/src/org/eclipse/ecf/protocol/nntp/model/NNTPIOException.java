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
package org.eclipse.ecf.protocol.nntp.model;

/**
 * This exception is thrown when something in the store or server went wrong.
 * 
 * @author Wim Jongman
 * 
 */
public class NNTPIOException extends NNTPException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7279967326719278029L;

	public NNTPIOException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public NNTPIOException(String arg0) {
		super(arg0);
	}
}
