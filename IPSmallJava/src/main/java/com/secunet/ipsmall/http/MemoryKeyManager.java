package com.secunet.ipsmall.http;

import java.net.Socket;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509ExtendedKeyManager;

public class MemoryKeyManager extends X509ExtendedKeyManager
{

	PrivateKey m_serverKey = null;
	X509Certificate[] m_serverCertificateChain = null;
	
	public MemoryKeyManager(PrivateKey key , X509Certificate[] serverCertificateChain)
	{
		m_serverKey = key;
		m_serverCertificateChain = serverCertificateChain;
	}
	
	@Override
	public String chooseClientAlias(String[] arg0, Principal[] arg1, Socket arg2) {
		
		return "alias";
	}

	@Override
	public String chooseServerAlias(String arg0, Principal[] arg1, Socket arg2) {
		
		return "alias";
	}

	@Override
	public X509Certificate[] getCertificateChain(String arg0) {
		
		return m_serverCertificateChain;
	}

	@Override
	public String[] getClientAliases(String arg0, Principal[] arg1) {
		
		return null;
	}

	@Override
	public PrivateKey getPrivateKey(String arg0) {
		
		return m_serverKey;
	}

	@Override
	public String[] getServerAliases(String arg0, Principal[] arg1) {
		
		return new String[]{"alias"};
	}

}
