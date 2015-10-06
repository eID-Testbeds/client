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
 * <p>Java-Klasse für basicConstraintsType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="basicConstraintsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element name="cA" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="pathLenConstraint" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/choice>
 *       &lt;attribute name="critical" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "basicConstraintsType", propOrder = {
    "ca",
    "pathLenConstraint"
})
public class BasicConstraintsType {

    @XmlElement(name = "cA", defaultValue = "1")
    protected Boolean ca;
    protected Integer pathLenConstraint;
    @XmlAttribute(name = "critical")
    protected Boolean critical;

    /**
     * Ruft den Wert der ca-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isCA() {
        return ca;
    }

    /**
     * Legt den Wert der ca-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setCA(Boolean value) {
        this.ca = value;
    }

    /**
     * Ruft den Wert der pathLenConstraint-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getPathLenConstraint() {
        return pathLenConstraint;
    }

    /**
     * Legt den Wert der pathLenConstraint-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setPathLenConstraint(Integer value) {
        this.pathLenConstraint = value;
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
