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

public interface ISubscribable {

	/**
	 * Subscribes or unsubscribes to this subscribable.
	 * 
	 * @param subscribe
	 */
	public void setSubscribed(boolean subscribe);

	/**
	 * Returns if the user is subscribed to the subscribeable. Clients use this
	 * to show the or hide it from the active view.
	 * 
	 * @return
	 */
	public boolean isSubscribed();

}
