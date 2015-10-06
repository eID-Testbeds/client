package com.secunet.testbedutils.eac2;

import java.io.IOException;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;

public class PACEInfo extends SecurityInfo {
	//private static Logger log = Logger.getLogger(PACEInfo.class);
	
	private int version = -1;
	private int parameterId = -1;

	public PACEInfo( ASN1ObjectIdentifier protocol, boolean doLog ) {
		super(protocol, doLog);
		
		
		//if( doLog ) log.debug( "created PACEInfo with protocol " + protocol );
	}

	@Override
	public void fromAsn1( ASN1Encodable required, ASN1Encodable optional ) throws IOException, EIDException {
		int version = ASN1Helper.getCheckedInt( (ASN1Integer)required );
		//if( doLog ) log.debug( "read version: " + version );
		
		if( 2 != version ) 
			throw new EIDException( "version must be 2" );
		
		this.version = version;
		if( null != optional ) {
			parameterId = ASN1Helper.getCheckedInt( (ASN1Integer)optional );
			
			//if( doLog ) log.debug( "read parameterId: " + parameterId );
		}
	}

	public int getVersion() {
		return version;
	}

	public int getParameterId() {
		return parameterId;
	}

	@Override
	boolean siEquals( SecurityInfo obj ) 
	{
		if( ! (obj instanceof PACEInfo) ) return false;
		PACEInfo o = (PACEInfo) obj;
		if( version != o.version ) return false;
		if( parameterId != o.parameterId ) return false;
		return true;
	}

}
