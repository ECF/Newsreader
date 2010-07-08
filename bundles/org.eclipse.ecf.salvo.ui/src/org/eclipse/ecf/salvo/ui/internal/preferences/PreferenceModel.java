package org.eclipse.ecf.salvo.ui.internal.preferences;

import org.eclipse.ecf.protocol.nntp.core.Debug;
import org.eclipse.ecf.protocol.nntp.model.SALVO;
import org.eclipse.ecf.salvo.ui.internal.Activator;
import org.eclipse.jface.preference.IPreferenceStore;

public class PreferenceModel {

	public static final String VIEW_PER_GROUP = "viewPerGroup";

	public final static PreferenceModel instance = new PreferenceModel();

	private IPreferenceStore store;

	protected PreferenceModel() {
		store = Activator.getDefault().getPreferenceStore();
		store.setDefault("useDetachedView", true);
	}

	public void setViewPerGroup(boolean viewPerGroup) {
		Debug.log(getClass(), "setViewPerGroup(" + viewPerGroup + ")");
		store.setValue(VIEW_PER_GROUP, viewPerGroup);
	}

	public boolean getViewPerGroup() {
		return store.getBoolean(VIEW_PER_GROUP);
	}

	/**
	 * Retrieves the Signature from the preferences
	 * 
	 * @return
	 */
	public String getSignature() {
		String result = "";

		result = store.getString("signature");
		if (result.equals("")) {
			result = "--" + SALVO.CRLF + SALVO.CRLF + "Best Regards," + SALVO.CRLF + "${NAME}" + SALVO.CRLF
					+ "-- ${QUOTE}";
			setSignature(result);
		}
		return result;
	}

	public void setUseSignature(boolean useSignature) {
		store.setValue("useSignature", useSignature);
	}

	/**
	 * Retrieves the Signature from the preferences
	 * 
	 * @return
	 */
	public boolean getUseSignature() {
		return store.getBoolean("useSignature");
	}

	public void setSignature(String signature) {
		store.setValue("signature", signature);
	}

	/**
	 * Retrieves the Signature from the preferences
	 * 
	 * @return
	 */
	public boolean getUseDetachedView() {
		return store.getBoolean("useDetachedView");
	}

	public void setUseDetachedView(boolean useDetached) {
		store.setValue("useDetachedView", useDetached);
	}

	public void setQuoteService(String element) {
		store.setValue("quoteService", element);
	}

	public String getQuoteService() {
		return store.getString("quoteService");
	}
}
