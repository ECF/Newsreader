/*******************************************************************************
 *  Copyright (c) 2010 Weltevree Beheer BV, Remain Software & Industrial-TSI
 *                                                                      
 * All rights reserved. This program and the accompanying materials     
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at             
 * http://www.eclipse.org/legal/epl-v10.html                            
 *                                                                      
 * Contributors:                                                        
 *    Wim Jongman - initial API and implementation
 *******************************************************************************/
package org.eclipse.ecf.salvo.ui.internal.wizards;

import org.eclipse.ecf.protocol.nntp.core.Debug;
import org.eclipse.ecf.protocol.nntp.core.ServerFactory;
import org.eclipse.ecf.protocol.nntp.model.AbstractCredentials;
import org.eclipse.ecf.protocol.nntp.model.IServer;
import org.eclipse.ecf.protocol.nntp.model.IServerConnection;
import org.eclipse.ecf.protocol.nntp.model.NNTPException;
import org.eclipse.ecf.provider.nntp.security.SalvoUtil;
import org.eclipse.ecf.salvo.ui.wizards.NewNewsServerWizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ProgressBar;

public class NewNewsServerWizardPage extends WizardPage {

	private Text pass;

	private Text user;

	private Text login;

	private Text email;

	private Text address;

	private Text port;

	// private boolean isPageComplete = false;
	private Button requiresLogOnButton;

	private Button validateButton;

	private boolean serverValidated;

	private Label logInLabel;

	private Label passwordLabel;
	private ProgressBar progressBar;

	public NewNewsServerWizardPage(String pageName) {
		super(pageName);
		setTitle("New Server");
		setDescription("Enter the news server information and press Validate.");
	}

