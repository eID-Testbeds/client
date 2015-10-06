package com.secunet.testbedutils.eac2.cv;

import java.util.LinkedList;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;

public class CardVerifiableCertificate {
    private final CVCBody body;
    private final byte[] signature;
    
    public CardVerifiableCertificate(byte[] data) throws EIDCertificateException {
        TLVObject cvc = TLVObject.generateFromBytes(data);
        if (cvc.getTag() != 0x7F21)
            throw new EIDCertificateException("data does not contain a CVCertificate. expected tag 0x7F21, got tag "
                    + cvc.getTag());
        LinkedList<TLVObject> parts = cvc.getValueAsTLVObjectList();
        TLVObject body = parts.removeFirst();
        if (body.getTag() != 0x7F4E)
            throw new EIDCertificateException(
                    "data does not contain a CVCertificate body. expected tag 0x7F4E, got tag " + cvc.getTag());
        this.body = new CVCBody(body.value);
        TLVObject signature = parts.removeFirst();
        if (signature.getTag() != 0x5F37)
            throw new EIDCertificateException(
                    "data does not contain a CVCertificate signature. expected tag 0x5F37, got tag " + cvc.getTag());
        this.signature = signature.value;
        
    }
    
    public byte[] getCertificationAuthorityReference() throws EIDCertificateException {
        return body.getAuthorityReference();
    }
    
    public byte[] getCertificateHolderAuthorization() throws EIDCertificateException {
        return body.getHolderAuthorizationTemplate();
    }
    
    public byte[] getCertificateHolderReference() throws EIDCertificateException {
        return body.getHolderReference();
    }
    
    public byte[] getCertificateDescriptionHash() throws EIDCertificateException {
        return body.getCertificateDescriptionHash();
    }
    
    public byte[] getBody() throws EIDCertificateException {
        return body.getData();
    }
    
    public byte[] getSignature() throws EIDCertificateException {
        return signature;
    }
    
    public ASN1ObjectIdentifier getAlgorithmOID() throws EIDCertificateException {
        return ASN1ObjectIdentifier.getInstance(body.getAlgorithmOID().toBytes());
    }
    
}
