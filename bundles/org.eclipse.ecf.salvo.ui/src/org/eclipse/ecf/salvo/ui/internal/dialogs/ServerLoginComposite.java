package org.eclipse.ecf.salvo.ui.internal.dialogs;

import org.eclipse.ecf.protocol.nntp.model.IServer;
import org.eclipse.ecf.provider.nntp.security.SalvoUtil;
import org.eclipse.swt.SWT;
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
import org.eclipse.ui.forms.widgets.FormToolkit;

public class ServerLoginComposite extends Composite {

	private final FormToolkit toolkit = new FormToolkit(Display.getCurrent());
	private Text address;
	private Text port;
	private Text user;
	private Text email;
	private Button requiresLogOnButton;
	private Label logInLabel;
	private Text login;
	private Label passwordLabel;
	private Text pass;
	private Button validateButton;
	private final IServer server;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public ServerLoginComposite(Composite parent, int style, IServer server) {

		super(parent, style);
		this.server = server;

		toolkit.adapt(this);
		toolkit.paintBordersFor(this);

		// ModifyListener editor = new NewNewsServerWizardPageEditor(this);

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

			}
		});
		validateButton.setLayoutData(new GridData());
		validateButton.setSelection(true);
		validateButton.setText("Validate");
		new Label(composite, SWT.NONE);

		fillDialog();

		// address.addModifyListener(editor);
		// port.addModifyListener(editor);
		// user.addModifyListener(editor);
		// email.addModifyListener(editor);
		// login.addModifyListener(editor);
		// pass.addModifyListener(editor);

	}

	private void fillDialog() {

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
			pass.setText(SalvoUtil.getPassword(address.getText()));
		}
	}

	public void createControl(Composite parent) {
		// TODO Auto-generated method stub

	}

	public void setTitle(String title) {
		// TODO Auto-generated method stub

	}

	protected void setLoginEnabled(boolean selection) {
		logInLabel.setEnabled(selection);
		login.setEnabled(selection);
		passwordLabel.setEnabled(selection);
		pass.setEnabled(selection);

	}

}
