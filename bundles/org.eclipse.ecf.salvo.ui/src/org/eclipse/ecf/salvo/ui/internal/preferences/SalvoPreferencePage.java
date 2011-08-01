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
import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.ecf.protocol.nntp.core.Debug;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

public class SalvoPreferencePage extends PreferencePage implements
		IWorkbenchPreferencePage {
	private DataBindingContext m_bindingContext;
	private Button btnMoreArticleViews;

	private PreferenceModel model = PreferenceModel.instance;
	private Button btnCreateNewArticles;
	private Group grpSynchronization;
	private Group grpArticleView;
	private Text txtTimeInterval;

	public SalvoPreferencePage() {
	}

	@Override
	protected Control createContents(Composite parent) {

		Composite drawing = new Composite(parent, SWT.None);
		drawing.setLayout(new GridLayout(1, false));

		GridLayout gridLayout = new GridLayout(1, false);
		{
			grpArticleView = new Group(drawing, SWT.NONE);
			grpArticleView.setLayout(gridLayout);
			grpArticleView
					.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			grpArticleView.setText("Article Views");

			{
				btnMoreArticleViews = new Button(grpArticleView, SWT.CHECK);
				btnMoreArticleViews.setText("More Article Views");

				btnCreateNewArticles = new Button(grpArticleView, SWT.CHECK);
				btnCreateNewArticles
						.setText("Create new Articles in a Detached View");
			}
		}
		new Label(drawing, SWT.NONE);

		{

			grpSynchronization = new Group(drawing, SWT.NONE);
			gridLayout = new GridLayout(3, false);
			grpSynchronization.setLayout(gridLayout);
			grpSynchronization.setLayoutData(new GridData(
					GridData.FILL_HORIZONTAL));
			grpSynchronization.setText("Synchronization");

			{
				Label lblSyncTimeInterval = new Label(grpSynchronization,
						SWT.NONE);
				lblSyncTimeInterval.setText("Sync. TIme Interval");

				txtTimeInterval = new Text(grpSynchronization, SWT.BORDER);

				final Preferences prefs = new InstanceScope()
						.getNode("org.eclipse.ecf.protocol.nntp.core");

				txtTimeInterval.setText(Integer.toString(prefs.getInt(
						"syncinterval", 300)));
				GridData gridData = new GridData();
				gridData.widthHint = 100;
				txtTimeInterval.setLayoutData(gridData);

				txtTimeInterval.addKeyListener(new KeyListener() {

					public void keyReleased(KeyEvent arg0) {

						try {
							if (!txtTimeInterval.getText().equals("")) {
								prefs.putInt("syncinterval", Integer
										.parseInt(txtTimeInterval.getText()));
								prefs.flush();
							}
						} catch (Exception e) {
							Debug.log(this.getClass(), e);
						}

					}

					public void keyPressed(KeyEvent arg0) {
					}
				});

				Label lblSyncTimeIntervalTail = new Label(grpSynchronization,
						SWT.NONE);
				lblSyncTimeIntervalTail.setText("sec.");

			}
		}

		m_bindingContext = initDataBindings();
		m_bindingContext.updateTargets();

		return drawing;
	}

	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub

	}

	public void setMoreArticleViews() {

	}

	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue btnMoreArticleViewsObserveSelectionObserveWidget = SWTObservables
				.observeSelection(btnMoreArticleViews);
		IObservableValue modelViewPerGroupObserveValue = PojoObservables
				.observeValue(model, "viewPerGroup");
		bindingContext.bindValue(
				btnMoreArticleViewsObserveSelectionObserveWidget,
				modelViewPerGroupObserveValue, null, null);
		//
		IObservableValue btnCreateNewArticlesObserveSelectionObserveWidget = SWTObservables
				.observeSelection(btnCreateNewArticles);
		IObservableValue modelUseDetachedViewObserveValue = PojoObservables
				.observeValue(model, "useDetachedView");
		bindingContext.bindValue(
				btnCreateNewArticlesObserveSelectionObserveWidget,
				modelUseDetachedViewObserveValue, null, null);
		//
		return bindingContext;
	}
}
