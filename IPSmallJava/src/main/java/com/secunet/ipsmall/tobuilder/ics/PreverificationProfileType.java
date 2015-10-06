//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2015.05.15 um 03:07:49 PM CEST 
//


package com.secunet.ipsmall.tobuilder.ics;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für PreverificationProfileType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="PreverificationProfileType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.secunet.com}ProfileType">
 *       &lt;attribute name="includedTrustpoint" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PreverificationProfileType")
public class PreverificationProfileType
    extends ProfileType
{

    @XmlAttribute(name = "includedTrustpoint", required = true)
    protected boolean includedTrustpoint;

    /**
     * Ruft den Wert der includedTrustpoint-Eigenschaft ab.
     * 
     */
    public boolean isIncludedTrustpoint() {
        return includedTrustpoint;
    }

    /**
     * Legt den Wert der includedTrustpoint-Eigenschaft fest.
     * 
     */
    public void setIncludedTrustpoint(boolean value) {
        this.includedTrustpoint = value;
    }

}
