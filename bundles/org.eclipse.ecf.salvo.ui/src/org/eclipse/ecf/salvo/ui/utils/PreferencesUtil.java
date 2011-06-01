package org.eclipse.ecf.salvo.ui.utils;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.Preferences;

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
		} catch (org.osgi.service.prefs.BackingStoreException e) {
			e.printStackTrace();
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
