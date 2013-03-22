package org.eclipse.ecf.salvo.application.internal;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class PureE4 {
	@Inject
	public PureE4() {
		// TODO Your code here
	}

	@PostConstruct
	public void postConstruct(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false,
				false));
		button.setText("Pushe me!");
	}
}