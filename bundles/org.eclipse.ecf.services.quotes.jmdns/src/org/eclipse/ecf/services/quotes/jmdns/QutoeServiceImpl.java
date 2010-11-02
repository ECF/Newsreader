package org.eclipse.ecf.services.quotes.jmdns;

import java.util.Arrays;

import org.eclipse.ecf.discovery.IDiscoveryLocator;
import org.eclipse.ecf.discovery.IServiceInfo;
import org.eclipse.ecf.discovery.identity.IServiceID;
import org.eclipse.ecf.discovery.identity.IServiceTypeID;
import org.eclipse.ecf.services.quotes.QuoteService;

public class QutoeServiceImpl implements QuoteService {

	private IDiscoveryLocator discoveryLocator;

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.services.quotes.QuoteService#getServiceName()
	 */
	public String getServiceName() {
		return "JmDNS (ZeroConfig) Quote Service";
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.services.quotes.QuoteService#getServiceDescription()
	 */
	public String getServiceDescription() {
		return getServiceName();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.services.quotes.QuoteService#getRandomQuote()
	 */
	public String getRandomQuote() {
		final IServiceInfo[] services = discoveryLocator.getServices();
		final StringBuffer buf = new StringBuffer(services.length);
		for (int i = 0; i < services.length; i++) {
			final IServiceInfo service = services[i];
			buf.append("Service name:");
			buf.append(service.getServiceName());
			buf.append("\nLocation:");
			buf.append(service.getLocation());
			buf.append("\ntype:");
			final IServiceID serviceID = service.getServiceID();
			final IServiceTypeID serviceTypeID = serviceID.getServiceTypeID();
			buf.append("\nsupported protocols:");
			buf.append(Arrays.toString(serviceTypeID.getProtocols()));
			buf.append("\nscopes:");
			buf.append(Arrays.toString(serviceTypeID.getScopes()));
		}
		return buf.toString();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.services.quotes.QuoteService#getAllQuotes()
	 */
	public String[] getAllQuotes() {
		return new String[]{"foo", "bar"};
	}

	public void bindDiscoveryLocatorService(IDiscoveryLocator aDiscoveryLocator) {
		discoveryLocator = aDiscoveryLocator;
	}
	
	public void unbindDiscoveryLocatorService(IDiscoveryLocator aDiscoveryLocator) {
		
	}
}
