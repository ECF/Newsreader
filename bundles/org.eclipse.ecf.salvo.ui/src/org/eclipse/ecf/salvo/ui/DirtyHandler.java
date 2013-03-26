package org.eclipse.ecf.salvo.ui;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ecf.protocol.nntp.model.IServer;
import org.eclipse.ecf.salvo.ui.internal.resources.ISalvoResource;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

public class DirtyHandler extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {

		ISelection selection = HandlerUtil.getCurrentSelection(event);
		
		if(selection == null){
			return null;
		}
		if (!(selection instanceof IStructuredSelection)) {
			return null;
		}
		IStructuredSelection struct = (IStructuredSelection) selection;
		if (struct.isEmpty()) {
			return null;
		}
		if (!(struct.getFirstElement() instanceof ISalvoResource)) {
			return null;
		}
		
		ISalvoResource resource = (ISalvoResource) struct.getFirstElement();
		if (!(resource.getObject() instanceof IServer)) {
			return null;
		}

		IServer server = (IServer) resource.getObject();
		server.setDirty(true);
		return null;
	}
}
