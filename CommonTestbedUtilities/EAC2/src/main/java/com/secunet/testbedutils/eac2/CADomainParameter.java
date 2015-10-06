package com.secunet.testbedutils.eac2;

import java.security.spec.AlgorithmParameterSpec;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class CADomainParameter extends DHDomainParameter {
	public enum SymmetricCipher {
		UNKNOWN,
		DESEDE,
		AES
	}

	private int version = -1;
	private int keyId = -1;
	AlgorithmParameterSpec algorithmParameterSpec = null;
	
	public CADomainParameter( ASN1ObjectIdentifier protocol, int version,
			int keyId, AlgorithmIdentifier domainParameter, AlgorithmParameterSpec algorithmParameterSpec ) {
		super( protocol, domainParameter );
		this.version = version;
		this.keyId = keyId;
		this.algorithmParameterSpec = algorithmParameterSpec;
	}

	public int getVersion() {
		return version;
	}

	public int getKeyId() {
		return keyId;
	}

	public AlgorithmParameterSpec getAlgorithmParameterSpec() {
		return algorithmParameterSpec;
	}
}
