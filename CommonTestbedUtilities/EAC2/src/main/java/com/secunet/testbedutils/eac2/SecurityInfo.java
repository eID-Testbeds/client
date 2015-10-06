package com.secunet.testbedutils.eac2;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.util.ASN1Dump;

public abstract class SecurityInfo {
	private static final Logger logger = Logger
			.getLogger(SecurityInfo.class.getName());
	protected ASN1ObjectIdentifier protocol;
	protected boolean doLog;
	
	public SecurityInfo( ASN1ObjectIdentifier protocol, boolean doLog ) {
		this.protocol = protocol;
		this.doLog = doLog;
	}
	
	public void dump( ASN1Encodable d ) {
		logger.log(Level.INFO, ASN1Dump.dumpAsString( d ));
	}
	
	@Override
	public final boolean equals( Object obj ) {
		if( this == obj ) return true;
		if( ! (obj instanceof SecurityInfo) ) return false;
		SecurityInfo o = (SecurityInfo) obj;
		if( ! protocol.equals( o.protocol ) ) return false;
		return  siEquals( o );
	}
	
	public ASN1ObjectIdentifier getProtocol()
	{
		return protocol;
	}
	abstract boolean siEquals( SecurityInfo obj );

	public abstract void fromAsn1( ASN1Encodable required, ASN1Encodable optional ) throws IOException, EIDException;
}
