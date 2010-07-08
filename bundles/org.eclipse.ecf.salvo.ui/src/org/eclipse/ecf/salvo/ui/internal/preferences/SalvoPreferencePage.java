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
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class SalvoPreferencePage extends PreferencePage implements
		IWorkbenchPreferencePage {
	private DataBindingContext m_bindingContext;
	private Button btnMoreArticleViews;

	private PreferenceModel model = PreferenceModel.instance;
	private Button btnCreateNewArticles;

	public SalvoPreferencePage() {
	}

	@Override
	protected Control createContents(Composite parent) {

		Composite drawing = new Composite(parent, SWT.None);
		drawing.setLayout(new GridLayout(1, false));

		btnMoreArticleViews = new Button(drawing, SWT.CHECK);
		btnMoreArticleViews.setText("More Article Views");
		
		btnCreateNewArticles = new Button(drawing, SWT.CHECK);
		btnCreateNewArticles.setText("Create new Articles in a Detached View");
		
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
		IObservableValue btnMoreArticleViewsObserveSelectionObserveWidget = SWTObservables.observeSelection(btnMoreArticleViews);
		IObservableValue modelViewPerGroupObserveValue = PojoObservables.observeValue(model, "viewPerGroup");
		bindingContext.bindValue(btnMoreArticleViewsObserveSelectionObserveWidget, modelViewPerGroupObserveValue, null, null);
		//
		IObservableValue btnCreateNewArticlesObserveSelectionObserveWidget = SWTObservables.observeSelection(btnCreateNewArticles);
		IObservableValue modelUseDetachedViewObserveValue = PojoObservables.observeValue(model, "useDetachedView");
		bindingContext.bindValue(btnCreateNewArticlesObserveSelectionObserveWidget, modelUseDetachedViewObserveValue, null, null);
		//
		return bindingContext;
	}
}
