/*******************************************************************************
 *  Copyright (c) 2011 University Of Moratuwa
 *                                                                      
 * All rights reserved. This program and the accompanying materials     
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at             
 * http://www.eclipse.org/legal/epl-v10.html                            
 *                                                                      
 * Contributors:                                                        
 *    Isuru Udana - UI Integration in the Workbench
 *******************************************************************************/
package org.eclipse.ecf.salvo.ui.tools;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.ecf.protocol.nntp.core.Debug;
import org.osgi.service.prefs.Preferences;
import org.osgi.service.prefs.BackingStoreException;

public class PreferencesUtil {
	private static PreferencesUtil INSTANCE;
	
	/**
	 * @return an instance of PreferencesUtil
	 */
	public static PreferencesUtil instance(){
		if (INSTANCE==null){
			INSTANCE=new PreferencesUtil();
		}
		return INSTANCE;
	}
	
	/**
	 * This method will store preference based on the workspace
	 * @param key key of the stored preference
	 * @param value value of the stored preference
	 */
	public void savePluginSettings(String key, String value) {
		// saves plugin preferences at the workspace level
		Preferences prefs = new InstanceScope()
				.getNode("org.eclipse.ecf.salvo.ui");
		prefs.put(key, value);
		try {
			prefs.flush();
		} catch (BackingStoreException e) {
			Debug.log(this.getClass(), e);
		}

	}
	
	/**
	 * This method gives the stored preference value for the given key
	 * @param key key of the value to be retrieved from the preferences
	 * @return the value of the given key
	 */
	public String loadPluginSettings(String key) {
		  Preferences prefs = new InstanceScope().getNode("org.eclipse.ecf.salvo.ui");
		  String value = prefs.get(key,"null");
		  return value;
	}

	
}
