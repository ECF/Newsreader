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
 * This class is used to help the store to store secure information. An instance
 * of this class will be injected into the store to provide a local solution.
 * For example, in an OSGi equinox environment, the secure preferences could be
 * used.
 * 
 * @author Wim Jongman - Initial API and implementation
 * 
 */
public interface ISecureStore {

	/**
	 * Stores a value associated with the key.
	 * 
	 * @param key
	 *            key with which the value is going to be associated, may not be
	 *            null
	 * @param value
	 *            value to store, may not be null
	 * @param encrypt
	 *            <code>true</code> if value is to be encrypted,
	 *            <code>false</code> value does not need to be encrypted
	 */
	public void put(String key, String value, boolean encrypt);

	/**
	 * Retrieves a value associated with the key. If the value was encrypted, it
	 * is decrypted.
	 * 
	 * @param key
	 *            key with this the value is associated, may not be null
	 * @param def
	 *            default value to return if the key is not associated with any
	 *            value
	 * @return value associated the key. If value was stored encrypted, it will
	 *         be decrypted
	 */
	public String get(String key, String def);

	/**
	 * Removes value associated with the key.
	 * 
	 * @param key
	 *            key with which a value is associated, may not be null
	 */
	public void remove(String key);

	/**
	 * Removes all values.
	 */
	public void clear();

}
