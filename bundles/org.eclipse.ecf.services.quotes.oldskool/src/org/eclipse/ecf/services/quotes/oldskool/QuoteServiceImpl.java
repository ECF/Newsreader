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
package org.eclipse.ecf.services.quotes.oldskool;

import java.util.Random;

import org.eclipse.ecf.services.quotes.QuoteService;

public class QuoteServiceImpl implements QuoteService {
	public static final String[] QUOTES = new String[] {
			"2+2 = 5 for extremely large values of 2.",
			"Computers make very fast, very accurate mistakes.",
			"Computers are not intelligent. They only think they are.",
			"The Definition of an Upgrade: Take old bugs out, put new ones in.",
			"The name is Baud......, James Baud.",
			"BUFFERS=20 FILES=15 2nd down, 4th quarter, 5 yards to go!",
			"As a computer, I find your faith in technology amusing.",
			"Southern DOS: Y'all reckon? (Yep/Nope)",
			"... File not found. Should I fake it? (Y/N)",
			"Does fuzzy logic tickle?",
			"A computer's attention span is as long as it's power cord.",
			"Who's General Failure & why is he reading my disk?",
			"RAM disk is not an installation procedure.",
			"All computers wait at the same speed.",
			"Smash forehead on keyboard to continue.....",
			"Enter any 11-digit prime number to continue...",
			"ASCII question, get an ANSI!",
			"All wiyht. Rho sritched mg kegtops awound?",
			"Error: Keyboard not attached. Press F1 to continue.",
			"\"640K ought to be enough for anybody.\" - Bill Gates, 1981",
			"Press any key to continue or any other key to quit...",
			"Press CTRL-ALT-DEL to continue ..." };

	private Random random;

	public QuoteServiceImpl() {
		random = new Random(System.nanoTime());
	}

	public String getRandomQuote() {
		return QUOTES[random.nextInt(QUOTES.length)]
				+ "\n\t(Eclipse Old Skool Quote Service)";
	}

	public String getServiceName() {
		return "Old Skool Technology Quotes";
	}

	public String getServiceDescription() {
		return "Old Skool Technology Quotes";
	}

	public String[] getAllQuotes() {
		return QUOTES;
	}
}
