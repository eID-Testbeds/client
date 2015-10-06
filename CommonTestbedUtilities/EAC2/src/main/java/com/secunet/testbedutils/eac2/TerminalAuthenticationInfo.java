package com.secunet.testbedutils.eac2;

import java.io.IOException;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;

public class TerminalAuthenticationInfo extends SecurityInfo {

	private int version = -1;

	public TerminalAuthenticationInfo( ASN1ObjectIdentifier protocol, boolean doLog ) {
		super(protocol, doLog);
		//if( doLog ) log.debug( "created TerminalAuthenticationInfo with protocol " + protocol );
	}

	@Override
	public void fromAsn1( ASN1Encodable required, ASN1Encodable optional ) throws IOException, EIDException {
		int version = ASN1Helper.getCheckedInt( (ASN1Integer)required );
		
		if( 2 != version ) 
			throw new EIDException( "version must be 2" );
		
		this.version = version;
		
		if( null != optional )  
			throw new EIDException( "optional efCVCA FileID not allowed in version 2" );
	}
	
	public int getVersion() {
		return version;
	}

	@Override
	boolean siEquals( SecurityInfo obj ) {
		if( ! (obj instanceof TerminalAuthenticationInfo) ) return false;
		TerminalAuthenticationInfo o = (TerminalAuthenticationInfo) obj;
		if( version != o.version ) return false;
		return true;
	}
}
