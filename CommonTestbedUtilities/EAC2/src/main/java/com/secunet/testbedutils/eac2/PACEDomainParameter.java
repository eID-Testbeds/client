package com.secunet.testbedutils.eac2;

import java.security.spec.AlgorithmParameterSpec;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class PACEDomainParameter extends DHDomainParameter {
	public enum Mapping {
		UNKNOWN,
		GM,
		IM
	}

	private int version = -1;
	private int parameterId = -1;
	AlgorithmParameterSpec algorithmParameterSpec = null;
	
	public PACEDomainParameter( ASN1ObjectIdentifier protocol, int version,
			int parameterId, AlgorithmIdentifier domainParameter, AlgorithmParameterSpec algorithmParameterSpec ) {
		super( protocol, domainParameter );
		this.version = version;
		this.parameterId = parameterId;
		this.algorithmParameterSpec = algorithmParameterSpec;
	}

	/**
	 * This constructor generates a copy of the given PACEDomainParameter and
	 * replaces the included AlgorithmParameterSpec with the second parameter.<br/>
	 * <br/>
	 * Attention! This constructor is intended to be used for PACE domain
	 * parameter mapping only. Redundant members of the copy are not
	 * synchronized to the given AlgorithmParameterSpec.
	 * 
	 * @param paceDomainParameter
	 *            the PACEDomainParameter to be copied
	 * @param algorithmParameterSpec
	 *            the AlgorithmParameterSpec to be inserted into the copied
	 *            PACEDomainParameter
	 */
	public PACEDomainParameter( PACEDomainParameter paceDomainParameter, AlgorithmParameterSpec algorithmParameterSpec ) {
		super( paceDomainParameter.protocol, paceDomainParameter.domainParameter );
		this.version = paceDomainParameter.version;
		this.parameterId = paceDomainParameter.parameterId;
		this.algorithmParameterSpec = algorithmParameterSpec;
	}

	public int getVersion() {
		return version;
	}

	public int getParameterId() {
		return parameterId;
	}

	public AlgorithmParameterSpec getAlgorithmParameterSpec() {
		return algorithmParameterSpec;
	}

	public Mapping getMapping() {
		return getMapping( protocol );
	}
	
	public static Mapping getMapping( ASN1ObjectIdentifier oid ) {
		if( EAC2ObjectIdentifiers.id_PACE_DH_GM.equals( oid ) ||
			EAC2ObjectIdentifiers.id_PACE_DH_GM_3DES_CBC_CBC.equals( oid ) ||
			EAC2ObjectIdentifiers.id_PACE_DH_GM_AES_CBC_CMAC_128.equals( oid ) ||
			EAC2ObjectIdentifiers.id_PACE_DH_GM_AES_CBC_CMAC_192.equals( oid ) ||
			EAC2ObjectIdentifiers.id_PACE_DH_GM_AES_CBC_CMAC_256.equals( oid ) ||
			EAC2ObjectIdentifiers.id_PACE_ECDH_GM.equals( oid ) ||
			EAC2ObjectIdentifiers.id_PACE_ECDH_GM_3DES_CBC_CBC.equals( oid ) ||
			EAC2ObjectIdentifiers.id_PACE_ECDH_GM_AES_CBC_CMAC_128.equals( oid ) ||
			EAC2ObjectIdentifiers.id_PACE_ECDH_GM_AES_CBC_CMAC_192.equals( oid ) ||
			EAC2ObjectIdentifiers.id_PACE_ECDH_GM_AES_CBC_CMAC_256.equals( oid ) ) {
			return Mapping.GM;
		}
		if( EAC2ObjectIdentifiers.id_PACE_DH_IM.equals( oid ) ||
			EAC2ObjectIdentifiers.id_PACE_DH_IM_3DES_CBC_CBC.equals( oid ) ||
			EAC2ObjectIdentifiers.id_PACE_DH_IM_AES_CBC_CMAC_128.equals( oid ) ||
			EAC2ObjectIdentifiers.id_PACE_DH_IM_AES_CBC_CMAC_192.equals( oid ) ||
			EAC2ObjectIdentifiers.id_PACE_DH_IM_AES_CBC_CMAC_256.equals( oid ) ||
			EAC2ObjectIdentifiers.id_PACE_ECDH_IM.equals( oid ) ||
			EAC2ObjectIdentifiers.id_PACE_ECDH_IM_3DES_CBC_CBC.equals( oid ) ||
			EAC2ObjectIdentifiers.id_PACE_ECDH_IM_AES_CBC_CMAC_128.equals( oid ) ||
			EAC2ObjectIdentifiers.id_PACE_ECDH_IM_AES_CBC_CMAC_192.equals( oid ) ||
			EAC2ObjectIdentifiers.id_PACE_ECDH_IM_AES_CBC_CMAC_256.equals( oid ) ) {
			return Mapping.IM;
		}
		return Mapping.UNKNOWN;
	}
	
}
