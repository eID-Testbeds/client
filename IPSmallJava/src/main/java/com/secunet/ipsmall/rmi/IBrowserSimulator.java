package com.secunet.ipsmall.rmi;

import java.rmi.Remote;
import java.security.cert.X509Certificate;

/**
 * Rmi Client interface of the browser simulator. To be looked up by the 
 * client, i.e. here, the testbed (VMWare Host) initiates this interface to pass RMI
 * command to the RMI-server on the VMWare Guest.
 * RMI-Server does implement this interface.
 * 
 * @author kersten.benjamin
 *
 */
public interface IBrowserSimulator extends Remote {
	
	public static final String RMI_SERVICE_NAME = "BrowserSimulatorRmiServer";
	
	public static final String HEADER_KEY_USER_AGENT = "user-agent";
	public static final String HEADER_VALUE_USER_AGENT = "BrowserSimulator";
	
	//public void setCallback(IHttpResponseCallback callback) throws RemoteException;
	public RmiHttpResponse sendHttpRequest(String url, X509Certificate[] trustedCerts, boolean followRedirects) throws Exception;
	
}
