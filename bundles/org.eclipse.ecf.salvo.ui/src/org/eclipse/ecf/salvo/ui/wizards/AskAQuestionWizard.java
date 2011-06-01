package org.eclipse.ecf.salvo.ui.wizards;

import org.eclipse.ecf.protocol.nntp.core.ServerStoreFactory;
import org.eclipse.ecf.protocol.nntp.model.INewsgroup;
import org.eclipse.ecf.protocol.nntp.model.IServerStoreFacade;
import org.eclipse.ecf.protocol.nntp.model.NNTPIOException;
import org.eclipse.ecf.protocol.nntp.model.StoreException;
import org.eclipse.ecf.protocol.nntp.model.UnexpectedResponseException;
import org.eclipse.ecf.salvo.ui.internal.wizards.ComposeNewArticleWizardPage;
import org.eclipse.ecf.salvo.ui.internal.wizards.SelectNewsgroupWizardPage;
import org.eclipse.ecf.salvo.ui.utils.ImageUtils;
import org.eclipse.ecf.salvo.ui.utils.PreferencesUtil;
import org.eclipse.jface.wizard.Wizard;

public class AskAQuestionWizard extends Wizard {

	private SelectNewsgroupWizardPage selectNewsgroupWizardPage;
	private ComposeNewArticleWizardPage composeNewArticleWizardPage;

	public AskAQuestionWizard() {
		super();
		setNeedsProgressMonitor(true);
		setWindowTitle("Ask a Question");
		setDefaultPageImageDescriptor(ImageUtils.getInstance()
				.getImageDescriptor("salvo.gif"));

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

		// Saving preferences
		PreferencesUtil.instance().savePluginSettings(
				"recentSelectedNewsgroup", group.getNewsgroupName());
		PreferencesUtil.instance().savePluginSettings("recentSelectedServer",
				group.getServer().getAddress());

		String subject = composeNewArticleWizardPage.getSubject();
		String body = composeNewArticleWizardPage.getBodyText();

		IServerStoreFacade serverStoreFacade = ServerStoreFactory.instance()
				.getServerStoreFacade();
		try {
			serverStoreFacade.postNewArticle(new INewsgroup[] { group },
					subject, body);
		} catch (NNTPIOException e) {
			e.printStackTrace();
		} catch (UnexpectedResponseException e) {
			e.printStackTrace();
		} catch (StoreException e) {
			e.printStackTrace();
		}

		return true;
	}

}
