package com.secunet.ipsmall.tls;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Vector;

import org.bouncycastle.crypto.params.DHParameters;

import com.secunet.bouncycastle.crypto.tls.Certificate;
import com.secunet.bouncycastle.crypto.tls.DefaultTlsServer;
import com.secunet.bouncycastle.crypto.tls.NewSessionTicket;
import com.secunet.bouncycastle.crypto.tls.ProtocolVersion;
import com.secunet.bouncycastle.crypto.tls.SecurityParameters;
import com.secunet.bouncycastle.crypto.tls.SessionParameters;
import com.secunet.bouncycastle.crypto.tls.SignatureAndHashAlgorithm;
import com.secunet.bouncycastle.crypto.tls.TlsECCUtils;
import com.secunet.bouncycastle.crypto.tls.TlsExtensionsUtils;
import com.secunet.bouncycastle.crypto.tls.TlsSession;
import com.secunet.bouncycastle.crypto.tls.TlsUtils;
import org.bouncycastle.util.Arrays;

import com.secunet.ipsmall.log.IModuleLogger.LogLevel;
import com.secunet.ipsmall.log.Logger;
import com.secunet.testbedutils.utilities.BouncyCastleTlsHelper;

/**
 * This class maps BouncyCastle TLS events from class DefaultTlsServer to an external notification listener interface.
 * @author schiel.patrick
 */
public class BouncyCastleNotifyingTlsServer extends DefaultTlsServer implements BouncyCastleTlsNotificationProducer {
	
	private SecureRandom random = new SecureRandom();
    
    private LinkedList<BouncyCastleTlsNotificationListener> listeners = new LinkedList<BouncyCastleTlsNotificationListener>();
    
    protected boolean enableSessionTicketSupport = false;
    protected boolean enableSessionIdSupport = false;
    protected boolean allowSessionResumption = false;
    private boolean clientSentSessionTicketExtension = false;
    
	protected final byte[] DUMMY_TICKET_DATA = new byte[] {0x01, 0x02, 0x03, 0x04, 0x01, 0x02, 0x03, 0x04, 0x01, 0x02, 0x03, 0x04, 0x01, 0x02, 0x03, 0x04, 0x01, 0x02, 0x03, 0x04, 0x01, 0x02, 0x03, 0x04, 0x01, 0x02, 0x03, 0x04, 0x01, 0x02, 0x03, 0x04};
	//protected final byte[] DUMMY_SESSION_ID = new byte[] {0x05, 0x06, 0x07, 0x08, 0x05, 0x06, 0x07, 0x08};
    
	protected TlsSession resumableSession = null;
	protected HashMap<String, TlsSession> resumableSessions = new HashMap<String, TlsSession>();
    
    public void setEnableSessionTicketSupport(boolean enableSessionTicketSupport)
    {
    	this.enableSessionTicketSupport = enableSessionTicketSupport;
    }
    
    public void setEnableSessionIdSupport(boolean enableSessionIdSupport)
    {
    	this.enableSessionIdSupport = enableSessionIdSupport;
    }
    
    public void setAllowSessionResumption(boolean allowSessionResumption)
    {
    	this.allowSessionResumption = allowSessionResumption;
    }

    private byte[] generateNewSessionId()
    {
    	byte[] newSessionId = new byte[16];
    	random.nextBytes(newSessionId);
    	return newSessionId;
    }
    
    private String mapSessionIdToString(byte[] sessionId)
    {
    	return javax.xml.bind.DatatypeConverter.printHexBinary(sessionId).toUpperCase();
    }
    
