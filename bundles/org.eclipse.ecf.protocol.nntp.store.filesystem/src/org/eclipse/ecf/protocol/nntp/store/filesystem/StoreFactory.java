package org.eclipse.ecf.protocol.nntp.store.filesystem;

import org.eclipse.ecf.protocol.nntp.model.IStore;
import org.eclipse.ecf.protocol.nntp.model.IStoreFactory;
import org.eclipse.ecf.protocol.nntp.model.SALVO;
import org.eclipse.ecf.protocol.nntp.model.StoreException;
import org.eclipse.ecf.protocol.nntp.store.filesystem.internal.Store;

/**
 * The store factory will create {@link IStore} implementations.
 * 
 * @author Wim Jongman
 * 
 */
public class StoreFactory implements IStoreFactory {

	private IStore store;

	public IStore createStore(String root) {
		if (store == null)
			store = new Store(root + SALVO.SEPARATOR + "SalvoFilesystemStore");
		return store;
	}

	public void deleteStore() throws StoreException {
		throw new StoreException("not implemented ..");		
	}
}
