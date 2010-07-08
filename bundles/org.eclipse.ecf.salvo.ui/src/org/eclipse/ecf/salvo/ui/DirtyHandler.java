package org.eclipse.ecf.salvo.ui;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ecf.protocol.nntp.model.IServer;
import org.eclipse.ecf.salvo.ui.internal.resources.ISalvoResource;
import org.eclipse.ecf.salvo.ui.tools.SelectionUtil;
import org.eclipse.ui.handlers.HandlerUtil;

public class DirtyHandler extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {

		ISalvoResource resource = (ISalvoResource) SelectionUtil
				.getFirstObjectFromSelection(HandlerUtil
						.getCurrentSelection(event), ISalvoResource.class);
		if (resource != null && resource.getObject() instanceof IServer) {
			((IServer) resource.getObject()).setDirty(true);
		}
		return null;
	}

}
