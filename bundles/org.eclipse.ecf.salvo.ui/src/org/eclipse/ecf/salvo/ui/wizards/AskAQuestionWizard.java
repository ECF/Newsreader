package org.eclipse.ecf.salvo.ui.wizards;

import org.eclipse.ecf.protocol.nntp.core.ServerStoreFactory;
import org.eclipse.ecf.protocol.nntp.model.INewsgroup;
import org.eclipse.ecf.protocol.nntp.model.IServerStoreFacade;
import org.eclipse.ecf.protocol.nntp.model.NNTPIOException;
import org.eclipse.ecf.protocol.nntp.model.StoreException;
import org.eclipse.ecf.protocol.nntp.model.UnexpectedResponseException;
import org.eclipse.ecf.salvo.ui.internal.wizards.ComposeNewArticleWizardPage;
import org.eclipse.ecf.salvo.ui.internal.wizards.SelectNewsgroupWizardPage;
import org.eclipse.jface.wizard.Wizard;

public class AskAQuestionWizard extends Wizard{

	private SelectNewsgroupWizardPage selectNewsgroupWizardPage;
	private ComposeNewArticleWizardPage composeNewArticleWizardPage;
	
	
	public AskAQuestionWizard() {
		super();
		setNeedsProgressMonitor(true);
		setWindowTitle("Ask a Question");
	}

	@Override
	public void addPages() {
		selectNewsgroupWizardPage = new SelectNewsgroupWizardPage();
		composeNewArticleWizardPage = new ComposeNewArticleWizardPage();
		addPage(selectNewsgroupWizardPage);
		addPage(composeNewArticleWizardPage);
		
	}
	
	@Override
	public boolean performFinish() {
		
		INewsgroup group = selectNewsgroupWizardPage.getSelectedNewsgroup();
		String subject = composeNewArticleWizardPage.getSubject();
		String body = composeNewArticleWizardPage.getBodyText();
				
		IServerStoreFacade serverStoreFacade = ServerStoreFactory.instance().getServerStoreFacade(); 
		  try {
			serverStoreFacade.postNewArticle(new INewsgroup[] { group }, subject, body);
			System.out.println("Article POSTED to "+group.getNewsgroupName());
		} catch (NNTPIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnexpectedResponseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (StoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return true;
	}

}
