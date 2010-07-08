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
package org.eclipse.ecf.salvo.ui.internal.editor;

import org.eclipse.ecf.protocol.nntp.core.Debug;
import org.eclipse.ecf.protocol.nntp.model.IArticle;
import org.eclipse.ecf.salvo.ui.internal.MimeArticleContentHandler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;


public class ArticleWidgetBuilder {

	public static void build(Composite composite, IArticle article, MimeArticleContentHandler handler) {

		String contentType = article.getHeaderAttributeValue("Content-Type");
		Debug.log(ArticleWidgetBuilder.class, "building widget for " + contentType);

		if (contentType == null) {
			contentType = "unknown to SALVO";
		}

		if (contentType.startsWith("text/html")) {
			Browser browser = new Browser(composite, SWT.V_SCROLL);
			browser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			browser.setText(handler.getBody());
		}

		else {
			Text text = new Text(composite, SWT.WRAP | SWT.V_SCROLL | SWT.READ_ONLY);
			text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			text.setText(handler.getBody());
		}
	}
}
