package com.secunet.testbedutils.eac2.cv;

import java.util.LinkedList;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;

import com.secunet.testbedutils.eac2.EAC2ObjectIdentifiers;

public class CVCBody {
    
    private byte[] data = null;
    private byte[] authorityReference = null;
    private byte[] holderReference = null;
    private TLVObject algorithmOID = null;
    private byte[] holderAuthorizationTemplate = null;
    private byte[] certificateDescriptionHash = null;
    
    public CVCBody(byte[] data) throws EIDCertificateException {
        LinkedList<TLVObject> parts = TLVObject.generateMultipleFromBytes(data);
        TLVObject profile = parts.removeFirst();
        if (profile.getTag() != 0x5F29)
            throw new EIDCertificateException(
                    "CVCertificate body does not contain profile identifier. expected tag 0x5F29, got tag "
                            + profile.getTag());
        TLVObject authorityReference = parts.removeFirst();
        if (authorityReference.getTag() != 0x42)
            throw new EIDCertificateException(
                    "CVCertificate body does not contain authority reference. expected tag 0x42, got tag "
                            + authorityReference.getTag());
        TLVObject publicKey = parts.removeFirst();
        if (publicKey.getTag() != 0x7F49)
            throw new EIDCertificateException(
                    "CVCertificate body does not contain public key. expected tag 0x7F49, got tag "
                            + publicKey.getTag());
        LinkedList<TLVObject> publicKeyParts = TLVObject.generateMultipleFromBytes(publicKey.value);
        TLVObject publicKeyOID = publicKeyParts.removeFirst();
        if (publicKeyOID.getTag() != 0x06)
            throw new EIDCertificateException(
                    "CVCertificate public key does not contain public key OID. expected tag 0x06, got tag "
                            + publicKeyOID.getTag());
        TLVObject holderReference = parts.removeFirst();
        if (holderReference.getTag() != 0x5F20)
            throw new EIDCertificateException(
                    "CVCertificate body does not contain holderReference. expected tag 0x5F20, got tag "
                            + holderReference.getTag());
        TLVObject chat = parts.removeFirst();
        if (chat.getTag() != 0x7F4C)
            throw new EIDCertificateException(
                    "CVCertificate body does not contain certificate holder authorization template. expected tag 0x7F4C, got tag "
                            + chat.getTag());
        TLVObject effectiveDate = parts.removeFirst();
        if (effectiveDate.getTag() != 0x5F25)
            throw new EIDCertificateException(
                    "CVCertificate body does not contain effective date. expected tag 0x5F25, got tag "
                            + effectiveDate.getTag());
        TLVObject expirationDate = parts.removeFirst();
        if (expirationDate.getTag() != 0x5F24)
            throw new EIDCertificateException(
                    "CVCertificate body does not contain expiration date. expected tag 0x5F24, got tag "
                            + expirationDate.getTag());
        // extensions
        
        if (parts.size() > 0) {
            TLVObject certificateExtensions = parts.removeFirst();
            if (certificateExtensions.getTag() != 0x65)
                throw new EIDCertificateException(
                        "CVCertificate body does not contain correct extensions. expected tag 0x65, got tag "
                                + certificateExtensions.getTag());
            
            LinkedList<TLVObject> extensions = TLVObject.generateMultipleFromBytes(certificateExtensions.value);
            while (extensions.size() > 0) {
                TLVObject extension = extensions.removeFirst();
                
                if (extension.getTag() != 0x73)
                    throw new EIDCertificateException(
                            "CVCertificate body does not contain correct extension. expected tag 0x73, got tag "
                                    + extension.getTag());
                
                LinkedList<TLVObject> extensionParts = TLVObject.generateMultipleFromBytes(extension.value);
                TLVObject oidTLV = extensionParts.removeFirst();
                ASN1ObjectIdentifier oid = ASN1ObjectIdentifier.getInstance(oidTLV.toBytes());
                if (EAC2ObjectIdentifiers.id_description.equals(oid)) {
                    TLVObject certificateDescriptionHash80 = extensionParts.removeFirst();
                    if (certificateDescriptionHash80.getTag() != 0x80)
                        throw new EIDCertificateException(
                                "CVCertificate extension does not contain correct certificateDescriptionHash. expected tag 0x80, got tag "
                                        + extension.getTag());
                    certificateDescriptionHash = certificateDescriptionHash80.value;
                }
                
            }
        }
        this.data = data;
        this.authorityReference = authorityReference.value;
        this.holderReference = holderReference.value;
        this.algorithmOID = publicKeyOID;
        this.holderAuthorizationTemplate = chat.value;
    }
    
    public byte[] getData() {
        return data;
    }
    
    public byte[] getAuthorityReference() {
        return authorityReference;
    }
    
    public byte[] getHolderReference() {
        return holderReference;
    }
    
    public TLVObject getAlgorithmOID() {
        return algorithmOID;
    }
    
    public byte[] getHolderAuthorizationTemplate() {
        return holderAuthorizationTemplate;
    }
    
    public byte[] getCertificateDescriptionHash() {
        return certificateDescriptionHash;
    }
}
