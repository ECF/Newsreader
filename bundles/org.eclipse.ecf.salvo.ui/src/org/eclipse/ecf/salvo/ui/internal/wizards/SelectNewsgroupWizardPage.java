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

import java.util.ArrayList;

import org.eclipse.ecf.protocol.nntp.core.Debug;
import org.eclipse.ecf.protocol.nntp.core.ServerStoreFactory;
import org.eclipse.ecf.protocol.nntp.model.INewsgroup;
import org.eclipse.ecf.protocol.nntp.model.IServer;
import org.eclipse.ecf.protocol.nntp.model.NNTPException;
import org.eclipse.ecf.salvo.ui.external.provider.HookedNewsgroupProvider;
import org.eclipse.ecf.salvo.ui.external.provider.INewsGroupProvider;
import org.eclipse.ecf.salvo.ui.tools.ImageUtils;
import org.eclipse.ecf.salvo.ui.tools.PreferencesUtil;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;

public class SelectNewsgroupWizardPage extends WizardPage {

	private Composite container;
	private List newsgroupList;
	private Text searchBar;
	private ArrayList<INewsgroup> subscribedNewsgroups;
	private Button btnCheckPickSubscribed;
	private Combo cboSuggestedNewgroups;
	private INewsGroupProvider[] hookedNewsgroupProviders;
	private Label lblSuggested;

	public SelectNewsgroupWizardPage() {
		super("Select Newsgroup");
		setTitle("Select Newsgroup");
		setDescription("Select the Newsgroup you want to ask the question");
		initHookedNewsgroupsProviders();
		fetchSubscribedNewsgroups();
		setImageDescriptor(ImageUtils.getInstance().getImageDescriptor(
				"selectnewsgroup.png"));
	}

