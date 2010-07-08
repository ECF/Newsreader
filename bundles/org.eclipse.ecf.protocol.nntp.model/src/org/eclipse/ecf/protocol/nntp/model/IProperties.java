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
package org.eclipse.ecf.protocol.nntp.model;

import java.util.Map;

public interface IProperties {
	
	/**
	 * Sets an arbitrary property, an already existing property will be replaced
	 * and a null for the value removes the property.
	 * 
	 * @param key
	 * @param value
	 */
	public void setProperty(String key, String value);

	/**
	 * Gets a previously set property.
	 * 
	 * @param key
	 * @return null if the property key does not exist
	 */
	public String getProperty(String key);

	/**
	 * @return all the specified properties in a map.
	 */
	public Map getProperties();


}
