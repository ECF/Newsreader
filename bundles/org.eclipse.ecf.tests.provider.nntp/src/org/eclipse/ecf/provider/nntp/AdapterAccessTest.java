package org.eclipse.ecf.provider.nntp;


import org.eclipse.ecf.provider.nntp.internal.NNTPNameSpace;
import org.eclipse.ecf.tests.presence.AbstractAdapterAccessTest;

public class AdapterAccessTest extends AbstractAdapterAccessTest{

	@Override
	protected String getClientContainerName() {
		return NNTPNameSpace.NAME;
	}

	

}
