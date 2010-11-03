package org.eclipse.ecf.examples.remoteservices.quotes.consumer;

import java.lang.reflect.Method;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.ecf.services.quotes.QuoteService;
import org.eclipse.ecf.services.quotes.QuoteServiceAsync;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.equinox.concurrent.future.IFuture;
import org.eclipse.swt.widgets.Display;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceException;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;

@SuppressWarnings("restriction")
public class Application implements IApplication {

	private ConsumerUI ui;

	@Override
	public Object start(IApplicationContext context) throws Exception {

		System.out.println("Started");

		ui = new ConsumerUI(null);
		addListener();
		ui.main(null);

		return null;
	}

	@Override
	public void stop() {
		System.out.println("Stopped");

	}

	private void addListener() {
		Activator.getContext().addServiceListener(new ServiceListener() {

			@Override
			public void serviceChanged(final ServiceEvent event) {
				
				/*
				 * Do only for registrations
				 */
				if (event.getType() == ServiceEvent.REGISTERED) {
						Object obj = Activator.getContext().getService(event.getServiceReference());
						fillInfo(event.getServiceReference(), obj);
						
						/*
						 * If we know this service
						 * 
						 * Only call expensive getAllQuotes() on async proxy
						 */
						boolean useAsync = true;
						if (obj instanceof QuoteServiceAsync && useAsync) {
							final QuoteServiceAsync service = (QuoteServiceAsync) Activator.getContext().getService(
									event.getServiceReference());
							
							/*
							 * Decide which invocation style to use
							 */
							boolean useCallback = false;
							if(useCallback) {
								useCallbackParadigm(service);
							} else {
								useFutureParadigm(service);
							}
							
							
					} else if (obj instanceof QuoteService) {
						try {
							// this is bad because called inside a framework
							// thread we should no block for long
							final QuoteService service = (QuoteService) Activator.getContext().getService(
									event.getServiceReference());
							// update ui
							updateUI(service.getServiceDescription(), service.getRandomQuote(), 1);
						} catch (final Exception e) {
							// catch all possible exceptions	
							if (e instanceof ServiceException) {
								ServiceException se = (ServiceException) e;
								if (se.getType() == ServiceException.REMOTE) {
									System.err.println("Remote ServiceException caught: ");
									e.printStackTrace();
								}
							}
							// update ui
							updateUI(e.getLocalizedMessage(), "", -1);
						}
					}
				}
			}

			private void useFutureParadigm(final QuoteServiceAsync service) {
				try {
					String text = "";
					final IFuture future = service.getAllQuotesAsync();
					
					// burn some CPU cycles to show async invocation
					int i = 0;
					while (!future.isDone()) {
						final int cycle = i++;
						final String serviceDesc = getServiceDesc(service);
						Display.getDefault().asyncExec(new Runnable() {
							@Override
							public void run() {
								ui.getLabel().setText(
										"waiting for " + serviceDesc + " counting cycles: " + cycle);
								ui.redraw();
							}
						});
						// give the client some rest
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					
					final IStatus status = future.getStatus();
					if (status.isOK()) {
						try {
							StringBuffer buf = new StringBuffer();
							String[] values = (String[]) future.get();
							for (int j = 0; j < values.length; j++) {
								String string = values[j];
								buf.append(string);
								buf.append("\n");
							}
							text = buf.toString();
							// checked exceptions do suck
						} catch (OperationCanceledException e) {
							// will never happen since status is OK
							e.printStackTrace();
						} catch (InterruptedException e) {
							// will never happen since status is OK
							e.printStackTrace();
						}
						updateUI("Future invocation succeeded on " + getServiceDesc(service), text, 1);
					} else {
						updateUI("Future invocation failed", status.getException().getMessage(), -1);
					}
				} catch (Exception e) {
					updateUI("Future invocation failed", e.getMessage(), -1);
				}
			}

			// get the name synchronously
			private String getServiceDesc(QuoteServiceAsync service) {
				QuoteService qs = (QuoteService) service;
				return qs.getServiceDescription();
			}

			private void updateUI(String label, String text, int value) {
				if(value != 0) {
					final String fLabel = label;
					final String fText = text;
					final int fValue = value;
					Display.getDefault().asyncExec(new Runnable() {
						@Override
						public void run() {
							ui.getLabel().setText(fLabel == null ? "" : fLabel);
							ui.getStyledText().setText(fText == null ? "" : fText);
							ui.getDispatcher().setValue(fValue);
							ui.redraw();
						}
					});
				}
			}

			private void useCallbackParadigm(final QuoteServiceAsync service) {
				// create a callback to handle the async invocation
				MyAsyncCallback<String[]> callback = new MyAsyncCallback<String[]>(ui);
				service.getAllQuotesAsync(callback);
			}

			private void fillInfo(ServiceReference s, Object obj) {
				final StringBuffer infoBuf = new StringBuffer();
				infoBuf.append("ServiceReference Info\n\t");
				infoBuf.append("Bundle:\t").append(s.getBundle().getSymbolicName()).append("\n\n\t");
				for (String key : s.getPropertyKeys())
					if (s.getProperty(key) instanceof Object[])
						for (int i = 0; i < ((Object[]) s.getProperty(key)).length; i++)
							if (i == 0)
								infoBuf.append("Property:\t").append(key).append("=")
										.append(((Object[]) s.getProperty(key))[i].toString()).append("\n\t");
							else
								infoBuf.append("\t\t\t").append(key).append("=")
										.append(((Object[]) s.getProperty(key))[i]).append("\n\t");
					else
						infoBuf.append("Property:\t").append(key).append("=").append(s.getProperty(key)).append("\n\t");
				infoBuf.append("\nService Info\n\t");
				infoBuf.append("Class:\t").append(obj.getClass().getName()).append("\n\t");
				infoBuf.append("\nImplements").append("\n\t");
				for (Class<?> i : obj.getClass().getInterfaces())
					infoBuf.append("Class:\t").append(i.getName()).append("\n\t");
				infoBuf.append("\nSupers").append("\n\t");
				Class<?> sc = obj.getClass().getSuperclass();
				while (sc != null && sc != Object.class) {
					infoBuf.append("Class:\t").append(sc.getName()).append("\n\t");
					sc = sc.getClass().getSuperclass();
				}
				infoBuf.append("\nMethods").append("\n\t");
				for (Method m : obj.getClass().getMethods()) {
					infoBuf.append("Method:\t").append("(").append(m.getReturnType()).append(") ").append(m.getName())
							.append("(");
					for (Class<?> c : m.getParameterTypes())
						infoBuf.append(c.getSimpleName()).append(" ");
					infoBuf.append(")\n\t");
				}

				Display.getDefault().asyncExec(new Runnable() {

					@Override
					public void run() {
						ui.getInfo().setText(infoBuf.toString());

					}
				});
			}
		});
	}
}
