package com.secunet.testbedutils.eac2;

import java.security.spec.AlgorithmParameterSpec;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.jce.spec.ECParameterSpec;

public class DHDomainParameter {

	public enum Type {
		UNKNOWN,
		DH,
		ECDH
	}
	public enum SymmetricCipher {
		UNKNOWN,
		DESEDE,
		AES
	}

	protected ASN1ObjectIdentifier protocol;
	protected AlgorithmIdentifier domainParameter = null;

	public DHDomainParameter( ASN1ObjectIdentifier protocol, AlgorithmIdentifier domainParameter ) {
		this.protocol = protocol;
		this.domainParameter = domainParameter;
	}
	
	public boolean isCofactorGiven() {
		if( getType() != Type.ECDH ) return false;
		if( null == domainParameter ) return false;
		ASN1Encodable parameters = (ASN1Encodable) domainParameter.getParameters();
		if( null == parameters ) return false;
		
		if(parameters instanceof ASN1Integer)
		{
			AlgorithmParameterSpec spec = StandardizedDomainParameters.getParameters((((ASN1Integer) parameters).getPositiveValue()));
			if(spec instanceof ECParameterSpec)
			{
				return ((ECParameterSpec)spec).getH() != null; 
			}
		}
		else if(parameters instanceof ASN1Sequence)
		{
			if(( (ASN1Sequence)parameters).size() != 6 ) return false;
		}
		
		//
		return true;
	}
	
	public ASN1ObjectIdentifier getProtocol() {
		return protocol;
	}
	
	public AlgorithmIdentifier getDomainParameter() {
		return domainParameter;
	}

	public Type getType() {
		return getType( protocol );
	}

	public SymmetricCipher getSymmetricCipher() {
		return getSymmetricCipher( protocol );
	}

	public static Type getType( ASN1ObjectIdentifier oid ) {
		if( EAC2ObjectIdentifiers.id_CA_DH.equals( oid ) ||
			EAC2ObjectIdentifiers.id_CA_DH_3DES_CBC_CBC.equals( oid ) ||
			EAC2ObjectIdentifiers.id_CA_DH_AES_CBC_CMAC_128.equals( oid ) ||
			EAC2ObjectIdentifiers.id_CA_DH_AES_CBC_CMAC_192.equals( oid ) ||
			EAC2ObjectIdentifiers.id_CA_DH_AES_CBC_CMAC_256.equals( oid ) ) {
			return Type.DH;
		}
		if( EAC2ObjectIdentifiers.id_CA_ECDH.equals( oid ) ||
			EAC2ObjectIdentifiers.id_CA_ECDH_3DES_CBC_CBC.equals( oid ) ||
			EAC2ObjectIdentifiers.id_CA_ECDH_AES_CBC_CMAC_128.equals( oid ) ||
			EAC2ObjectIdentifiers.id_CA_ECDH_AES_CBC_CMAC_192.equals( oid ) ||
			EAC2ObjectIdentifiers.id_CA_ECDH_AES_CBC_CMAC_256.equals( oid ) ) {
			return Type.ECDH;
		}
		if( EAC2ObjectIdentifiers.id_PACE_DH_GM.equals( oid ) ||
			EAC2ObjectIdentifiers.id_PACE_DH_GM_3DES_CBC_CBC.equals( oid ) ||
			EAC2ObjectIdentifiers.id_PACE_DH_GM_AES_CBC_CMAC_128.equals( oid ) ||
			EAC2ObjectIdentifiers.id_PACE_DH_GM_AES_CBC_CMAC_192.equals( oid ) ||
			EAC2ObjectIdentifiers.id_PACE_DH_GM_AES_CBC_CMAC_256.equals( oid ) ||
			EAC2ObjectIdentifiers.id_PACE_DH_IM.equals( oid ) ||
			EAC2ObjectIdentifiers.id_PACE_DH_IM_3DES_CBC_CBC.equals( oid ) ||
			EAC2ObjectIdentifiers.id_PACE_DH_IM_AES_CBC_CMAC_128.equals( oid ) ||
			EAC2ObjectIdentifiers.id_PACE_DH_IM_AES_CBC_CMAC_192.equals( oid ) ||
			EAC2ObjectIdentifiers.id_PACE_DH_IM_AES_CBC_CMAC_256.equals( oid ) ) {
			return Type.DH;
		}
		if( EAC2ObjectIdentifiers.id_PACE_ECDH_GM.equals( oid ) ||
			EAC2ObjectIdentifiers.id_PACE_ECDH_GM_3DES_CBC_CBC.equals( oid ) ||
			EAC2ObjectIdentifiers.id_PACE_ECDH_GM_AES_CBC_CMAC_128.equals( oid ) ||
			EAC2ObjectIdentifiers.id_PACE_ECDH_GM_AES_CBC_CMAC_192.equals( oid ) ||
			EAC2ObjectIdentifiers.id_PACE_ECDH_GM_AES_CBC_CMAC_256.equals( oid ) ||
			EAC2ObjectIdentifiers.id_PACE_ECDH_IM.equals( oid ) ||
			EAC2ObjectIdentifiers.id_PACE_ECDH_IM_3DES_CBC_CBC.equals( oid ) ||
			EAC2ObjectIdentifiers.id_PACE_ECDH_IM_AES_CBC_CMAC_128.equals( oid ) ||
			EAC2ObjectIdentifiers.id_PACE_ECDH_IM_AES_CBC_CMAC_192.equals( oid ) ||
			EAC2ObjectIdentifiers.id_PACE_ECDH_IM_AES_CBC_CMAC_256.equals( oid ) ) {
			return Type.ECDH;
		}
			
		return Type.UNKNOWN;
	}

