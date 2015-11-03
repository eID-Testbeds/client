//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2015.10.19 um 04:26:04 PM CEST 
//


package com.secunet.ipsmall.tobuilder.ics;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für ClientTrustStoreCertificateType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="ClientTrustStoreCertificateType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="sequenceNumber" use="required" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" />
 *       &lt;attribute name="CAR" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="CHR" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ClientTrustStoreCertificateType")
public class ClientTrustStoreCertificateType {

    @XmlAttribute(name = "sequenceNumber", required = true)
    @XmlSchemaType(name = "unsignedInt")
    protected long sequenceNumber;
    @XmlAttribute(name = "CAR", required = true)
    protected String car;
    @XmlAttribute(name = "CHR", required = true)
    protected String chr;

    /**
     * Ruft den Wert der sequenceNumber-Eigenschaft ab.
     * 
     */
    public long getSequenceNumber() {
        return sequenceNumber;
    }

    /**
     * Legt den Wert der sequenceNumber-Eigenschaft fest.
     * 
     */
    public void setSequenceNumber(long value) {
        this.sequenceNumber = value;
    }

    /**
     * Ruft den Wert der car-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCAR() {
        return car;
    }

    /**
     * Legt den Wert der car-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCAR(String value) {
        this.car = value;
    }

    /**
     * Ruft den Wert der chr-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCHR() {
        return chr;
    }

    /**
     * Legt den Wert der chr-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCHR(String value) {
        this.chr = value;
    }

}
