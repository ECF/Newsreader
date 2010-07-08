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

import java.util.ArrayList;

public class StringUtils {

	/**
	 * Splits the given string by delimiter.
	 * 
	 * @param string
	 * @param delimiter
	 * @return the unprocessed array
	 */
	public static String[] split(String string, String delimiter) {

		String[] result;

		if (string == null) {
			result = new String[0];
			return result;
		}

		ArrayList list = new ArrayList();

		do {
			int location = string.indexOf(delimiter);

			if (location < 0)
				break;
			list.add(string.substring(0, location).trim());
			string = string.substring(location + 1);
		} while (true);

		string = string.trim();

		if (string.length() > 0)
			list.add(string);

		result = (String[]) list.toArray(new String[list.size()]);

		// StringTokenizer tizer = new StringTokenizer(string, delimiter);
		// String[] result = new String[tizer.countTokens()];
		// for (int i = 0; tizer.hasMoreElements(); i++) {
		// result[i] = tizer.nextToken();
		// }
		return result;

	}

	/**
	 * Splits the string by the given delimiter but does not accept delimiters
	 * or blank elements and trims all elements. If the delimiter contains
	 * blanks then these are trimmed before comparing so the results might be
	 * not as you expect.
	 * 
	 * @param string
	 * @param delimiter
	 * @return the processed array
	 */
	public static String[] split2(String string, String delimiter) {

		String[] result = split(string, delimiter);
		ArrayList result2 = new ArrayList();
		for (int i = 0; i < result.length; i++) {
			String mean = result[i].trim();
			if (mean.length() > 0 && !mean.equals(delimiter.trim())) {
				result2.add(mean);
			}
		}
		return (String[]) result2.toArray(new String[result2.size()]);
	}
}
