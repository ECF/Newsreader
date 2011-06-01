package org.eclipse.ecf.salvo.ui.internal.wizards;

import java.util.ArrayList;

import org.eclipse.ecf.protocol.nntp.core.ServerStoreFactory;
import org.eclipse.ecf.protocol.nntp.model.INewsgroup;
import org.eclipse.ecf.protocol.nntp.model.IServer;
import org.eclipse.ecf.protocol.nntp.model.NNTPException;
import org.eclipse.ecf.salvo.ui.utils.PreferencesUtil;
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
		getAllNewsgroups();
		
	}


	public void createControl(Composite parent) {
		// Container
		container = new Composite(parent, SWT.NULL);
		container.setLayout(null);
		
		// Newsgroup List
		{
			newsgroupList = new List(container, SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL);
			newsgroupList.setBounds(48, 35, 400, 150);
		
			// Load preferences 
			String recentlySelectedNewsgroup = PreferencesUtil.instance().loadPluginSettings("recentSelectedNewsgroup");
			String recentlySelectedServer = PreferencesUtil.instance().loadPluginSettings("recentSelectedServer");
			
			int selectionIndex = 0;
			
			for (int i=0,size = newsgroups.size(); i<size;i++){
				
				String newsgroupName = newsgroups.get(i).getNewsgroupName();
				String serverAddress = newsgroups.get(i).getServer().getAddress();
				
				newsgroupList.add(newsgroupName+ "   (Server : "+ serverAddress+")");
				
				// calculate the recently selected item from the list
				if (newsgroupName.equals(recentlySelectedNewsgroup) && serverAddress.equals(recentlySelectedServer)){
					selectionIndex = i;
				}
				
			}
			
			newsgroupList.select(selectionIndex);
			
		}
				
		setControl(container);
		setPageComplete(true);
	}
	
	public INewsgroup getSelectedNewsgroup() {
		return newsgroups.get(newsgroupList.getSelectionIndex());
	}
	
	
	private void getAllNewsgroups(){
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
	
	}
	
		

}
