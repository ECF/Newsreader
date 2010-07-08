package org.eclipse.ecf.provider.nntp.security;

import org.eclipse.ecf.protocol.nntp.core.Debug;
import org.eclipse.ecf.protocol.nntp.model.SALVO;
import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.eclipse.equinox.security.storage.StorageException;

public class SalvoUtil {

	public static String getPassword(String address) {
		ISecurePreferences prefs = SecurePreferencesFactory.getDefault();
		ISecurePreferences node = prefs.node(SALVO.SECURE_PREFS_NODE);
		try {
			return node.get(address, "");
		} catch (StorageException e) {
			Debug.log(SalvoUtil.class, "Secure Storage Exception: "
					+ e.getMessage());
			return "";
		}
	}

}
