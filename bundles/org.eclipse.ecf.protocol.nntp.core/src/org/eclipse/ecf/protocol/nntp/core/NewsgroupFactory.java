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

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

import org.eclipse.ecf.protocol.nntp.core.internal.Newsgroup;
import org.eclipse.ecf.protocol.nntp.model.INewsgroup;
import org.eclipse.ecf.protocol.nntp.model.IServer;


public class NewsgroupFactory {

	public static INewsgroup createNewsGroup(IServer server, String newsgroup,
			String description) {
		return new Newsgroup(server, newsgroup, description);
	}

	public static INewsgroup createNewsGroup(File file) {
		if (file.exists()) {
			try {
				FileInputStream f_in = new FileInputStream(file);
				ObjectInputStream obj_in = new ObjectInputStream(f_in);
				Object obj = obj_in.readObject();
				if (obj instanceof INewsgroup) {
					return (INewsgroup) obj;
				}
			} catch (Exception e) {
			}
		}
		return null;
	}
}
