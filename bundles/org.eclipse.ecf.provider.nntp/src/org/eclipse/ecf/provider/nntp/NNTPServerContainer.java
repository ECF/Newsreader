package org.eclipse.ecf.provider.nntp;

import org.eclipse.ecf.core.AbstractContainer;
import org.eclipse.ecf.core.BaseContainer;
import org.eclipse.ecf.core.ContainerConnectException;
import org.eclipse.ecf.core.ContainerCreateException;
import org.eclipse.ecf.core.events.ContainerConnectedEvent;
import org.eclipse.ecf.core.events.ContainerConnectingEvent;
import org.eclipse.ecf.core.events.ContainerDisconnectedEvent;
import org.eclipse.ecf.core.events.ContainerDisconnectingEvent;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.core.security.IConnectContext;
import org.eclipse.ecf.protocol.nntp.model.IServer;
import org.eclipse.ecf.provider.nntp.internal.NNTPNameSpace;

public class NNTPServerContainer extends AbstractContainer {

	private ID targetID;
	private ID containerID;
	private IServer server;

	protected NNTPServerContainer(ID id) {
		super();
		this.containerID = id;
	}

	public Namespace getConnectNamespace() {
		return IDFactory.getDefault().getNamespaceByName(NNTPNameSpace.NAME);
	}

	public void connect(ID targetID, IConnectContext connectContext)
			throws ContainerConnectException {
		if (!targetID.getNamespace().getName().equals(
				getConnectNamespace().getName()))
			throw new ContainerConnectException(
					"targetID not of appropriate Namespace");

		fireContainerEvent(new ContainerConnectingEvent(getID(), targetID));

		// XXX connect to remote service here

		this.targetID = targetID;
		fireContainerEvent(new ContainerConnectedEvent(getID(), targetID));
	}

	public void disconnect() {
		fireContainerEvent(new ContainerDisconnectingEvent(getID(), targetID));

		final ID oldID = targetID;

		// XXX disconnect here

		fireContainerEvent(new ContainerDisconnectedEvent(getID(), oldID));
	}

	public ID getConnectedID() {
		return targetID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.identity.IIdentifiable#getID()
	 */
	public ID getID() {
		return containerID;
	}

	public void setServer(IServer server) {
		this.server = server;

	}

	public IServer getServer() {
		return server;
	}
}
