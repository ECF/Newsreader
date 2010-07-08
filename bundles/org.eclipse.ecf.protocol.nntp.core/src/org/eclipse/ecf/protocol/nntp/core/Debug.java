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
import java.util.Calendar;

public class Debug {

	public static boolean debug = "true".equals(System
			.getProperty("salvo.debug"));

	public static boolean printStackTrace = "true".equals(System
			.getProperty("salvo.debug.printstacktrace"));

	private static long timer;

	private static ArrayList classes = new ArrayList();

	/**
	 * Adds a class to the classes to log messages from
	 */
	public static void addClass(Class clazz) {
		if (!classes.contains(clazz))
			classes.add(clazz);
	}

	/**
	 * Removes a class to the classes to log messages from
	 */
	public static void removeClass(Class clazz) {
		classes.remove(clazz);
	}

	/**
	 * If the log was filtered to certain classes it is now reset to log from
	 * all classes.
	 */
	public static void logAllClasses() {
		classes.clear();
	}

	/**
	 * @param callingClass
	 * @param line
	 */
	public static void log(Class callingClass, String line) {
		if (!debug)
			return;
		if (classes.isEmpty() || classes.contains(callingClass))
			System.out.println(getTime() + " - " + callingClass.getName()
					+ "\t\t" + line);
	}

	/**
	 * @param callingClass
	 * @param line
	 */
	public static void log(Class callingClass, Throwable e) {
		System.out.println(getTime() + " - " + callingClass.getName() + "\t\t"
				+ e.getMessage());
		if (printStackTrace)
			e.printStackTrace();
	}

	private static String getTime() {

		return Calendar.getInstance().get(Calendar.HOUR) + ":"
				+ Calendar.getInstance().get(Calendar.MINUTE) + ":"
				+ Calendar.getInstance().get(Calendar.SECOND);
	}

	public static void logn(String line) {
		if (debug)
			System.out.print(line);

	}

	public static int timerStart(Class logger) {
		if (debug) {
			timer = Calendar.getInstance().getTimeInMillis();
			System.out.println(getTime() + " - " + logger.getName()
					+ "\t\t Timer started");
		}
		return 0; // insert timer number TODO
	}

	public static void timerStop(Class logger, int timerNumber) {
		if (debug) {
			timer = Calendar.getInstance().getTimeInMillis() - timer;
			System.out.println(getTime() + " - " + logger.getName()
					+ "\t\t Timer stopped: " + timer);
		}
	}

}
