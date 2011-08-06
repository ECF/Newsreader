/*******************************************************************************
 *  Copyright (c) 2011 University Of Moratuwa
 *                                                                      
 * All rights reserved. This program and the accompanying materials     
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at             
 * http://www.eclipse.org/legal/epl-v10.html                            
 *                                                                      
 * Contributors:                                                        
 *    Isuru Udana - UI Integration in the Workbench
 *******************************************************************************/
package org.eclipse.ecf.salvo.ui.internal.wizards;


import org.eclipse.ecf.salvo.ui.tools.ImageUtils;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

/**
 * This wizard page can be used to compose a new article.
 * 
 * @author isuru
 * 
 */
public class ComposeNewArticleWizardPage extends WizardPage {

	private Text bodyText;
	private Text subjectText;
	private Composite composite;
	private Button btnSubscribeToNewsgroup;

	public ComposeNewArticleWizardPage() {
		super("Compose Question");
		setTitle("Compose Question");
		setDescription("Compose your question");
		setImageDescriptor(ImageUtils.getInstance().getImageDescriptor(
				"composequestion.png"));
	}

	public void createControl(Composite parent) {

		composite = new Composite(parent, SWT.NULL);
		composite.setLayout(new GridLayout(1, false));

		// Subject
		{
			Composite group = new Composite(composite, SWT.NONE);
			group.setLayout(new GridLayout(2, false));
			group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
					1, 1));
			{
				Label lblSubject = new Label(group, SWT.NONE);
				lblSubject.setBounds(0, 0, 55, 15);
				lblSubject.setText("Subject:");
			}
			{
				subjectText = new Text(group, SWT.BORDER);
				subjectText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
						true, false, 1, 1));
				subjectText.addKeyListener(new KeyAdapter() {
					@Override
					public void keyReleased(KeyEvent e) {
						getWizard().getContainer().updateButtons();
					}
				});
			}
		}

		// Body
		{
			bodyText = new Text(composite, SWT.BORDER | SWT.MULTI | SWT.WRAP);
			bodyText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true,
					1, 1));

			bodyText.addKeyListener(new KeyAdapter() {

				@Override
				public void keyReleased(KeyEvent e) {
					getWizard().getContainer().updateButtons();
				}
			});
		}

		// checkbox 
		setControl(composite);
		{
			btnSubscribeToNewsgroup = new Button(composite, SWT.CHECK);
			btnSubscribeToNewsgroup.setText("Subscribe to Newsgroup after posting the question");
			btnSubscribeToNewsgroup.setSelection(true);
		}
		setPageComplete(false);

	}

	/**
	 * Check whether both subject and body is not empty
	 * 
	 * @return whether both subject and body is set
	 */
	public boolean isValuesSet() {
		if (!getBodyText().equals("") && !getSubject().equals("")) {
			return true;
		}
		return false;
	}

	/**
	 * Get the body of the article
	 * 
	 * @return body of the article
	 */
	public String getBodyText() {
		return bodyText.getText();
	}

	/**
	 * Get the subject of the article
	 * 
	 * @return subject of the article
	 */
	public String getSubject() {
		return subjectText.getText();
	}
	
	/**
	 * @return whether to subscribe to newsgroup
	 */
	public boolean doSubscribe() {
		return btnSubscribeToNewsgroup.getSelection();
	}
	

}
