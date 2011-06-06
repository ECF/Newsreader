package org.eclipse.ecf.salvo.ui.internal.wizards;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class ComposeNewArticleWizardPage extends WizardPage {

	private Text bodyText;
	private Text subjectText;
	private Composite composite;

	public ComposeNewArticleWizardPage() {
		super("Compose Question");
		setTitle("Compose Question");
		setDescription("Compose your question");
		
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

		setControl(composite);
		setPageComplete(false);

	}
		
	public boolean isValuesSet() {
		if(!getBodyText().equals("") && !getSubject().equals("")){
			return true;
		}
		return false;
		
	}
	
	
	public String getBodyText(){
		return bodyText.getText();
	}
	
	public String getSubject() {
		return subjectText.getText();
	}
	
	
}
