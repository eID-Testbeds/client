//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2015.07.24 um 04:29:34 PM CEST 
//


package com.secunet.testbedutils.bouncycertgen.x509;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für extendedKeyUsageType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="extendedKeyUsageType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="anyExtendedKeyUsage" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="serverAuth" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="clientAuth" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="codeSigning" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="emailProtection" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="timeStamping" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="OCSPSigning" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="ipsecEndSystem" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="ipsecTunnel" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="ipsecUser" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="smartcardlogon" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *       &lt;/all>
 *       &lt;attribute name="critical" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "extendedKeyUsageType", propOrder = {

})
public class ExtendedKeyUsageType {

    @XmlElement(defaultValue = "0")
    protected Boolean anyExtendedKeyUsage;
    @XmlElement(defaultValue = "0")
    protected Boolean serverAuth;
    @XmlElement(defaultValue = "0")
    protected Boolean clientAuth;
    @XmlElement(defaultValue = "0")
    protected Boolean codeSigning;
    @XmlElement(defaultValue = "0")
    protected Boolean emailProtection;
    @XmlElement(defaultValue = "0")
    protected Boolean timeStamping;
    @XmlElement(name = "OCSPSigning", defaultValue = "0")
    protected Boolean ocspSigning;
    @XmlElement(defaultValue = "0")
    protected Boolean ipsecEndSystem;
    @XmlElement(defaultValue = "0")
    protected Boolean ipsecTunnel;
    @XmlElement(defaultValue = "0")
    protected Boolean ipsecUser;
    @XmlElement(defaultValue = "0")
    protected Boolean smartcardlogon;
    @XmlAttribute(name = "critical")
    protected Boolean critical;

    /**
     * Ruft den Wert der anyExtendedKeyUsage-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAnyExtendedKeyUsage() {
        return anyExtendedKeyUsage;
    }

    /**
     * Legt den Wert der anyExtendedKeyUsage-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAnyExtendedKeyUsage(Boolean value) {
        this.anyExtendedKeyUsage = value;
    }

    /**
     * Ruft den Wert der serverAuth-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isServerAuth() {
        return serverAuth;
    }

    /**
     * Legt den Wert der serverAuth-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setServerAuth(Boolean value) {
        this.serverAuth = value;
    }

    /**
     * Ruft den Wert der clientAuth-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isClientAuth() {
        return clientAuth;
    }

    /**
     * Legt den Wert der clientAuth-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setClientAuth(Boolean value) {
        this.clientAuth = value;
    }

    /**
     * Ruft den Wert der codeSigning-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isCodeSigning() {
        return codeSigning;
    }

    /**
     * Legt den Wert der codeSigning-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setCodeSigning(Boolean value) {
        this.codeSigning = value;
    }

    /**
     * Ruft den Wert der emailProtection-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isEmailProtection() {
        return emailProtection;
    }

    /**
     * Legt den Wert der emailProtection-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setEmailProtection(Boolean value) {
        this.emailProtection = value;
    }

    /**
     * Ruft den Wert der timeStamping-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isTimeStamping() {
        return timeStamping;
    }

    /**
     * Legt den Wert der timeStamping-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setTimeStamping(Boolean value) {
        this.timeStamping = value;
    }

    /**
     * Ruft den Wert der ocspSigning-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isOCSPSigning() {
        return ocspSigning;
    }

    /**
     * Legt den Wert der ocspSigning-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setOCSPSigning(Boolean value) {
        this.ocspSigning = value;
    }

    /**
     * Ruft den Wert der ipsecEndSystem-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIpsecEndSystem() {
        return ipsecEndSystem;
    }

    /**
     * Legt den Wert der ipsecEndSystem-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIpsecEndSystem(Boolean value) {
        this.ipsecEndSystem = value;
    }

    /**
     * Ruft den Wert der ipsecTunnel-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIpsecTunnel() {
        return ipsecTunnel;
    }

    /**
     * Legt den Wert der ipsecTunnel-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIpsecTunnel(Boolean value) {
        this.ipsecTunnel = value;
    }

    /**
     * Ruft den Wert der ipsecUser-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIpsecUser() {
        return ipsecUser;
    }

    /**
     * Legt den Wert der ipsecUser-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIpsecUser(Boolean value) {
        this.ipsecUser = value;
    }

    /**
     * Ruft den Wert der smartcardlogon-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isSmartcardlogon() {
        return smartcardlogon;
    }

    /**
     * Legt den Wert der smartcardlogon-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setSmartcardlogon(Boolean value) {
        this.smartcardlogon = value;
    }

    /**
     * Ruft den Wert der critical-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isCritical() {
        if (critical == null) {
            return false;
        } else {
            return critical;
        }
    }

    /**
     * Legt den Wert der critical-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setCritical(Boolean value) {
        this.critical = value;
    }

}
