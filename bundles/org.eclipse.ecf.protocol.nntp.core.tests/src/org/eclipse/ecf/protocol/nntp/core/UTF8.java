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
package org.eclipse.ecf.protocol.nntp.core;

import java.io.UnsupportedEncodingException;

import org.apache.james.mime4j.codec.DecoderUtil;

public class UTF8 {

	/**
	 * @param args
	 * @throws UnsupportedEncodingException
	 */
	public static void main(String[] args) throws UnsupportedEncodingException {
		// TODO Auto-generated method stub

		// your UTF-8 string here, taken from args, request params, etc.
		String utf = "=?utf-8?Q?Re:=20How=20to=20place=20a=20view=20at=20the=20bottom?= =?utf-8?Q?=20with=20a=20100%=20width?=";
	//	String utf = "=?ISO-8859-15?Q?Bj=F6rn_Fischer?= <bjoern@bjoernfischer.de>";
	//	System.out.println(MimeUtility.decodeText(utf));
	//	MimeUtility.decodeText(utf)
		

		String[] result = utf.split("\\=\\?"); // = new
											// StringTokenizer(utf,"=?",false);

		for (String t : result) {
			System.out.println(t);
			System.out.println(DecoderUtil.decodeEncodedWords("=?" + t));
			System.out.println("");
		}

	}
}
