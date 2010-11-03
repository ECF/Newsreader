package org.eclipse.ecf.examples.remoteservices.quotes.consumer;

import org.eclipse.ecf.remoteservice.IAsyncCallback;
import org.eclipse.swt.widgets.Display;

public class MyAsyncCallback<T> implements IAsyncCallback<String[]> {

	private static final int FAILURE = -1;
	private static final int SUCCESS = 1;
	private ConsumerUI ui;

	public MyAsyncCallback(ConsumerUI anUI) {
		ui = anUI;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.remoteservice.IAsyncCallback#onSuccess(java.lang.Object)
	 */
	@Override
	public void onSuccess(String[] result) {
		String text = convertStringArrayToString(result);
		updateUI("Callback invocation succeeded", text, SUCCESS);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.remoteservice.IAsyncCallback#onFailure(java.lang.Throwable)
	 */
	@Override
	public void onFailure(final Throwable exception) {
		updateUI("Callback invocation failed", exception.getMessage(), FAILURE);
	}

	/* helper methods */

	private String convertStringArrayToString(String[] result) {
		// convert string[] to string and add newlines
		final StringBuffer buf = new StringBuffer(result.length * 2);
		for (int j = 0; j < result.length; j++) {
			String string = result[j];
			buf.append(string);
			buf.append("\n");
		}
		return buf.toString();
	}
	
	private void updateUI(final String label, final String text, final int status) {
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
