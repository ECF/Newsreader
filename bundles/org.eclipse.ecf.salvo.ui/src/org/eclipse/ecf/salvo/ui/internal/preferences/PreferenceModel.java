package org.eclipse.ecf.salvo.ui.internal.preferences;

import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.ecf.protocol.nntp.core.Debug;
import org.eclipse.ecf.protocol.nntp.model.SALVO;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

public class PreferenceModel {

	public static final String VIEW_PER_GROUP = "viewPerGroup";

	public final static PreferenceModel instance = new PreferenceModel();

	private Preferences preferences;

	protected PreferenceModel() {
		preferences = ConfigurationScope.INSTANCE
				.getNode("org.eclipse.ecf.salvo.ui");
		preferences.putBoolean("useDetachedView", true);
		flush();
	}

	private void flush() {
		try {
			preferences.flush();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}

	public void setViewPerGroup(boolean viewPerGroup) {
		Debug.log(getClass(), "setViewPerGroup(" + viewPerGroup + ")");
		preferences.putBoolean(VIEW_PER_GROUP, viewPerGroup);
	}

	public boolean getViewPerGroup() {
		return preferences.getBoolean(VIEW_PER_GROUP, false);
	}

	/**
	 * Retrieves the Signature from the preferences
	 * 
	 * @return
	 */
	public String getSignature() {
		String result = "";

		result = preferences.get("signature", "");
		if (result.equals("")) {
			result = "--" + SALVO.CRLF + SALVO.CRLF + "Best Regards,"
					+ SALVO.CRLF + "${NAME}" + SALVO.CRLF + "-- ${QUOTE}";
			setSignature(result);
		}
		return result;
	}

	public void setUseSignature(boolean useSignature) {
		preferences.putBoolean("useSignature", useSignature);
		flush();
	}

	public boolean getUseSignature() {
		return preferences.getBoolean("useSignature", false);
	}

	public void setSignature(String signature) {
		preferences.put("signature", signature);
	}

	public boolean getUseDetachedView() {
		return preferences.getBoolean("useDetachedView", false);
	}

	public void setUseDetachedView(boolean useDetached) {
		preferences.putBoolean("useDetachedView", useDetached);
	}

	public void setQuoteService(String element) {
		preferences.put("quoteService", element);
	}

	public String getQuoteService() {
		return preferences.get("quoteService", "");
	}
}
