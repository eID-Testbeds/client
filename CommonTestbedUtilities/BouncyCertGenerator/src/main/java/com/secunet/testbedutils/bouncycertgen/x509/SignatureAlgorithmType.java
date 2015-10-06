//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2015.03.05 um 09:35:13 AM CET 
//


package com.secunet.testbedutils.bouncycertgen.x509;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für signatureAlgorithmType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="signatureAlgorithmType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element name="ECDSA" type="{http://www.secunet.com}signatureAlgorithmTypeEC"/>
 *         &lt;element name="RSA" type="{http://www.secunet.com}signatureAlgorithmTypeRSA"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "signatureAlgorithmType", propOrder = {
    "ecdsa",
    "rsa"
})
public class SignatureAlgorithmType {

    @XmlElement(name = "ECDSA")
    protected SignatureAlgorithmTypeEC ecdsa;
    @XmlElement(name = "RSA")
    protected SignatureAlgorithmTypeRSA rsa;

    /**
     * Ruft den Wert der ecdsa-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link SignatureAlgorithmTypeEC }
     *     
     */
    public SignatureAlgorithmTypeEC getECDSA() {
        return ecdsa;
    }

    /**
     * Legt den Wert der ecdsa-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link SignatureAlgorithmTypeEC }
     *     
     */
    public void setECDSA(SignatureAlgorithmTypeEC value) {
        this.ecdsa = value;
    }

    /**
     * Ruft den Wert der rsa-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link SignatureAlgorithmTypeRSA }
     *     
     */
    public SignatureAlgorithmTypeRSA getRSA() {
        return rsa;
    }

    /**
     * Legt den Wert der rsa-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link SignatureAlgorithmTypeRSA }
     *     
     */
    public void setRSA(SignatureAlgorithmTypeRSA value) {
        this.rsa = value;
    }

}
