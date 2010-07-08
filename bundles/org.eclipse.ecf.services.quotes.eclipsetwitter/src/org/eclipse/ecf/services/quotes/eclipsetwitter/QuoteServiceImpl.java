/*******************************************************************************
 *  Copyright (c) 2010 Weltevree Beheer BV, Remain Software & Industrial-TSI
 *
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *     Wim Jongman - initial API and implementation
 *
 *
 *******************************************************************************/
package org.eclipse.ecf.services.quotes.eclipsetwitter;

import java.util.Random;

import org.eclipse.ecf.services.quotes.QuoteService;

public class QuoteServiceImpl implements QuoteService {
	final static String NL = System.getProperty("line.separator");

	public static final String[] QUOTES = new String[] {
			"The net knows all! " + NL + "Jeff McAffer - EclipseSource",
			"is building, testing, building (but with hand crafted bits)" + NL
					+ "\tPaul Webster",
			"is sitting still" + NL + "\tPaul Webster",
			"Taking a shower. Need a couple of good ideas. ;-)" + NL
					+ "\tBoris Bokowski",
			"Pressure makes diamonds" + NL + "\tChris Anyszczyk",
			"In vacation. May the internet survive without me" + NL
					+ "\tLars Vogel",
			"BONG BONG BONG BONG" + NL + "\tBig Ben Clock",
			"wondering if I will ever again give a talk that I didn't prepare up to the minute..."
					+ NL + "\tPeter Friese",
			"I am all for convention over configuration, the only question is: Whose convention?"
					+ NL + "\tWim Jongman",
			"birdstrike today - pigeon into windscreen at 80km/h - quite a solid thunk - some dusty smears - alfa 156 2.0l wins"
					+ NL + "\tOison Hurley",
			"learning an important lesson: sometimes the crowd is not that wise"
					+ NL + "\tIan Skerrett",
			"NASA keynote at EclipseCon has me re-evaluating my wasted life..."
					+ NL + "\tWayne Beaton",
			"is very proud, as a Dutch immigrant, to be Canadian." + NL
					+ "\tEd Merks",
			"Too much coding lately, I find myself ending sentences with a semicolon"
					+ NL + "\tIan Bull",
			"When you get an error message, and punch it into Google, and get 0 hits, YOU ARE TRULY SCREWED."
					+ NL + "\tDonald Smith",
					"Liar.getLiar().isPantsOnFire()"
					+ NL + "\tKim Horne",
			"What happens when 3D printers get good enough to print better 3D printers?"
					+ NL + "\tNeil Bartlett" };

	private Random random;

	public QuoteServiceImpl() {
		random = new Random(System.nanoTime());
	}

	public String getRandomQuote() {
		return QUOTES[random.nextInt(QUOTES.length)];
	}

	public String getServiceName() {
		return "Eclipse Twitter";
	}

	public String getServiceDescription() {
		return "Eclipse Twitter";
	}

	public String[] getAllQuotes() {
		return QUOTES;
	}

}
