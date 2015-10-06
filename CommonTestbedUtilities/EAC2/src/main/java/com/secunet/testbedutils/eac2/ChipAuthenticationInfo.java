package com.secunet.testbedutils.eac2;

import java.io.IOException;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;

public class ChipAuthenticationInfo extends SecurityInfo {
	
	
	private int version = -1;
	private int keyId = -1;

	public ChipAuthenticationInfo( ASN1ObjectIdentifier protocol, boolean doLog ) {
		super(protocol, doLog);
		
	}

	@Override
	public void fromAsn1( ASN1Encodable required, ASN1Encodable optional ) throws IOException, EIDException 
	{
		
		int version = ASN1Helper.getCheckedInt( (ASN1Integer)required );
		//if( doLog ) log.debug( "read version: " + version );
		
		if( 2 != version ) 
			throw new EIDException( "version must be 2" );
		this.version = version;
	
		if( null != optional ) 
		{
			keyId = ASN1Helper.getCheckedInt( (ASN1Integer)optional );
			
			//if( doLog ) log.debug( "read keyId: " + keyId );
		}
	}

	public int getVersion() {
		return version;
	}

	public int getKeyId() {
		return keyId;
	}

	@Override
	boolean siEquals( SecurityInfo obj ) {
		if( ! (obj instanceof ChipAuthenticationInfo) ) return false;
		ChipAuthenticationInfo o = (ChipAuthenticationInfo) obj;
		if( version != o.version ) return false;
		if( keyId != o.keyId ) return false;
		return true;
	}
}