	public void createControl(Composite parent) {

		// Container
		container = new Composite(parent, SWT.NULL);
		container.setLayout(new GridLayout(1, false));
		setControl(container);

		if (isHookedNewsgroupsAvailable()) {
			// Label Suggested
			{
				lblSuggested = new Label(container, SWT.NULL);
				lblSuggested.setText("Suggested Newsgroups");
			}
			// Combobox of suggested newsgroups
			{
				cboSuggestedNewgroups = new Combo(container, SWT.READ_ONLY);
				cboSuggestedNewgroups.setLayoutData(new GridData(SWT.FILL,
						SWT.CENTER, true, false, 1, 1));
				fillCboSuggestedNewsgroups();
				cboSuggestedNewgroups.select(0);
			}
		}

		if (isHookedNewsgroupsAvailable() && isSubscribedNewsgroupsAvailable()) {
			// Checkbox
			{
				btnCheckPickSubscribed = new Button(container, SWT.CHECK);
				btnCheckPickSubscribed
						.addSelectionListener(new SelectionAdapter() {
							@Override
							public void widgetSelected(SelectionEvent e) {
								if (btnCheckPickSubscribed.getSelection()) {
									cboSuggestedNewgroups.setEnabled(false);
									lblSuggested.setEnabled(false);
									newsgroupList.setEnabled(true);
									searchBar.setEnabled(true);
								} else {
									cboSuggestedNewgroups.setEnabled(true);
									lblSuggested.setEnabled(true);
									newsgroupList.setEnabled(false);
									searchBar.setEnabled(false);
									searchBar.setText("");
									setPageComplete(true);
								}
							}
						});
				btnCheckPickSubscribed
						.setText("Pick from subscribed newsgroups");
			}
		}

		if (isSubscribedNewsgroupsAvailable()) {
			// Search bar
			{
				searchBar = new Text(container, SWT.BORDER);
				searchBar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
						true, false, 1, 1));
				searchBar.addKeyListener(new KeyAdapter() {
					@Override
					public void keyReleased(KeyEvent e) {
						setFilteredListItems();
						if (newsgroupList.getItemCount() == 0) {
							setPageComplete(false);
						} else {
							setPageComplete(true);
						}
					}
				});
			}
			// Newsgroup List
			{
				newsgroupList = new List(container, SWT.SINGLE | SWT.BORDER
						| SWT.V_SCROLL);
				newsgroupList.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
						true, true, 1, 1));
				initNewsgroupList();
			}
		}

		if (isHookedNewsgroupsAvailable() && isSubscribedNewsgroupsAvailable()) {
			cboSuggestedNewgroups.setEnabled(true);
			btnCheckPickSubscribed.setSelection(false);
			newsgroupList.setEnabled(false);
			searchBar.setEnabled(false);
		}

		// Page completeness
		if (!isHookedNewsgroupsAvailable()
				&& !isSubscribedNewsgroupsAvailable()) {
			lblSuggested = new Label(container, SWT.NULL);
			lblSuggested.setText("No Newsgroups available");
			setPageComplete(false);
		}

	}

	/**
	 * Fill CboSugesstedNewsgroups combo box
	 */
	private void fillCboSuggestedNewsgroups() {
		for (INewsGroupProvider newsgroupProvider : hookedNewsgroupProviders) {

			String newsgroupName = newsgroupProvider.getNewsgroupName();
			String serverAddress = newsgroupProvider.getServerAddress();
			String description = newsgroupProvider.getNewsgroupDescription();

			cboSuggestedNewgroups.add(newsgroupName + "  (Server: "
					+ serverAddress + ") -  " + description);
		}
	}

	/**
	 * Initialize NewsgroupList for the first time with all the available
	 * newsgroups
	 */
	private void initNewsgroupList() {

		// Load preferences
		String recentlySelectedNewsgroup = PreferencesUtil.instance()
				.loadPluginSettings("recentSelectedNewsgroup");
		String recentlySelectedServer = PreferencesUtil.instance()
				.loadPluginSettings("recentSelectedServer");

		int selectionIndex = 0;

		for (int i = 0, size = subscribedNewsgroups.size(); i < size; i++) {

			String newsgroupName = subscribedNewsgroups.get(i)
					.getNewsgroupName();
			String serverAddress = subscribedNewsgroups.get(i).getServer()
					.getAddress();

			newsgroupList.add(newsgroupName + "  (Server: " + serverAddress
					+ ")");

			// calculate the recently selected item from the list
			if (newsgroupName.equals(recentlySelectedNewsgroup)
					&& serverAddress.equals(recentlySelectedServer)) {
				selectionIndex = i;
			}

		}
		newsgroupList.select(selectionIndex);
	}

	/**
	 * Fill the newsgroupList according to the filter specified on the search
	 * bar
	 */
	private void setFilteredListItems() {
		newsgroupList.removeAll();

		for (INewsgroup newsgroup : subscribedNewsgroups) {
			if (matchPattern(newsgroup.getNewsgroupName())) {
				String newsgroupName = newsgroup.getNewsgroupName();
				String serverAddress = newsgroup.getServer().getAddress();

				newsgroupList.add(newsgroupName + "  (Server: " + serverAddress
						+ ")");
			}
		}
		newsgroupList.select(0);
	}

	/**
	 * Match whether the given newsgroup is match with the filter.
	 * 
	 * @param newsgroupName
	 *            Name of the newsgroup
	 * @return whether the newsgroup is match with the filter
	 */
	private boolean matchPattern(String newsgroupName) {

		String searchText = searchBar.getText();
		if (newsgroupName.contains(searchText)) {
			if (newsgroupName.startsWith(searchText)) {
				return true;
			} else if (newsgroupName.contains("." + searchText)) {
				return true;
			} else if (newsgroupName.contains("-" + searchText)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Get the selected Newsgroup from the list
	 * 
	 * @return selected Newsgroup
	 */
	public INewsgroup getSelectedNewsgroup() {

		INewsgroup resultNewsgroup = null;

		if (isHookedNewsgroupsAvailable() && isSubscribedNewsgroupsAvailable()) {
			if (btnCheckPickSubscribed.getSelection()) {
				resultNewsgroup = getSelectedSubscribedNewsgroup();
			} else {
				resultNewsgroup = getSelectedHookedNewsgroup();
			}
		} else if (isHookedNewsgroupsAvailable()) {
			resultNewsgroup = getSelectedHookedNewsgroup();
		} else {
			resultNewsgroup = getSelectedSubscribedNewsgroup();
		}

		return resultNewsgroup;
	}

	/**
	 * Get the selected hooked newsgroup
	 */
	private INewsgroup getSelectedHookedNewsgroup() {
		INewsgroup resultNewsgroup = null;

		INewsGroupProvider provider = hookedNewsgroupProviders[cboSuggestedNewgroups
				.getSelectionIndex()];
		if (!HookedNewsgroupProvider.instance().isServerSubscribed(provider)) {
			if (provider.initCredentials()) {
				resultNewsgroup = HookedNewsgroupProvider.instance()
						.getNewsgroup(provider);
			}
		} else {
			resultNewsgroup = HookedNewsgroupProvider.instance().getNewsgroup(
					provider);
		}
		return resultNewsgroup;
	}

	/**
	 * Get the selected subscribed newsgroup
	 */
	private INewsgroup getSelectedSubscribedNewsgroup() {
		INewsgroup resultNewsgroup = null;

		try {
			String selectedNewsgroupString = newsgroupList.getItem(
					newsgroupList.getSelectionIndex()).replace(")", "");

			String selectedNewsgroup = selectedNewsgroupString.split("\\(")[0]
					.trim();
			String selectedServer = selectedNewsgroupString.split("\\(")[1]
					.replace("Server: ", "").trim();

			for (INewsgroup newsgroup : subscribedNewsgroups) {
				if (newsgroup.getNewsgroupName().equals(selectedNewsgroup)
						&& newsgroup.getServer().getAddress()
								.equals(selectedServer)) {
					resultNewsgroup = newsgroup;
				}
			}
		} catch (Exception e) {
			// No newsgroups
		}
		return resultNewsgroup;
	}

	/**
	 * Fetch all subscribed newsgroups from the store
	 */
	private void fetchSubscribedNewsgroups() {

		subscribedNewsgroups = new ArrayList<INewsgroup>();

		try {
			for (IServer server : ServerStoreFactory.instance()
					.getServerStoreFacade().getFirstStore().getServers()) {

				INewsgroup[] groups = ServerStoreFactory.instance()
						.getServerStoreFacade().getSubscribedNewsgroups(server);

				for (INewsgroup group : groups) {

					if (!subscribedNewsgroups.contains(group)) {
						subscribedNewsgroups.add(group);
					}
				}

			}
		} catch (NNTPException e) {
			Debug.log(this.getClass(), e);
		}

	}

	/**
	 * initialize hooked newsgroup providers
	 */
	private void initHookedNewsgroupsProviders() {
		hookedNewsgroupProviders = HookedNewsgroupProvider.instance()
				.getProviders();
	}

	/**
	 * check whether subscribed newsgroups available
	 */
	private boolean isSubscribedNewsgroupsAvailable() {
		if (subscribedNewsgroups.size() == 0) {
			return false;
		}
		return true;
	}

	/**
	 * check whether hooked newsgroups available
	 */
	private boolean isHookedNewsgroupsAvailable() {
		if (hookedNewsgroupProviders.length == 0) {
			return false;
		}
		return true;
	}

}
