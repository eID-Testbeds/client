package com.secunet.testbedutils.eac2;

import java.io.IOException;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;

public class ChipAuthenticationPublicKeyInfo extends SecurityInfo {
	//private static Logger log = Logger.getLogger(ChipAuthenticationPublicKeyInfo.class.getName());
	
	private SubjectPublicKeyInfo subjectPublicKeyInfo = null;
	private int keyId = -1;

	public ChipAuthenticationPublicKeyInfo( ASN1ObjectIdentifier protocol, boolean doLog ) {
		super(protocol, doLog);
		
		//if( doLog ) log.debug( "created ChipAuthenticationPublicKeyInfo with protocol " + protocol );
	}

	@Override
	public void fromAsn1(ASN1Encodable required, ASN1Encodable optional) throws IOException, EIDException {
		ASN1Sequence chipAuthenticationPublicKey = (ASN1Sequence) required;
		subjectPublicKeyInfo = SubjectPublicKeyInfo.getInstance(chipAuthenticationPublicKey);
		if( null != optional ) {
			keyId = ASN1Helper.getCheckedInt( (ASN1Integer)optional );
			//if( doLog ) log.debug( "read keyId: " + keyId );
		}
	}

	public int getKeyId() {
		return keyId;
	}

	public SubjectPublicKeyInfo getSubjectPublicKeyInfo() {
		return subjectPublicKeyInfo;
	}

	@Override
	boolean siEquals( SecurityInfo obj ) {
		if( ! (obj instanceof ChipAuthenticationPublicKeyInfo) ) return false;
		ChipAuthenticationPublicKeyInfo o = (ChipAuthenticationPublicKeyInfo) obj;
		if( ! subjectPublicKeyInfo.equals( o.subjectPublicKeyInfo ) ) return false;
		if( keyId != o.keyId ) return false;
		return true;
	}
}
