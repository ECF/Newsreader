package org.eclipse.ecf.examples.remoteservices.quotes.consumer;

import org.eclipse.ecf.remoteservice.IAsyncCallback;

public class MyAsyncCallback<T> extends AbstractAsyncCallback implements IAsyncCallback<String[]> {

	public MyAsyncCallback(ConsumerUI anUI) {
		super(anUI);
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
}
