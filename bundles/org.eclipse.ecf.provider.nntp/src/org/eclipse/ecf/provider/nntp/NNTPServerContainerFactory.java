package org.eclipse.ecf.provider.nntp;

import org.eclipse.ecf.core.ContainerCreateException;
import org.eclipse.ecf.core.ContainerTypeDescription;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.provider.BaseContainerInstantiator;

public class NNTPServerContainerFactory extends BaseContainerInstantiator {

	public IContainer createInstance(ContainerTypeDescription description,
			Object[] parameters) throws ContainerCreateException {

		try {
			if (parameters != null) {
				if (parameters[0] instanceof ID) {
					return new NNTPServerContainer((ID) parameters[0]);
				} else if (parameters[0] instanceof String) {
					return new NNTPServerContainer(IDFactory.getDefault()
							.createStringID((String) parameters[0]));
				}
			}

			return new NNTPServerContainer(IDFactory.getDefault().createGUID());
		} catch (final Exception e) {
			throw new ContainerCreateException(
					"Could not create nntp server container", e);
		}
	}
}
