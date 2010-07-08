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
package org.eclipse.ecf.salvo.ui.internal;

import java.io.IOException;
import java.io.InputStream;

import org.apache.james.mime4j.MimeException;
import org.apache.james.mime4j.descriptor.BodyDescriptor;
import org.apache.james.mime4j.parser.AbstractContentHandler;
import org.apache.james.mime4j.parser.Field;
import org.eclipse.ecf.protocol.nntp.core.Debug;
import org.eclipse.ecf.protocol.nntp.model.IArticle;
import org.eclipse.ecf.protocol.nntp.model.SALVO;


public class MimeArticleContentHandler extends AbstractContentHandler {

	private final IArticle article;

	private String body;

	public MimeArticleContentHandler(IArticle article) {
		this.article = article;
	}

	public MimeArticleContentHandler() {
		this.article = null;
	}

	@Override
	public void body(BodyDescriptor bd, InputStream is) throws MimeException, IOException {
		StringBuffer buffer = new StringBuffer();
		int ch = is.read();
		while (ch > -1) {
			buffer.append((char) ch);
			ch = is.read();
		}
		this.body = buffer.toString().replaceAll(SALVO.CRLF + "\\." + SALVO.CRLF, SALVO.CRLF);
	}

	public String getBody() {
		return body;
	}

	@Override
	public void field(Field field) throws MimeException {
		article.setHeaderAttributeValue(field.getName(), field.getBody());
		Debug.log(getClass(), "Header field detected: " + field.getName() + " - " + field.getBody());
	}

	@Override
	public void startMultipart(BodyDescriptor bd) throws MimeException {
		article.setHeaderAttributeValue("Mime-Media " , bd.getMediaType());
		article.setHeaderAttributeValue("Mime-Type " , bd.getMimeType() + " " + bd.getSubType());
		article.setHeaderAttributeValue("Mime-Enco " , bd.getTransferEncoding());
		article.setHeaderAttributeValue("Mime-CTParms " , bd.getContentTypeParameters().toString());
		System.out.println("Multipart body " + bd.getBoundary());
	}

	@Override
	public void preamble(InputStream is) throws MimeException, IOException {
		article.setHeaderAttributeValue("Mime - Preamble" , is.toString());
	}
}