    @Override
    public TlsSession getResumableSession(byte[] requestedClientSessionID) throws IOException
    {
    	String sessionString = ( (requestedClientSessionID == null) ? ("null") : ("[length=" + requestedClientSessionID.length + "] " + mapSessionIdToString(requestedClientSessionID)) );
    	Logger.TLS.logState("TLS client sent session id: " + sessionString, LogLevel.Debug);

    	if( (requestedClientSessionID != null) && (requestedClientSessionID.length > 0) && enableSessionIdSupport)
    	{
    		TlsSession session = resumableSessions.remove(mapSessionIdToString(requestedClientSessionID));
	    	if(session != null && session.isResumable())
	    	{
	    		if(allowSessionResumption)
	    		{
			    	Logger.TLS.logState("TLS server resumes session: " + mapSessionIdToString(session.getSessionID()), LogLevel.Debug);
					return session;
	    		}
	    		else
	    		{
			    	Logger.TLS.logState("TLS server denies session resumption", LogLevel.Debug);
			    	session.invalidate();
	    			return null;
	    		}
	    	}
	    	else
	    	{
		    	Logger.TLS.logState("TLS server has no resumable session", LogLevel.Debug);
		    	return null;
	    	}
    	}
    	else
    	{
    		return super.getResumableSession(requestedClientSessionID);
    	}
    }
    
    @Override
    public TlsSession getNewResumableSession(byte[] requestedClientSessionID) throws IOException
    {
    	String sessionString = ((requestedClientSessionID == null) ? "null" : mapSessionIdToString(requestedClientSessionID));
    	Logger.TLS.logState("TLS called getNewResumableSession(): " + sessionString, LogLevel.Debug);

    	if(enableSessionIdSupport)
    	{
    		byte[] sid = generateNewSessionId();
        	Logger.TLS.logState("TLS server provides new resumable session with ID: " + mapSessionIdToString(sid), LogLevel.Debug);
    		return TlsUtils.importSession(sid, null);
    	}
    	else if(enableSessionTicketSupport)
    	{
	    	if(resumableSession == null)
	    	{
	    		byte[] sid = generateNewSessionId();
	        	Logger.TLS.logState("TLS server provides new resumable session with ID: " + mapSessionIdToString(sid), LogLevel.Debug);
	    		return TlsUtils.importSession(sid, null);
	    	}
	    	else
	    	{
	        	Logger.TLS.logState("TLS server already has a resumable session", LogLevel.Debug);
	    		return null;
	    	}
    	}
    	else
    	{
    		return super.getNewResumableSession(requestedClientSessionID);
    	}
    }
    
    @Override
    public TlsSession getResumableSession(byte[] sessionID, byte[] sessionTicket) throws IOException
    {
    	String sessionString = ( (sessionTicket == null) ? ("null") : ("[length=" + sessionTicket.length + "] " + javax.xml.bind.DatatypeConverter.printHexBinary(sessionTicket)) );
    	sessionString += " session ID: " + ( (sessionID == null) ? ("null") : ("[length=" + sessionID.length + "] " + mapSessionIdToString(sessionID)) );
    	Logger.TLS.logState("TLS client sent session ticket: " + sessionString, LogLevel.Debug);

    	if(enableSessionTicketSupport)
    	{
	    	if(resumableSession != null && resumableSession.isResumable())
	    	{
	    		if(allowSessionResumption)
	    		{
	    			if(sessionID != null && sessionID.length > 0)
	    			{
	    				// client sent his own session ID for this ticket, we must use it
	    				resumableSession = TlsUtils.importSession(sessionID, resumableSession.exportSessionParameters());
	    			}
			    	Logger.TLS.logState("TLS server resumes session: " + mapSessionIdToString(resumableSession.getSessionID()), LogLevel.Debug);
					return resumableSession;
	    		}
	    		else
	    		{
			    	Logger.TLS.logState("TLS server denies session resumption", LogLevel.Debug);
	    			return null;
	    		}
	    	}
	    	else
	    	{
		    	Logger.TLS.logState("TLS server has no resumable session", LogLevel.Debug);
		    	return null;
	    	}
    	}
    	else
    	{
    		return super.getResumableSession(sessionID, sessionTicket);
    	}
    }
    
    @Override
    public void notifyAlertRaised(short alertLevel, short alertDescription, String message, Throwable cause)
    {
        sendNotificationAlertRaised(alertLevel, alertDescription, message, cause);
        super.notifyAlertRaised(alertLevel, alertDescription, message, cause);
    }

