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
package org.eclipse.ecf.salvo.ui.properties;


public class SalvoPropertyPage {
	
//	extends PropertyPage {
//}
//
//	private static final String PATH_TITLE = "Path:";
//	private static final String OWNER_TITLE = "&Owner:";
////	private static final String OWNER_PROPERTY = "OWNER";
//	private static final String DEFAULT_OWNER = "John Doe";
//
//	private static final int TEXT_FIELD_WIDTH = 50;
//
//	private Text ownerText;
//
//	/**
//	 * Constructor for SamplePropertyPage.
//	 */
//	public SalvoPropertyPage() {
//		super();
//	}
//
//	private void addFirstSection(Composite parent) {
//		Composite composite = createDefaultComposite(parent);
//
//		//Label for path field
//		Label pathLabel = new Label(composite, SWT.NONE);
//		pathLabel.setText(PATH_TITLE);
//
//		// Path text field
//		Text pathValueText = new Text(composite, SWT.WRAP | SWT.READ_ONLY);
//		pathValueText.setText("Value goes here");
//	}
//
//	private void addSeparator(Composite parent) {
//		Label separator = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
//		GridData gridData = new GridData();
//		gridData.horizontalAlignment = GridData.FILL;
//		gridData.grabExcessHorizontalSpace = true;
//		separator.setLayoutData(gridData);
//	}
//
//	private void addSecondSection(Composite parent) {
//		Composite composite = createDefaultComposite(parent);
//
//		// Label for owner field
//		Label ownerLabel = new Label(composite, SWT.NONE);
//		ownerLabel.setText(OWNER_TITLE);
//
//		// Owner text field
//		ownerText = new Text(composite, SWT.SINGLE | SWT.BORDER);
//		GridData gd = new GridData();
//		gd.widthHint = convertWidthInCharsToPixels(TEXT_FIELD_WIDTH);
//		ownerText.setLayoutData(gd);
//
//		// Populate owner text field
//			String owner = null;
//			ownerText.setText((owner != null) ? owner : DEFAULT_OWNER);
//	}
//
//	/**
//	 * @see PreferencePage#createContents(Composite)
//	 */
//	@Override
//	protected Control createContents(Composite parent) {
//		Composite composite = new Composite(parent, SWT.NONE);
//		GridLayout layout = new GridLayout();
//		composite.setLayout(layout);
//		GridData data = new GridData(GridData.FILL);
//		data.grabExcessHorizontalSpace = true;
//		composite.setLayoutData(data);
//
//		addFirstSection(composite);
//		addSeparator(composite);
//		addSecondSection(composite);
//		return composite;
//	}
//
//	private Composite createDefaultComposite(Composite parent) {
//		Composite composite = new Composite(parent, SWT.NULL);
//		GridLayout layout = new GridLayout();
//		layout.numColumns = 2;
//		composite.setLayout(layout);
//
//		GridData data = new GridData();
//		data.verticalAlignment = GridData.FILL;
//		data.horizontalAlignment = GridData.FILL;
//		composite.setLayoutData(data);
//
//		return composite;
//	}
//
//	@Override
//	protected void performDefaults() {
//		// Populate the owner text field with the default value
//		ownerText.setText(DEFAULT_OWNER);
//	}
//	
//	@Override
//	public boolean performOk() {
//		// TODO store the value in the owner text field
//		
//		return true;
//	}

}