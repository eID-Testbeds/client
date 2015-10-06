package com.secunet.testbedutils.bouncycertgen;

import java.security.KeyPair;
import java.security.cert.X509Certificate;

import com.secunet.testbedutils.bouncycertgen.x509.CertificateDefinition;

public class GeneratedCertificate {
	private final X509Certificate certificate;
	private final KeyPair keyPair;
	private final CertificateDefinition definition;
	
	public GeneratedCertificate(CertificateDefinition definition, X509Certificate certificate, KeyPair keyPair) {
		this.certificate = certificate;
		this.keyPair = keyPair;
		this.definition = definition;
	}

	public X509Certificate getCertificate() {
		return certificate;
	}

	public KeyPair getKeyPair() {
		return keyPair;
	}

	public CertificateDefinition getDefinition() {
		return definition;
	}
}
