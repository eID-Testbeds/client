//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2015.07.24 um 04:29:34 PM CEST 
//


package com.secunet.testbedutils.bouncycertgen.x509;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für extensionsType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="extensionsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="basicConstraints" type="{http://www.secunet.com}basicConstraintsType"/>
 *         &lt;element name="keyUsage" type="{http://www.secunet.com}keyUsageType" minOccurs="0"/>
 *         &lt;element name="extendedKeyUsage" type="{http://www.secunet.com}extendedKeyUsageType" minOccurs="0"/>
 *         &lt;element name="subjectAltName" type="{http://www.secunet.com}altNameType" minOccurs="0"/>
 *         &lt;element name="issuerAltName" type="{http://www.secunet.com}altNameType" minOccurs="0"/>
 *         &lt;element name="cRLDistributionPoints" type="{http://www.secunet.com}cRLDistributionPointsType" minOccurs="0"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "extensionsType", propOrder = {

})
public class ExtensionsType {

    @XmlElement(required = true)
    protected BasicConstraintsType basicConstraints;
    protected KeyUsageType keyUsage;
    protected ExtendedKeyUsageType extendedKeyUsage;
    protected AltNameType subjectAltName;
    protected AltNameType issuerAltName;
    @XmlElement(name = "cRLDistributionPoints")
    protected CRLDistributionPointsType crlDistributionPoints;

    /**
     * Ruft den Wert der basicConstraints-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BasicConstraintsType }
     *     
     */
    public BasicConstraintsType getBasicConstraints() {
        return basicConstraints;
    }

    /**
     * Legt den Wert der basicConstraints-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BasicConstraintsType }
     *     
     */
    public void setBasicConstraints(BasicConstraintsType value) {
        this.basicConstraints = value;
    }

    /**
     * Ruft den Wert der keyUsage-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link KeyUsageType }
     *     
     */
    public KeyUsageType getKeyUsage() {
        return keyUsage;
    }

    /**
     * Legt den Wert der keyUsage-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link KeyUsageType }
     *     
     */
    public void setKeyUsage(KeyUsageType value) {
        this.keyUsage = value;
    }

    /**
     * Ruft den Wert der extendedKeyUsage-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ExtendedKeyUsageType }
     *     
     */
    public ExtendedKeyUsageType getExtendedKeyUsage() {
        return extendedKeyUsage;
    }

    /**
     * Legt den Wert der extendedKeyUsage-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ExtendedKeyUsageType }
     *     
     */
    public void setExtendedKeyUsage(ExtendedKeyUsageType value) {
        this.extendedKeyUsage = value;
    }

    /**
     * Ruft den Wert der subjectAltName-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link AltNameType }
     *     
     */
    public AltNameType getSubjectAltName() {
        return subjectAltName;
    }

    /**
     * Legt den Wert der subjectAltName-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link AltNameType }
     *     
     */
    public void setSubjectAltName(AltNameType value) {
        this.subjectAltName = value;
    }

    /**
     * Ruft den Wert der issuerAltName-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link AltNameType }
     *     
     */
    public AltNameType getIssuerAltName() {
        return issuerAltName;
    }

    /**
     * Legt den Wert der issuerAltName-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link AltNameType }
     *     
     */
    public void setIssuerAltName(AltNameType value) {
        this.issuerAltName = value;
    }

    /**
     * Ruft den Wert der crlDistributionPoints-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link CRLDistributionPointsType }
     *     
     */
    public CRLDistributionPointsType getCRLDistributionPoints() {
        return crlDistributionPoints;
    }

    /**
     * Legt den Wert der crlDistributionPoints-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CRLDistributionPointsType }
     *     
     */
    public void setCRLDistributionPoints(CRLDistributionPointsType value) {
        this.crlDistributionPoints = value;
    }

}
