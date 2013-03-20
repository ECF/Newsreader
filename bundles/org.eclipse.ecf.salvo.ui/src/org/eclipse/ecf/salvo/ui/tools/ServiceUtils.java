package org.eclipse.ecf.salvo.ui.tools;

import java.util.Collection;
import java.util.Dictionary;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

/**
 * Service and Handler related utilities.
 * 
 */
public class ServiceUtils {

	/**
	 * Returns an instance of the requested service or null if none could be
	 * found.
	 * 
	 * @param requester
	 *            the class of the requester of the service
	 * @param serviceClass
	 *            the service class to look for
	 * @param filter
	 *            OSGi filter
	 * @return an instance of the serviceClass or null if none was found or the
	 *         filter syntax is invalid.
	 */
	public static <T extends Object> T getOSGiService(Class<?> requester,
			Class<T> serviceClass, String filter) {
		BundleContext context = FrameworkUtil.getBundle(requester)
				.getBundleContext();
		Collection<ServiceReference<T>> serviceReferences;
		try {
			serviceReferences = context.getServiceReferences(serviceClass,
					filter);
		} catch (InvalidSyntaxException e) {
			e.printStackTrace();
			return null;
		}
		if (serviceReferences.isEmpty()) {
			return null;
		}

		return context.getService(serviceReferences.iterator().next());
	}

	public static ServiceRegistration<?> registerService(Class<?> requester,
			Object service, Dictionary dictionary, String... services) {
		BundleContext context = FrameworkUtil.getBundle(requester)
				.getBundleContext();
		ServiceRegistration<?> serviceRegistration = context.registerService(
				services, service, dictionary);
		return serviceRegistration;
	}
}