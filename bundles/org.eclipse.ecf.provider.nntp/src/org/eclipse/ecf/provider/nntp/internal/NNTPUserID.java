package org.eclipse.ecf.provider.nntp.internal;

import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.core.identity.StringID;

public class NNTPUserID extends StringID {

	/**
	 * 
	 */
	private static final long serialVersionUID = -253612068591600395L;

	/**
	 * @param n
	 * @param s
	 */
	protected NNTPUserID(Namespace n, String s) {
		super(n, s);
	}

	public NNTPUserID(String s) {
		super(IDFactory.getDefault().getNamespaceByName(NNTPNameSpace.NAME), s);
	}

}
