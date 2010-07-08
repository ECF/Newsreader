package org.eclipse.ecf.provider.nntp.internal;

import java.net.URISyntaxException;

import org.eclipse.ecf.core.identity.BaseID;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.core.util.StringUtils;

public class NNTPHostID extends BaseID {

	private static final long serialVersionUID = 3257289140701049140L;

	protected String hostname;
	protected int port = -1;

	private final String URI;

	public NNTPHostID(Namespace namespace, String URI)
			throws URISyntaxException {

		super(namespace);
		this.URI = URI; 

		// valid URI is nntp://host.net:123[/]
		if (!URI.startsWith("nntp://"))
			throw new URISyntaxException(URI, "URI does not start with nntp://");

		try {
			String portString = StringUtils.split(URI, ":")[2];
			if (portString.endsWith("/"))
				portString = portString.substring(0, portString.length() - 1);
			port = Integer.parseInt(portString);
		} catch (Exception e) {
			URISyntaxException e2 = new URISyntaxException(URI, e.getMessage());
			e2.setStackTrace(e.getStackTrace());
			throw e2;
		}

		hostname = StringUtils.split(URI, ":")[1];
		hostname = hostname.substring(2);
	}

	protected int namespaceCompareTo(BaseID o) {
		return getName().compareTo(o.getName());
	}

	protected boolean namespaceEquals(BaseID o) {
		if (!(o instanceof NNTPHostID)) {
			return false;
		}
		final NNTPHostID other = (NNTPHostID) o;
		// Get resources from this and other
		String thisResourceName = getResourceName();
		String otherResourceName = other.getResourceName();

		// The resources are considered equal if either one is null (not known
		// yet), or they are equal by
		// string comparison
		boolean resourceEquals = false;
		if (thisResourceName == null) {
			resourceEquals = (otherResourceName == null) ? true : false;
		} else {
			resourceEquals = thisResourceName.equals(otherResourceName);
		}
		return false;
	}

	private String getResourceName() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.identity.BaseID#namespaceToExternalForm()
	 */
	protected String namespaceToExternalForm() {
		return URI;
	}

	public int getPort() {
		return port;
	}

	public String toString() {
		final StringBuffer sb = new StringBuffer("XMPPID["); //$NON-NLS-1$
		sb.append(toExternalForm()).append("]");
		return sb.toString();
	}

	public Object getAdapter(Class clazz) {
		if (clazz.isInstance(this)) {
			return this;
		} else
			return super.getAdapter(clazz);
	}

	protected String namespaceGetName() {
		// TODO Auto-generated method stub
		return null;
	}

	protected int namespaceHashCode() {
		// TODO Auto-generated method stub
		return 0;
	}
}
