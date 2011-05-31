package org.eclipse.ecf.salvo.ui.internal.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ecf.salvo.ui.wizards.AskAQuestionWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.handlers.HandlerUtil;

public class AskAquestionHandler extends AbstractHandler{

	
	public Object execute(ExecutionEvent event) throws ExecutionException {
		AskAQuestionWizard wizard = new AskAQuestionWizard();
		WizardDialog dialog = new WizardDialog(HandlerUtil
				.getActiveShell(event), wizard);
		dialog.open();
		return null;
	}
		
}
