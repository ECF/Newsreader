package org.eclipse.ecf.provider.nntp.security;

import org.eclipse.ecf.protocol.nntp.core.Debug;
import org.eclipse.ecf.protocol.nntp.model.ISecureStore;
import org.eclipse.ecf.protocol.nntp.model.SALVO;
import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.eclipse.equinox.security.storage.StorageException;

public class SalvoSecureStore implements ISecureStore {

	private ISecurePreferences node;

	public SalvoSecureStore() {
		ISecurePreferences prefs = SecurePreferencesFactory.getDefault();
		node = prefs.node(SALVO.SECURE_PREFS_NODE);
	}

	public void clear() {
		node.clear();
	}

	public String get(String key, String def) {
		try {
			return node.get(key, def);
		} catch (StorageException e) {
			return def;
		}
	}

	public void put(String key, String value, boolean encrypt) {
		try {
			node.put(key, value, encrypt);
		} catch (StorageException e) {
			Debug.log(getClass(), e);
		}
	}

	public void remove(String key) {
		node.remove(key);
	}

	}
