package org.eclipse.ecf.provider.ui.wizards;

import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.IContainerListener;
import org.eclipse.ecf.core.events.IContainerConnectedEvent;
import org.eclipse.ecf.core.events.IContainerEvent;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.security.ConnectContextFactory;
import org.eclipse.ecf.core.security.IConnectContext;
import org.eclipse.ecf.protocol.nntp.model.IServer;
import org.eclipse.ecf.protocol.nntp.model.NNTPException;
import org.eclipse.ecf.provider.nntp.NNTPServerContainer;
import org.eclipse.ecf.salvo.ui.wizards.NewNewsServerWizard;
import org.eclipse.ecf.ui.IConnectWizard;
import org.eclipse.ecf.ui.actions.AsynchContainerConnectAction;
import org.eclipse.ecf.ui.dialogs.IDCreateErrorDialog;
import org.eclipse.ecf.ui.util.PasswordCacheHelper;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbench;

public class NewNewsServerConnectWizard extends NewNewsServerWizard implements
		IConnectWizard {

	private IContainer container;
	private IConnectContext connectContext;
	private ID targetID;
	private IWorkbench workbench;
	private String connectID;

	public NewNewsServerConnectWizard() {
		// TODO Auto-generated constructor stub
	}

	public NewNewsServerConnectWizard(IServer server) {
		super(server);
		// TODO Auto-generated constructor stub
	}

	private void openPerspective() {
		IPerspectiveDescriptor descriptor = workbench.getPerspectiveRegistry()
				.findPerspectiveWithId(
						"org.eclipse.ecf.salvo.application.perspective");
		workbench.getActiveWorkbenchWindow().getActivePage().setPerspective(
				descriptor);
	}

	public void init(IWorkbench workbench, IContainer container) {
		this.workbench = workbench;
		this.container = container;
	}

	public boolean performFinish() {

		super.performFinish();

		try {
			connectID = page1.getServer().getURL();
			((NNTPServerContainer) container).setServer(page1.getServer());
		} catch (NNTPException e1) {
			new IDCreateErrorDialog(null, connectID, new IDCreateException(e1))
					.open();
			return false;
		}
		connectContext = ConnectContextFactory
				.createPasswordConnectContext(page1.getPass());
		final String pass = page1.getPass();
		try {
			targetID = IDFactory.getDefault().createID(
					container.getConnectNamespace(), connectID);
		} catch (final IDCreateException e) {
			new IDCreateErrorDialog(null, connectID, e).open();
			return false;
		}

		container.addListener(new IContainerListener() {
			public void handleEvent(IContainerEvent event) {
				if (event instanceof IContainerConnectedEvent) {
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							openPerspective();
						}
					});
				}
			}
		});

		new AsynchContainerConnectAction(container, targetID, connectContext,
				null, new Runnable() {
					public void run() {
						cachePassword(connectID, pass);
					}
				}).run();

		return true;
	}

	protected void cachePassword(final String connectID, String password) {
		if (password != null && !password.equals("")) { //$NON-NLS-1$
			final PasswordCacheHelper pwStorage = new PasswordCacheHelper(
					connectID);
			pwStorage.savePassword(password);
		}
	}
}