    @Override
    public void notifyAlertReceived(short alertLevel, short alertDescription) {
        sendNotificationAlertReceived(alertLevel, alertDescription);
        super.notifyAlertReceived(alertLevel, alertDescription);
    }
    
    @Override
    public void notifyClientVersion(ProtocolVersion clientVersion) throws IOException {
        sendNotificationClientVersion(clientVersion);
        super.notifyClientVersion(clientVersion);
    }

    @Override
    public void notifyFallback(boolean isFallback) throws IOException {
        sendNotificationFallback(isFallback);
        super.notifyFallback(isFallback);
    }

    @Override
    public void notifyOfferedCipherSuites(int[] offeredCipherSuites) throws IOException {
        sendNotificationOfferedCipherSuites(offeredCipherSuites);
        super.notifyOfferedCipherSuites(offeredCipherSuites);
    }

    @Override
    public void notifyOfferedCompressionMethods(short[] offeredCompressionMethods) throws IOException {
        sendNotificationOfferedCompressionMethods(offeredCompressionMethods);
        super.notifyOfferedCompressionMethods(offeredCompressionMethods);
    }

    @Override
    public void notifyClientCertificate(Certificate clientCertificate) throws IOException {
        sendNotificationClientCertificate(clientCertificate);
        super.notifyClientCertificate(clientCertificate);
    }

    @Override
    public void notifySecureRenegotiation(boolean secureRenegotiation) throws IOException {
        sendNotificationSecureRenegotiation(secureRenegotiation);
        super.notifySecureRenegotiation(secureRenegotiation);
    }

