package com.secunet.ipsmall.browser.simulator;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.cert.X509Certificate;

import com.secunet.ipsmall.rmi.IBrowserSimulator;
import com.secunet.ipsmall.rmi.RmiHttpResponse;

/**
 * Implementation of the IBrowserSimulator interface to communicate
 * with the testbed via RMI, e.g. to receive commands
 * (e.g. to request a page). 
 * That is, this is the RMI-server running within VMWare guest.
 * 
 * Also allows for sending commands (e.g. notify on results) via backchannel, 
 * which is initiated upon creation (todo).
 * 
 * @author kersten.benjamin
 *
 */
public class BrowserSimulatorRmiServer extends UnicastRemoteObject implements IBrowserSimulator {

	
	private static final long serialVersionUID = 4887240908307691704L;
	
	protected BrowserSimulatorRmiServer() throws RemoteException {
		super();
	}


	@Override
	public RmiHttpResponse sendHttpRequest(String url, X509Certificate[] trustedCerts, boolean followRedirects) throws Exception {
		//System.out.println("Incoming RMI-command for http request: " + url);
		return  (new HttpHandler()).sendRequest(url, trustedCerts, followRedirects);
	}

	
}
