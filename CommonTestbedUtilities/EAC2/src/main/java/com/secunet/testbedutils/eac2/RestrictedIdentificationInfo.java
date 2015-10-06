package com.secunet.testbedutils.eac2;

import java.io.IOException;

import org.bouncycastle.asn1.ASN1Boolean;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;

public class RestrictedIdentificationInfo extends SecurityInfo {
	//private static Logger log = Logger.getLogger(RestrictedIdentificationInfo.class);
	
	private int version = -1;
	private int keyId = -1;
	private boolean authorizedOnly = false;
	private int maxKeyLen = -1;

	public RestrictedIdentificationInfo( ASN1ObjectIdentifier protocol, boolean doLog ) {
		super(protocol, doLog);
		//if( doLog ) log.debug( "created RestrictedIdentificationInfo with protocol " + protocol );
	}

	@Override
	public void fromAsn1( ASN1Encodable required, ASN1Encodable optional ) throws IOException, EIDException 
	{
		
		ASN1Sequence params = (ASN1Sequence) required;
		int version = ASN1Helper.getCheckedInt( (ASN1Integer) params.getObjectAt( 0 ) );
		//if( doLog ) log.debug( "read version: " + version );
		
		int keyId = ASN1Helper.getCheckedInt( (ASN1Integer) params.getObjectAt( 1 ) );
		//if( doLog ) log.debug( "read keyId: " + keyId );
		
		boolean authorizedOnly = ((ASN1Boolean) params.getObjectAt( 2 )).isTrue();
		//if( doLog ) log.debug( "read authorizedOnly: " + authorizedOnly );
		
		if( 1 != version ) 
			throw new EIDException( "version must be 1" );
		
		this.version = version;
		this.keyId = keyId;
		this.authorizedOnly = authorizedOnly;
		
		if( null != optional ) {
			maxKeyLen = ASN1Helper.getCheckedInt( (ASN1Integer)optional );
			//if( doLog ) log.debug( "read maxKeyLen: " + maxKeyLen );
		}
	}

	public int getVersion() {
		return version;
	}

	public int getKeyId() {
		return keyId;
	}

	public boolean isAuthorizedOnly() {
		return authorizedOnly;
	}

	public int getMaxKeyLength() {
		return maxKeyLen;
	}

	public ASN1ObjectIdentifier getProtocol() {
		return protocol;
	}
	
	@Override
	boolean siEquals( SecurityInfo obj ) {
		if( ! (obj instanceof RestrictedIdentificationInfo) ) return false;
		RestrictedIdentificationInfo o = (RestrictedIdentificationInfo) obj;
		if( version != o.version ) return false;
		if( keyId != o.keyId ) return false;
		if( authorizedOnly != o.authorizedOnly ) return false;
		if( maxKeyLen != o.maxKeyLen ) return false;
		return true;
	}

}
