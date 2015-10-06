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

public class ChipAuthenticationDomainParameterInfo extends SecurityInfo {
    
    private AlgorithmIdentifier domainParameter = null;
    private int keyId = -1;
    AlgorithmParameterSpec algorithmParameterSpec = null;
    
    public ChipAuthenticationDomainParameterInfo(ASN1ObjectIdentifier protocol, boolean doLog) {
        super(protocol, doLog);
        // if( doLog ) log.debug(
        // "created ChipAuthenticationDomainParameterInfo with protocol " +
        // protocol );
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public void fromAsn1(ASN1Encodable required, ASN1Encodable optional) throws IOException, EIDException {
        ASN1Sequence algorithmIdentifier = (ASN1Sequence) required;
        AlgorithmIdentifier domainParameter = new AlgorithmIdentifier(algorithmIdentifier);
        this.domainParameter = domainParameter;
        // DERObjectIdentifier algorithm = domainParameter.getObjectId();
        ASN1Encodable parameters = domainParameter.getParameters();
        
        if (parameters instanceof ASN1Integer) {
            algorithmParameterSpec = StandardizedDomainParameters.getParameters(((ASN1Integer) parameters)
                    .getPositiveValue());
        } else {
            switch (DHDomainParameter.getType(protocol)) {
                case DH: {
                    DHParameter params = DHParameter.getInstance(parameters);
                    if (params.getL() != null) {
                        algorithmParameterSpec = new DHParameterSpec(params.getP(), params.getG(), params.getL()
                                .intValue());
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
        }
        if (null != optional) {
            keyId = ASN1Helper.getCheckedInt((ASN1Integer) optional);
            // if( doLog ) log.debug( "read keyId: " + keyId );
        }
    }
    
    public AlgorithmIdentifier getDomainParameter() {
        return domainParameter;
    }
    
    public int getKeyId() {
        return keyId;
    }
    
    public AlgorithmParameterSpec getAlgorithmParameterSpec() {
        return algorithmParameterSpec;
    }
    
    @Override
    boolean siEquals(SecurityInfo obj) {
        if (!(obj instanceof ChipAuthenticationDomainParameterInfo))
            return false;
        ChipAuthenticationDomainParameterInfo o = (ChipAuthenticationDomainParameterInfo) obj;
        if (!domainParameter.equals(o.domainParameter))
            return false;
        if (keyId != o.keyId)
            return false;
        if (!algorithmParameterSpec.equals(o.algorithmParameterSpec))
            return false;
        return true;
    }
}
