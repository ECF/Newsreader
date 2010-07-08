package org.eclipse.ecf.provider.nntp.internal;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.identity.Namespace;

public class NNTPNameSpace extends Namespace {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2450042292994735908L;

	public static final String NAME = "ecf.namespace.nntp";
	public static final String SCHEME = "nntp";

	public ID createInstance(Object[] parameters) throws IDCreateException {
		try {
			final String init = getInitFromExternalForm(parameters);
			if (init != null)
				return new NNTPHostID(this, init);
			return new NNTPHostID(this, parameters[0].toString());
		} catch (final Exception e) {
			throw new IDCreateException("Cannot create NNTP ID");
		}
	}

	private String getInitFromExternalForm(Object[] args) {
		if (args == null || args.length < 1 || args[0] == null)
			return null;
		if (args[0] instanceof String) {
			return (String) args[0];
//			final String arg = (String) args[0];
//			if (arg.startsWith(getScheme() + Namespace.SCHEME_SEPARATOR)) {
//				final int index = arg.indexOf(Namespace.SCHEME_SEPARATOR);
//				if (index >= arg.length())
//					return null;
//				return arg.substring(index + 3);
//			}
		}
		return null;
	}

	public String getScheme() {
		return SCHEME;
	}

	public String[] getSupportedSchemes() {
		return new String[] { getScheme() };
	}
}
