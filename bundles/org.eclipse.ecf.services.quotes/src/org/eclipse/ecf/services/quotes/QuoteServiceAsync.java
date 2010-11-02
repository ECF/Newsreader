package org.eclipse.ecf.services.quotes;



import org.eclipse.ecf.remoteservice.IAsyncRemoteServiceProxy;
import org.eclipse.equinox.concurrent.future.IFuture;

@SuppressWarnings("restriction")
public interface QuoteServiceAsync extends IAsyncRemoteServiceProxy {
	public IFuture getAllQuotesAsync();
}
