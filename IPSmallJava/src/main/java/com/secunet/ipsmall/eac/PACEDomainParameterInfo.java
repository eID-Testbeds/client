package com.secunet.ipsmall.eac;

import java.io.IOException;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.spec.DHParameterSpec;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.pkcs.DHParameter;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.jce.spec.ECParameterSpec;

public class PACEDomainParameterInfo extends SecurityInfo {
    // private static Logger log =
    // Logger.getLogger(PACEDomainParameterInfo.class);
    
    private AlgorithmIdentifier domainParameter = null;
    private int parameterId = -1;
    AlgorithmParameterSpec algorithmParameterSpec = null;
    
    public PACEDomainParameterInfo(ASN1ObjectIdentifier protocol, boolean doLog) {
        super(protocol, doLog);
        // if( doLog ) log.debug(
        // "created PACEDomainParameterInfo with protocol " + protocol );
    }
    
    @Override
    public void fromAsn1(ASN1Encodable required, ASN1Encodable optional) throws IOException, EIDException {
        ASN1Sequence algorithmIdentifier = (ASN1Sequence) required;
        AlgorithmIdentifier domainParameter = AlgorithmIdentifier.getInstance(algorithmIdentifier);
        this.domainParameter = domainParameter;
        // DERObjectIdentifier algorithm = domainParameter.getObjectId();
        ASN1Sequence parameters = (ASN1Sequence) domainParameter.getParameters();
        switch (DHDomainParameter.getType(protocol)) {
            case DH: {
                DHParameter params = DHParameter.getInstance(parameters);
                if (params.getL() != null) {
                    algorithmParameterSpec = new DHParameterSpec(params.getP(), params.getG(), params.getL().intValue());
                } else {
                    algorithmParameterSpec = new DHParameterSpec(params.getP(), params.getG());
                }
                break;
            }
            case ECDH: {
                X9ECParameters params = X9ECParameters.getInstance(parameters);
                algorithmParameterSpec = new ECParameterSpec(params.getCurve(), params.getG(), params.getN(),
                        params.getH(), params.getSeed());
                break;
            }
            default: {
                // ???
                break;
            }
        }
        if (null != optional) {
            parameterId = ASN1Helper.getCheckedInt((ASN1Integer) optional);
            // if( doLog ) log.debug( "read parameterId: " + parameterId );
        }
    }
    
    public AlgorithmIdentifier getDomainParameter() {
        return domainParameter;
    }
    
    public int getParameterId() {
        return parameterId;
    }
    
    public AlgorithmParameterSpec getAlgorithmParameterSpec() {
        return algorithmParameterSpec;
    }
    
    @Override
    boolean siEquals(SecurityInfo obj) {
        if (!(obj instanceof PACEDomainParameterInfo))
            return false;
        PACEDomainParameterInfo o = (PACEDomainParameterInfo) obj;
        if (!domainParameter.equals(o.domainParameter))
            return false;
        if (parameterId != o.parameterId)
            return false;
        if (!algorithmParameterSpec.equals(o.algorithmParameterSpec))
            return false;
        return true;
    }
}
