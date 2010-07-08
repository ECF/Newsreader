package org.eclipse.ecf.salvo.ui.internal.provider;

import org.eclipse.ecf.protocol.nntp.core.Debug;
import org.eclipse.ecf.protocol.nntp.model.INewsgroup;
import org.eclipse.ecf.salvo.ui.internal.Activator;
import org.eclipse.ecf.salvo.ui.internal.preferences.PreferenceModel;
import org.eclipse.ecf.services.quotes.QuoteService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

public class SignatureProvider {

	public static String getSignature(INewsgroup newsgroup) {

		PreferenceModel prefs = PreferenceModel.instance;
		String quote = prefs.getSignature();
		quote = quote.replaceAll("\\$\\{NAME\\}", newsgroup.getServer().getServerConnection().getUser());

		String service = PreferenceModel.instance.getQuoteService();
		
		if(service.length() == 0)
			return "";

		BundleContext bundleContext = Activator.getDefault().getBundle().getBundleContext();
		ServiceReference[] srvRefs;
		try {
			srvRefs = bundleContext.getServiceReferences(QuoteService.class.getName(), "(component.name=" + service+")");
			if (srvRefs.length > 0) {
				QuoteService quoteService = (QuoteService) bundleContext.getService(srvRefs[0]);
				quote = quote.replaceAll("\\$\\{QUOTE\\}", quoteService.getRandomQuote());
			}
		} catch (InvalidSyntaxException e) {
			Debug.log(SignatureProvider.class, e);
		}

		return quote;

	}

}
