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
package org.eclipse.ecf.salvo.ui.internal.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ecf.protocol.nntp.core.Debug;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.handlers.HandlerUtil;

public class ReplyToMailHandler extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {

		MessageDialog.openInformation(HandlerUtil.getActiveShell(event),
				"Not yet implemented",
				"File a bug @ http://bugs.eclipse.org/bugs !");

		Debug.log(getClass(), "execute" + "(" + event + ")");
		return null;
	}

}
