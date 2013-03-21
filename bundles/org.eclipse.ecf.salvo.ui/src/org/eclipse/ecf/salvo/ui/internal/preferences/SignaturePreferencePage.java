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
package org.eclipse.ecf.salvo.ui.internal.preferences;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.ecf.protocol.nntp.core.Debug;
import org.eclipse.ecf.salvo.ui.internal.Activator;
import org.eclipse.ecf.services.quotes.QuoteService;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

public class SignaturePreferencePage extends PreferencePage implements
		IWorkbenchPreferencePage {
	private DataBindingContext m_bindingContext;

	private static class ServicesContentProvider implements
			IStructuredContentProvider {
		private Object[] input;

		public Object[] getElements(Object inputElement) {
			return input;
		}

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			input = (Object[]) newInput;
		}
	}

	private Text signature;

	private SignaturePreferencePage pref = this;

	private PreferenceModel model = PreferenceModel.instance;

	private Button btnUseSignature;

	private Group grpQuoteServices;

	private Composite drawing;

	private Composite composite;
	private Button button;
	private Button button_1;

	public SignaturePreferencePage() {
		// TODO Auto-generated constructor stub
	}

	public SignaturePreferencePage(String title) {
		super(title);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @wbp.parser.constructor
	 */
	public SignaturePreferencePage(String title, ImageDescriptor image) {
		super(title, image);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite drawing = new Composite(parent, SWT.None);
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.horizontalSpacing = 0;
		gridLayout.verticalSpacing = 0;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		drawing.setLayout(gridLayout);

		composite = new Composite(drawing, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1,
				1));
		GridLayout gridLayout_1 = new GridLayout(1, false);
		gridLayout_1.verticalSpacing = 0;
		gridLayout_1.marginWidth = 0;
		gridLayout_1.horizontalSpacing = 0;
		gridLayout_1.marginHeight = 0;
		composite.setLayout(gridLayout_1);

		btnUseSignature = new Button(composite, SWT.CHECK);
		btnUseSignature.setText("Use Signature");

		signature = new Text(composite, SWT.BORDER | SWT.MULTI);
		signature.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1,
				1));

		grpQuoteServices = new Group(drawing, SWT.NONE);
		grpQuoteServices.setLayout(new GridLayout(1, false));
		grpQuoteServices.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				true, 1, 1));
		grpQuoteServices.setSize(355, 113);
		grpQuoteServices.setText("Quote Services");

		m_bindingContext = initDataBindings();
		initTables();
		m_bindingContext.updateTargets();
		m_bindingContext.updateModels();
		// btnUseSignature.setSelection(model.getUseSignature());
		// signature.setText(PreferenceModel.instance.getSignature());

		return drawing;
	}

	private void initTables() {
		try {
			ServiceReference[] x = Activator
					.getDefault()
					.getBundleContext()
					.getAllServiceReferences(QuoteService.class.getName(), null);
			if (x != null)
				for (int i = 0; i < x.length; i++) {
					Button b = new Button(grpQuoteServices, SWT.RADIO);
					b.setText(x[i].getProperty("component.name").toString());
					b.setSelection(model.getQuoteService().equals(b.getText()));

					b.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(SelectionEvent e) {
							if (((Button) e.widget).getSelection())
								model.setQuoteService(((Button) e.widget)
										.getText());
							else
								model.setQuoteService("");
						}
					});
				}

		} catch (InvalidSyntaxException e) {
			Debug.log(getClass(), e);
		}
	}

	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub

	}

	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue signatureObserveTextObserveWidget = SWTObservables
				.observeText(signature, SWT.Modify);
		IObservableValue modelSignatureObserveValue = PojoObservables
				.observeValue(model, "signature");
		bindingContext.bindValue(signatureObserveTextObserveWidget,
				modelSignatureObserveValue, null, null);
		//
		IObservableValue btnUseSignatureObserveSelectionObserveWidget_7 = SWTObservables
				.observeSelection(btnUseSignature);
		IObservableValue signatureEnabledObserveValue = PojoObservables
				.observeValue(signature, "enabled");
		bindingContext.bindValue(
				btnUseSignatureObserveSelectionObserveWidget_7,
				signatureEnabledObserveValue, null, new UpdateValueStrategy(
						UpdateValueStrategy.POLICY_NEVER));
		//
		IObservableValue btnUseSignatureObserveSelectionObserveWidget_1 = SWTObservables
				.observeSelection(btnUseSignature);
		IObservableValue modelUseSignatureObserveValue = PojoObservables
				.observeValue(model, "useSignature");
		bindingContext.bindValue(
				btnUseSignatureObserveSelectionObserveWidget_1,
				modelUseSignatureObserveValue, null, null);
		//
		return bindingContext;
	}
}
