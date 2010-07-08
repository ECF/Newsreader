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
package org.eclipse.ecf.services.quotes.starwars;

import java.util.Random;

import org.eclipse.ecf.services.quotes.QuoteService;

public class QuoteServiceImpl implements QuoteService {

	public static final String[] QUOTES = new String[] {
			"Fear is the path to the Dark Side. Fear leads to anger, anger leads to hate, hate leads to suffering.\n\tYODA, Star Wars Episode I: The Phantom Menace",
			"Death is a natural part of life. Rejoice for those around you who transform into the Force.\n\tYODA, Star Wars Episode III: Revenge of the Sith",
			"Hokey religions and ancient weapons are no match for a good blaster at your side, kid.\n\t	HAN SOLO, Star Wars Episode IV: A New Hope",
			"It's against my programming to impersonate a deity.\n\tC-3PO, Star Wars Episode VI: Return of the Jedi",
			"Truly wonderful the mind of a child is.\n\tYODA, Star Wars Episode II: Attack of the Clones",
			"Always in motion is the future.\n\tYODA, Star Wars Episode V: The Empire Strikes Back",
			"A Jedi gains power through understanding and a Sith gains understanding through power.\n\t	PALPATINE, Star Wars Episode III: Revenge of the Sith",
			"There's no mystical energy field that controls my destiny.\n\tHAN SOLO, Star Wars Episode IV: A New Hope",
			"R2-D2, you know better than to trust a strange computer.\n\tC-3PO, Star Wars Episode V: The Empire Strikes Back",
			"Only a Sith Lord deals in absolutes.\n\tObi-Wan Kenobi, Star Wars Episode III: Revenge of the Sith",
			"The fear of loss is a path to the Dark Side.\n\tYODA, Star Wars Episode III: Revenge of the Sith",
			"Your eyes can deceive you; don't trust them.\n\tOBI-WAN KENOBI, Star Wars Episode IV: A New Hope",
			"Size matters not. Look at me. Judge me by my size, do you?	\n\tYODA, Star Wars Episode V: The Empire Strikes Back",
			"What if the democracy we thought we were serving no longer exists, and the Republic has become the very evil we've been fighting to destroy?\n\tPADME AMIDALA, Star Wars Episode III: Revenge of the Sith",
			"Who's the more foolish: the fool, or the fool who follows him?	\n\tOBI-WAN KENOBI, Star Wars Episode IV: A New Hope",
			"I think my eyes are getting better. Instead of a big dark blur, I see a big light blur.\n\tHAN SOLO, Star Wars Episode VI: Return of the Jedi",
			"There's always a bigger fish.\n\tQUI-GON JINN, Star Wars Episode I: The Phantom Menace",
			"When 900 years old, you reach… Look as good, you will not.\n\tYoda to Luke Skywalker, Episode VI: Return Of The Jedi",
			"You don’t know how hard I found it, signing the order to terminate your life\n\tGovernor “Grand Moff” Tarkin to Princess Leia Organa, Episode IV: A New Hope,",
			"You can’t win, Darth. Strike me down, and I will become more powerful than you could possibly imagine\n\tObi wan Kenobi to Darth Vader, Episode IV: A New Hope" };

	private Random random;

	public QuoteServiceImpl() {
		random = new Random(System.currentTimeMillis());
	}

	public String getRandomQuote() {
		return QUOTES[random.nextInt(QUOTES.length)];
	}

	public String getServiceDescription() {
		return "Star Wars Quotes";
	}

	public String getServiceName() {
		return "Star Wars Quotes";
	}

	public String[] getAllQuotes() {
		return QUOTES;
	}

}
