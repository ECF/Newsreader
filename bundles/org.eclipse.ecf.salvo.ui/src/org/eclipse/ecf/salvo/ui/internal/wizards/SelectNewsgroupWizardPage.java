package org.eclipse.ecf.salvo.ui.internal.wizards;

import java.util.ArrayList;

import org.eclipse.ecf.protocol.nntp.core.ServerStoreFactory;
import org.eclipse.ecf.protocol.nntp.model.INewsgroup;
import org.eclipse.ecf.protocol.nntp.model.IServer;
import org.eclipse.ecf.protocol.nntp.model.NNTPException;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;

public class SelectNewsgroupWizardPage extends WizardPage{

	
	private Composite container;
	private List newsgroupList;
	private ArrayList<INewsgroup> newsgroups;
	
	public SelectNewsgroupWizardPage() {
		super("Select Newsgroup");
		setTitle("Select Newsgroup");
		setDescription("Select the Newsgroup you want to ask the question");

		
	}


	public void createControl(Composite parent) {
		// Container
		container = new Composite(parent, SWT.NULL);
		container.setLayout(null);
		
		// Newsgroup List
		{
			newsgroupList = new List(container, SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL);
			newsgroupList.setBounds(48, 35, 400, 150);
		
			// Get all newsgroups
			for (INewsgroup newsgroup : getAllNewsgroups()) {
				newsgroupList.add(newsgroup.getNewsgroupName()+ "   (Server : "+ newsgroup.getServer()+")");	
			}

		}
				
		setControl(container);
		setPageComplete(true);
	}
	
	public INewsgroup getSelectedNewsgroup() {
		return newsgroups.get(newsgroupList.getSelectionIndex());
	}
	
	
	private  ArrayList<INewsgroup> getAllNewsgroups(){
		newsgroups = new ArrayList<INewsgroup>();
		
		try {
			for (IServer server : ServerStoreFactory.instance()
					.getServerStoreFacade().getFirstStore()
					.getServers()) {
				
				INewsgroup[] groups = ServerStoreFactory.instance().getServerStoreFacade()
				.getSubscribedNewsgroups(server);
				
				for (INewsgroup group : groups){
					newsgroups.add(group);
				}
				
			}
		} catch (NNTPException e) {
			e.printStackTrace();
		}
		
		return newsgroups;
	}
	
		

}