    @Override
    public void notifyHandshakeComplete() throws IOException {
    	TlsSession session = this.context.getResumableSession();
    	
		if (enableSessionIdSupport)
		{
			if (session != null && session.isResumable())
			{
				resumableSessions.put(mapSessionIdToString(session.getSessionID()), session);
			}
		}
		else
		{
			resumableSession = session;
		}
		
    	String sessionString = "null";
    	if(session != null)
    	{
    		SessionParameters params = session.exportSessionParameters();
    		sessionString = "session id: " + javax.xml.bind.DatatypeConverter.printHexBinary(session.getSessionID());
    		sessionString += " resumable: " + session.isResumable();
    		sessionString += " cipher suite: " + BouncyCastleTlsHelper.convertCipherSuiteIntToString(params.getCipherSuite());
    		sessionString += " master secret: " + javax.xml.bind.DatatypeConverter.printHexBinary(params.getMasterSecret());
    		
    	}
    	Logger.TLS.logState("TLS handshake completed! TLS session: " + sessionString, LogLevel.Debug);

    	SecurityParameters parameters = this.context.getSecurityParameters();
    	String parametersString = "null";
    	if(parameters != null)
    	{
    		parametersString = " cipher suite: " + BouncyCastleTlsHelper.convertCipherSuiteIntToString(parameters.getCipherSuite());
    		parametersString += " master secret: " + javax.xml.bind.DatatypeConverter.printHexBinary(parameters.getMasterSecret());
    		
    	}
    	Logger.TLS.logState("TLS handshake completed! TLS session parameters: " + parametersString, LogLevel.Debug);
    	
        sendNotificationHandshakeComplete();
        super.notifyHandshakeComplete();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void processClientExtensions(Hashtable clientExtensions) throws IOException {
        if (clientExtensions != null)
        {
            sendNotificationEncryptThenMACExtension(TlsExtensionsUtils.hasEncryptThenMACExtension(clientExtensions));

            Vector clientSupportedSignatureAlgorithms = TlsUtils.getSignatureAlgorithmsExtension(clientExtensions);
            if (clientSupportedSignatureAlgorithms != null) {
                SignatureAndHashAlgorithm[] signatureAlgorithms = new SignatureAndHashAlgorithm[clientSupportedSignatureAlgorithms.size()];
                int i = 0;
                for (Object entry : clientSupportedSignatureAlgorithms) {
                    signatureAlgorithms[i++] = (SignatureAndHashAlgorithm) entry;
                }
                sendNotificationSignatureAlgorithmsExtension(signatureAlgorithms);
            }

            int[] clientNamedCurves = TlsECCUtils.getSupportedEllipticCurvesExtension(clientExtensions);
            if (clientNamedCurves != null) {
                sendNotificationSupportedEllipticCurvesExtension(clientNamedCurves);
            }

            short[] clientSupportedECPointFormats = TlsECCUtils.getSupportedPointFormatsExtension(clientExtensions);
            if (clientSupportedECPointFormats != null) {
                sendNotificationSupportedPointFormatsExtension(clientSupportedECPointFormats);
            }
            
            if(TlsExtensionsUtils.hasSessionTicketExtension(clientExtensions))
            {
            	clientSentSessionTicketExtension = true;
            	byte[] sessionTicketData = TlsExtensionsUtils.getSessionTicketExtension(clientExtensions);
            	
            	if(sessionTicketData != null && sessionTicketData.length > 0 && Arrays.areEqual(DUMMY_TICKET_DATA, sessionTicketData))
            	{
            		
            	}
            	
            	sendNotificationSessionTicketExtension(sessionTicketData);
            }
        }
        super.processClientExtensions(clientExtensions);
    }

	@Override
    public ProtocolVersion getServerVersion() throws IOException {
        ProtocolVersion version = super.getServerVersion();
        sendNotificationSelectedVersion(version);
        return version;
    }

    @Override
    public int getSelectedCipherSuite() throws IOException {
        int cipherSuite = super.getSelectedCipherSuite();
        sendNotificationSelectedCipherSuite(cipherSuite);
        return cipherSuite;
    }

    @SuppressWarnings("rawtypes")
	@Override
	public Hashtable getServerExtensions() throws IOException
	{
    	super.getServerExtensions();
    	
    	if(enableSessionTicketSupport && clientSentSessionTicketExtension)
    	{
    		TlsExtensionsUtils.addSessionTicketExtension(checkServerExtensions());
    	}
    	
		return serverExtensions;
	}

    
    @Override
	public NewSessionTicket getNewSessionTicket() throws IOException
	{
    	if(enableSessionTicketSupport)
    	{
        	NewSessionTicket ticket = new NewSessionTicket(0L, DUMMY_TICKET_DATA);
    		return ticket;
    	}
    	else
    	{
    		return super.getNewSessionTicket();
    	}
	}

	// BEGIN implementation of BouncyCastleTlsNotificationProducer
    @Override
    public boolean addNotificationListener(BouncyCastleTlsNotificationListener listener) {
        return listeners.add(listener);
    }

    @Override
    public boolean removeNotificationListener(BouncyCastleTlsNotificationListener listener) {
        return listeners.remove(listener);
    }

    @Override
    public void sendNotificationAlertRaised(short alertLevel, short alertDescription, String message, Throwable cause) {
        for(BouncyCastleTlsNotificationListener listener : listeners) {
            listener.notifyAlertRaised(alertLevel, alertDescription, message, cause);
        }
    }

    @Override
    public void sendNotificationAlertReceived(short alertLevel, short alertDescription) {
        for(BouncyCastleTlsNotificationListener listener : listeners) {
            listener.notifyAlertReceived(alertLevel, alertDescription);
        }
    }

    @Override
    public void sendNotificationClientVersion(ProtocolVersion clientVersion) {
        for(BouncyCastleTlsNotificationListener listener : listeners) {
            listener.notifyClientVersion(clientVersion);
        }
    }

    @Override
    public void sendNotificationFallback(boolean isFallback) {
        for(BouncyCastleTlsNotificationListener listener : listeners) {
            listener.notifyFallback(isFallback);
        }
    }

    @Override
    public void sendNotificationOfferedCipherSuites(int[] offeredCipherSuites) {
        for(BouncyCastleTlsNotificationListener listener : listeners) {
            listener.notifyOfferedCipherSuites(offeredCipherSuites);
        }
    }

    @Override
    public void sendNotificationOfferedCompressionMethods(short[] offeredCompressionMethods) {
        for(BouncyCastleTlsNotificationListener listener : listeners) {
            listener.notifyOfferedCompressionMethods(offeredCompressionMethods);
        }
    }

    @Override
    public void sendNotificationClientCertificate(Certificate clientCertificate) {
        for(BouncyCastleTlsNotificationListener listener : listeners) {
            listener.notifyClientCertificate(clientCertificate);
        }
    }

    @Override
    public void sendNotificationSecureRenegotiation(boolean secureRenegotiation) {
        for(BouncyCastleTlsNotificationListener listener : listeners) {
            listener.notifySecureRenegotiation(secureRenegotiation);
        }
    }

    @Override
    public void sendNotificationHandshakeComplete() {
        for(BouncyCastleTlsNotificationListener listener : listeners) {
            listener.notifyHandshakeComplete();
        }
    }

    @Override
    public void sendNotificationEncryptThenMACExtension(boolean hasEncryptThenMACExtension) {
        for(BouncyCastleTlsNotificationListener listener : listeners) {
            listener.notifyEncryptThenMACExtension(hasEncryptThenMACExtension);
        }
    }

    @Override
    public void sendNotificationSignatureAlgorithmsExtension(SignatureAndHashAlgorithm[] signatureAlgorithms) {
        for(BouncyCastleTlsNotificationListener listener : listeners) {
            listener.notifySignatureAlgorithmsExtension(signatureAlgorithms);
        }
    }

    @Override
    public void sendNotificationSupportedEllipticCurvesExtension(int[] namedCurves) {
        for(BouncyCastleTlsNotificationListener listener : listeners) {
            listener.notifySupportedEllipticCurvesExtension(namedCurves);
        }
    }

    @Override
    public void sendNotificationSupportedPointFormatsExtension(short[] supportedECPointFormats) {
        for(BouncyCastleTlsNotificationListener listener : listeners) {
            listener.notifySupportedPointFormatsExtension(supportedECPointFormats);
        }
    }

    @Override
    public void sendNotificationSelectedVersion(ProtocolVersion clientVersion) {
        for(BouncyCastleTlsNotificationListener listener : listeners) {
            listener.notifySelectedVersion(clientVersion);
        }
    }

    @Override
    public void sendNotificationSelectedCipherSuite(int cipherSuite) {
        for(BouncyCastleTlsNotificationListener listener : listeners) {
            listener.notifySelectedCipherSuite(cipherSuite);
        }
    }

    @Override
    public void sendNotificationEnabledCipherSuites(int[] enabledCipherSuites) {
        for(BouncyCastleTlsNotificationListener listener : listeners) {
            listener.notifyEnabledCipherSuites(enabledCipherSuites);
        }
    }

    @Override
    public void sendNotificationEnabledMinimumVersion(ProtocolVersion minimumVersion) {
        for(BouncyCastleTlsNotificationListener listener : listeners) {
            listener.notifyEnabledMinimumVersion(minimumVersion);
        }
    }

    @Override
    public void sendNotificationEnabledMaximumVersion(ProtocolVersion maximumVersion) {
        for(BouncyCastleTlsNotificationListener listener : listeners) {
            listener.notifyEnabledMaximumVersion(maximumVersion);
        }
    }

    @Override
    public void sendNotificationSelectedDHParameters(final DHParameters dhParameters) {
        for(BouncyCastleTlsNotificationListener listener : listeners) {
            listener.notifySelectedDHParameters(dhParameters);
        }
    }

	@Override
	public void sendNotificationSessionTicketExtension(byte[] sessionTicketData)
	{
        for(BouncyCastleTlsNotificationListener listener : listeners) {
            listener.notifySessionTicketExtension(sessionTicketData);
        }
	}
    // END implementation of BouncyCastleTlsNotificationProducer

}
