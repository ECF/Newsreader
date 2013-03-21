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
package org.eclipse.ecf.salvo.ui.internal.handlers;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;

public class PostNewArticleHandler {

	@Execute
	public void execute(EModelService modelService, EPartService partService)
			throws ExecutionException {
		//
		// List<MPart> elements = modelService.findElements(null,
		// "org.eclipse.ecf.salvo.ui.internal.views.postNewArticleView",
		// MPart.class, null);
		MPart part = partService
				.createPart("org.eclipse.ecf.salvo.ui.internal.views.postNewArticleView");
		partService.showPart(part, PartState.ACTIVATE);
	}

}
