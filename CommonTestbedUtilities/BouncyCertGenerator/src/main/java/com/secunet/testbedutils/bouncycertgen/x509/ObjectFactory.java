//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2015.03.05 um 09:35:13 AM CET 
//


package com.secunet.testbedutils.bouncycertgen.x509;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.secunet.testbedutils.bouncycertgen.x509 package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.secunet.testbedutils.bouncycertgen.x509
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link CertificateDefinitions }
     * 
     */
    public CertificateDefinitions createCertificateDefinitions() {
        return new CertificateDefinitions();
    }

    /**
     * Create an instance of {@link CertificateDefinition }
     * 
     */
    public CertificateDefinition createCertificateDefinition() {
        return new CertificateDefinition();
    }

    /**
     * Create an instance of {@link SignatureAlgorithmTypeEC }
     * 
     */
    public SignatureAlgorithmTypeEC createSignatureAlgorithmTypeEC() {
        return new SignatureAlgorithmTypeEC();
    }

    /**
     * Create an instance of {@link SignatureAlgorithmTypeRSA }
     * 
     */
    public SignatureAlgorithmTypeRSA createSignatureAlgorithmTypeRSA() {
        return new SignatureAlgorithmTypeRSA();
    }

    /**
     * Create an instance of {@link SignatureAlgorithmType }
     * 
     */
    public SignatureAlgorithmType createSignatureAlgorithmType() {
        return new SignatureAlgorithmType();
    }

    /**
     * Create an instance of {@link BasicConstraintsType }
     * 
     */
    public BasicConstraintsType createBasicConstraintsType() {
        return new BasicConstraintsType();
    }

    /**
     * Create an instance of {@link ExtendedKeyUsageType }
     * 
     */
    public ExtendedKeyUsageType createExtendedKeyUsageType() {
        return new ExtendedKeyUsageType();
    }

    /**
     * Create an instance of {@link KeyUsageType }
     * 
     */
    public KeyUsageType createKeyUsageType() {
        return new KeyUsageType();
    }

    /**
     * Create an instance of {@link KeyFileType }
     * 
     */
    public KeyFileType createKeyFileType() {
        return new KeyFileType();
    }

    /**
     * Create an instance of {@link AltNameType }
     * 
     */
    public AltNameType createAltNameType() {
        return new AltNameType();
    }

    /**
     * Create an instance of {@link ExtensionsType }
     * 
     */
    public ExtensionsType createExtensionsType() {
        return new ExtensionsType();
    }

    /**
     * Create an instance of {@link DnType }
     * 
     */
    public DnType createDnType() {
        return new DnType();
    }

}
