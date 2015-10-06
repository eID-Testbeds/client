//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2015.03.05 um 09:35:13 AM CET 
//


package com.secunet.testbedutils.bouncycertgen.x509;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für keyUsageType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="keyUsageType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="digitalSignature" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="nonRepudiation" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="keyEncipherment" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="dataEncipherment" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="keyAgreement" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="keyCertSign" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="cRLSign" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="encipherOnly" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="decipherOnly" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *       &lt;/all>
 *       &lt;attribute name="critical" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "keyUsageType", propOrder = {

})
public class KeyUsageType {

    @XmlElement(defaultValue = "0")
    protected Boolean digitalSignature;
    @XmlElement(defaultValue = "0")
    protected Boolean nonRepudiation;
    @XmlElement(defaultValue = "0")
    protected Boolean keyEncipherment;
    @XmlElement(defaultValue = "0")
    protected Boolean dataEncipherment;
    @XmlElement(defaultValue = "0")
    protected Boolean keyAgreement;
    @XmlElement(defaultValue = "0")
    protected Boolean keyCertSign;
    @XmlElement(name = "cRLSign", defaultValue = "0")
    protected Boolean crlSign;
    @XmlElement(defaultValue = "0")
    protected Boolean encipherOnly;
    @XmlElement(defaultValue = "0")
    protected Boolean decipherOnly;
    @XmlAttribute(name = "critical")
    protected Boolean critical;

    /**
     * Ruft den Wert der digitalSignature-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isDigitalSignature() {
        return digitalSignature;
    }

    /**
     * Legt den Wert der digitalSignature-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setDigitalSignature(Boolean value) {
        this.digitalSignature = value;
    }

    /**
     * Ruft den Wert der nonRepudiation-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isNonRepudiation() {
        return nonRepudiation;
    }

    /**
     * Legt den Wert der nonRepudiation-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setNonRepudiation(Boolean value) {
        this.nonRepudiation = value;
    }

    /**
     * Ruft den Wert der keyEncipherment-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isKeyEncipherment() {
        return keyEncipherment;
    }

    /**
     * Legt den Wert der keyEncipherment-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setKeyEncipherment(Boolean value) {
        this.keyEncipherment = value;
    }

    /**
     * Ruft den Wert der dataEncipherment-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isDataEncipherment() {
        return dataEncipherment;
    }

    /**
     * Legt den Wert der dataEncipherment-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setDataEncipherment(Boolean value) {
        this.dataEncipherment = value;
    }

    /**
     * Ruft den Wert der keyAgreement-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isKeyAgreement() {
        return keyAgreement;
    }

    /**
     * Legt den Wert der keyAgreement-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setKeyAgreement(Boolean value) {
        this.keyAgreement = value;
    }

    /**
     * Ruft den Wert der keyCertSign-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isKeyCertSign() {
        return keyCertSign;
    }

    /**
     * Legt den Wert der keyCertSign-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setKeyCertSign(Boolean value) {
        this.keyCertSign = value;
    }

    /**
     * Ruft den Wert der crlSign-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isCRLSign() {
        return crlSign;
    }

    /**
     * Legt den Wert der crlSign-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setCRLSign(Boolean value) {
        this.crlSign = value;
    }

    /**
     * Ruft den Wert der encipherOnly-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isEncipherOnly() {
        return encipherOnly;
    }

    /**
     * Legt den Wert der encipherOnly-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setEncipherOnly(Boolean value) {
        this.encipherOnly = value;
    }

    /**
     * Ruft den Wert der decipherOnly-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isDecipherOnly() {
        return decipherOnly;
    }

    /**
     * Legt den Wert der decipherOnly-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setDecipherOnly(Boolean value) {
        this.decipherOnly = value;
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
            return true;
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
