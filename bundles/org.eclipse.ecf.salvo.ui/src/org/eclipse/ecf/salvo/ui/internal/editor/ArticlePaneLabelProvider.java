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

import java.text.DateFormat;
import java.util.Date;

import org.apache.james.mime4j.codec.DecoderUtil;
import org.eclipse.ecf.protocol.nntp.core.DateParser;
import org.eclipse.ecf.protocol.nntp.model.IArticle;
import org.eclipse.ecf.salvo.ui.internal.resources.ISalvoResource;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableFontProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;


public class ArticlePaneLabelProvider implements ITableLabelProvider,
		ITableFontProvider, ITableColorProvider {

	private static Font unreadArticle;
	private static Font unreadReplies;
	private static Color mineColor;
	private static Color commentColor;

	static {

		Font font = JFaceResources.getDefaultFont();

		FontData fd = font.getFontData()[0];
		fd.setStyle(SWT.BOLD);
		unreadArticle = new Font(font.getDevice(), fd);

		fd = font.getFontData()[0];
		fd.setStyle(SWT.ITALIC);
		unreadReplies = new Font(font.getDevice(), fd);

		mineColor = Display.getDefault().getSystemColor(SWT.COLOR_MAGENTA);
		commentColor = Display.getDefault().getSystemColor(
				SWT.COLOR_DARK_MAGENTA);
	}

	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {

		if (element instanceof ISalvoResource) {

			IArticle article = (IArticle) ((ISalvoResource) element)
					.getObject();

			if (columnIndex == 0)
				return DecoderUtil.decodeEncodedWords(article.getSubject());
			if (columnIndex == 1)
				 return DecoderUtil.decodeEncodedWords(article.getFullUserName());
			if (columnIndex == 2) {
				Date date = DateParser.parseRFC822(article.getDate());
				return getNiceDate(article, date);
			}
			if (columnIndex == 3)
				return article.getFormattedSize();
			if (columnIndex == 4)
				return "?6?";

		}

		return "dunno";
	}

	/**
	 * Makes a pleasant readable date like "today 12:15" or "12:15"
	 * 
	 * @param article
	 * @param date
	 * @return
	 */
	private String getNiceDate(IArticle article, Date date) {

		// FIXME - Make a nice date
		return date == null ? article.getDate() : DateFormat
				.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
				.format(date);
	}

	public void addListener(ILabelProviderListener listener) {
	}

	public void dispose() {
	}

	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	public void removeListener(ILabelProviderListener listener) {
	}

	public Font getFont(Object element, int columnIndex) {

		IArticle article = (IArticle) ((ISalvoResource) element).getObject();

		if (columnIndex == 0) {
			if (!article.isRead()) {
				return unreadArticle;
			}
			if (!article.isReplyRead()) {
				return unreadReplies;
			}
		}

		return JFaceResources.getDefaultFont();
	}

	public Color getBackground(Object element, int columnIndex) {
		return null;
	}

	public Color getForeground(Object element, int columnIndex) {
		IArticle article = (IArticle) ((ISalvoResource) element).getObject();

		if (columnIndex == 0) {
			if (article.isCommenting()) {
				return commentColor;
			}
			if (article.isMine()) {
				return mineColor;
			}
		}
		return null;
	}
}