	public static SymmetricCipher getSymmetricCipher( ASN1ObjectIdentifier oid ) {
		if( EAC2ObjectIdentifiers.id_PACE_DH_GM_3DES_CBC_CBC.equals( oid ) ||
			EAC2ObjectIdentifiers.id_PACE_ECDH_GM_3DES_CBC_CBC.equals( oid ) ||
			EAC2ObjectIdentifiers.id_PACE_DH_IM_3DES_CBC_CBC.equals( oid ) ||
			EAC2ObjectIdentifiers.id_PACE_ECDH_IM_3DES_CBC_CBC.equals( oid ) ) {
			return SymmetricCipher.DESEDE;
		}
		if( EAC2ObjectIdentifiers.id_PACE_DH_GM_AES_CBC_CMAC_128.equals( oid ) ||
			EAC2ObjectIdentifiers.id_PACE_DH_GM_AES_CBC_CMAC_192.equals( oid ) ||
			EAC2ObjectIdentifiers.id_PACE_DH_GM_AES_CBC_CMAC_256.equals( oid ) ||
			EAC2ObjectIdentifiers.id_PACE_ECDH_GM_AES_CBC_CMAC_128.equals( oid ) ||
			EAC2ObjectIdentifiers.id_PACE_ECDH_GM_AES_CBC_CMAC_192.equals( oid ) ||
			EAC2ObjectIdentifiers.id_PACE_ECDH_GM_AES_CBC_CMAC_256.equals( oid ) ||
			EAC2ObjectIdentifiers.id_PACE_DH_IM_AES_CBC_CMAC_128.equals( oid ) ||
			EAC2ObjectIdentifiers.id_PACE_DH_IM_AES_CBC_CMAC_192.equals( oid ) ||
			EAC2ObjectIdentifiers.id_PACE_DH_IM_AES_CBC_CMAC_256.equals( oid ) ||
			EAC2ObjectIdentifiers.id_PACE_ECDH_IM_AES_CBC_CMAC_128.equals( oid ) ||
			EAC2ObjectIdentifiers.id_PACE_ECDH_IM_AES_CBC_CMAC_192.equals( oid ) ||
			EAC2ObjectIdentifiers.id_PACE_ECDH_IM_AES_CBC_CMAC_256.equals( oid ) ) {
			return SymmetricCipher.AES;
		}
		if( EAC2ObjectIdentifiers.id_CA_DH_3DES_CBC_CBC.equals( oid ) ||
			EAC2ObjectIdentifiers.id_CA_ECDH_3DES_CBC_CBC.equals( oid ) ) {
			return SymmetricCipher.DESEDE;
		}
		if( EAC2ObjectIdentifiers.id_CA_DH_AES_CBC_CMAC_128.equals( oid ) ||
			EAC2ObjectIdentifiers.id_CA_DH_AES_CBC_CMAC_192.equals( oid ) ||
			EAC2ObjectIdentifiers.id_CA_DH_AES_CBC_CMAC_256.equals( oid ) ||
			EAC2ObjectIdentifiers.id_CA_ECDH_AES_CBC_CMAC_128.equals( oid ) ||
			EAC2ObjectIdentifiers.id_CA_ECDH_AES_CBC_CMAC_192.equals( oid ) ||
			EAC2ObjectIdentifiers.id_CA_ECDH_AES_CBC_CMAC_256.equals( oid ) ) {
			return SymmetricCipher.AES;
		}
		return SymmetricCipher.UNKNOWN;
	}

}