	public void createControl(Composite parent) {

		ModifyListener editor = new NewNewsServerWizardPageEditor(this);

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		composite.setLayout(new GridLayout(2, false));

		Label label = new Label(composite, SWT.NONE);
		label.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false,
				false));
		label.setText("News Server Address");

		address = new Text(composite, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
		address.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		address.setText("news.eclipse.org");

		Label label2 = new Label(composite, SWT.NONE);
		label2.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false,
				false));
		label2.setText("Port");

		port = new Text(composite, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
		port.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		port.setText("119");

		Label label21 = new Label(composite, SWT.NONE);
		label21.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false,
				false));
		label21.setText("User Name");

		user = new Text(composite, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
		user.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		user.setText("First Last");

		Label label22 = new Label(composite, SWT.NONE);
		label22.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false,
				false));
		label22.setText("Email Address");

		email = new Text(composite, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
		email.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		email.setText("your email");

		setControl(composite);

		requiresLogOnButton = new Button(composite, SWT.CHECK);
		requiresLogOnButton.setSelection(false);
		new Label(composite, SWT.NONE);

		final Composite composite_1 = new Composite(composite, SWT.NONE);
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 2, 1));
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		composite_1.setLayout(gridLayout);

		logInLabel = new Label(composite_1, SWT.NONE);
		logInLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false));
		logInLabel.setText("Login");

		login = new Text(composite_1, SWT.BORDER);
		login.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		login.setText("exquisitus");
		login.setSelection(new Point(0, 9));
		login.setEnabled(false);

		passwordLabel = new Label(composite_1, SWT.NONE);
		passwordLabel.setText("Password");

		pass = new Text(composite_1, SWT.BORDER);
		pass.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		pass.setEchoChar('*');
		pass.setText("flinder1f7");
		pass.setEnabled(false);

		requiresLogOnButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				setLoginEnabled(requiresLogOnButton.getSelection());
			}
		});
		requiresLogOnButton.setText("Requires log on");

		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);

		validateButton = new Button(composite, SWT.NONE);
		validateButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(final MouseEvent e) {
				progressBar.setVisible(true);
				validateServer();
				progressBar.setVisible(false);
			}
		});
		validateButton.setSelection(true);
		validateButton.setText("Validate");

		progressBar = new ProgressBar(composite, SWT.SMOOTH | SWT.INDETERMINATE);
		progressBar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		progressBar.setVisible(false);

		fillDialog();

		address.addModifyListener(editor);
		port.addModifyListener(editor);
		user.addModifyListener(editor);
		email.addModifyListener(editor);
		login.addModifyListener(editor);
		pass.addModifyListener(editor);
	}

	protected void setLoginEnabled(boolean selection) {
		logInLabel.setEnabled(selection);
		login.setEnabled(selection);
		passwordLabel.setEnabled(selection);
		pass.setEnabled(selection);

	}

	private void fillDialog() {
		IServer server = ((NewNewsServerWizard) getWizard()).getServer();
		if (server == null) {
			return;
		}
		address.setText(server.getAddress());
		port.setText(server.getPort() + "");
		user.setText(server.getServerConnection().getUser());
		email.setText(server.getServerConnection().getEmail());
		requiresLogOnButton.setSelection(!server.isAnonymous());
		setLoginEnabled(!server.isAnonymous());
		if (!server.isAnonymous()) {
			login.setText(server.getServerConnection().getLogin());
			pass.setText(SalvoUtil.getPassword(server.getAddress()));
		}
	}

	protected void validateServer() {
		// If everything is ok then try to connect to the server if this
		// is required.

		final StringBuffer message = new StringBuffer();
		final AbstractCredentials credentials = new AbstractCredentials(
				getUser(), getEmail(), getLogin(), getPass());
		final String v_address = getAddress();
		final int v_port = getPort();
		final boolean v_secure = isSecure();

		BusyIndicator.showWhile(null, new Runnable() {

			public void run() {

				setServerValidated(false);

				Runnable runner = new Runnable() {

					public void run() {
						setErrorMessage(null);

						try {
							IServer server = ServerFactory.getCreateServer(
									v_address, v_port, credentials, v_secure);
							IServerConnection connection = server
									.getServerConnection();
							connection.disconnect();
							connection.connect();
							connection.setModeReader(server);
							connection.getOverviewHeaders(server);
						} catch (NNTPException e) {
							message.append(e.getMessage());
							Debug.log(getClass(), e);
						}

					}
				};

				Thread thread = new Thread(runner,
						"Salvo Server validation Thread");
				thread.start();

				while (thread.isAlive())
					while (Display.getCurrent().readAndDispatch()
							&& thread.isAlive())
						Display.getCurrent().sleep();

				if (message.toString().length() > 0)
					setErrorMessage(message.toString());
				else
					setServerValidated(true);

				return;

			}
		});
	}

	public String getEmail() {
		return email.getText();
	}

	public void setServerValidated(boolean b) {
		serverValidated = b;
		getWizard().getContainer().updateButtons();
	}

	@Override
	public boolean isPageComplete() {
		return serverValidated;
	}

	public String getAddress() {
		return address.getText();
	}

	public int getPort() {
		return Integer.valueOf(port.getText());
	}

	public String getUser() {
		return user.getText();
	}

	public String getLogin() {
		if (requiresLogOnButton.getSelection()) {
			return login.getText();
		}
		return null;
	}

	public boolean isLogonRequired() {
		return requiresLogOnButton.getSelection();
	}

	public boolean isValidateRequired() {
		return validateButton.getSelection();
	}

	public String getPass() {
		if (requiresLogOnButton.getSelection()) {
			return pass.getText();
		}
		return null;
	}

	public IServer getServer() throws NNTPException {
		AbstractCredentials credentials = new AbstractCredentials(getUser(),
				getEmail(), getLogin(), getPass());
		return ServerFactory.getCreateServer(getAddress(), getPort(),
				credentials, isSecure());
	}

	public boolean isSecure() {
		// TODO Auto-generated method stub
		return false;
	}
}
