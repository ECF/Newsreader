package org.eclipse.ecf.examples.remoteservices.quotes.consumer;

import org.eclipse.swt.widgets.Display;

public abstract class AbstractAsyncCallback {

	protected static final int FAILURE = -1;
	protected static final int SUCCESS = 1;
	protected ConsumerUI ui;

	/* helper methods */

	public AbstractAsyncCallback(ConsumerUI anUI) {
		ui = anUI;
	}

	protected String convertStringArrayToString(String[] result) {
		// convert string[] to string and add newlines
		final StringBuffer buf = new StringBuffer(result.length * 2);
		for (int j = 0; j < result.length; j++) {
			String string = result[j];
			buf.append(string);
			buf.append("\n");
		}
		return buf.toString();
	}
	
	protected void updateUI(final String label, final String text, final int status) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				ui.getLabel().setText(label == null ? "" : label);
				ui.getStyledText().setText(text == null ? "" : text);
				ui.getDispatcher().setValue(status);
				ui.redraw();
			}
		});
	}

}
