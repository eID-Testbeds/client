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
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;


/**
 * <p>Java-Klasse für signatureAlgorithmTypeDSA complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="signatureAlgorithmTypeDSA">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.secunet.com>DSAAlgorithms">
 *       &lt;attribute name="keylength" default="2048">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
 *             &lt;enumeration value="512"/>
 *             &lt;enumeration value="768"/>
 *             &lt;enumeration value="1024"/>
 *             &lt;enumeration value="2048"/>
 *             &lt;enumeration value="3072"/>
 *             &lt;enumeration value="4096"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "signatureAlgorithmTypeDSA", propOrder = {
    "value"
})
public class SignatureAlgorithmTypeDSA {

    @XmlValue
    protected DSAAlgorithms value;
    @XmlAttribute(name = "keylength")
    protected Integer keylength;

    /**
     * Ruft den Wert der value-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link DSAAlgorithms }
     *     
     */
    public DSAAlgorithms getValue() {
        return value;
    }

    /**
     * Legt den Wert der value-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link DSAAlgorithms }
     *     
     */
    public void setValue(DSAAlgorithms value) {
        this.value = value;
    }

    /**
     * Ruft den Wert der keylength-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public int getKeylength() {
        if (keylength == null) {
            return  2048;
        } else {
            return keylength;
        }
    }

    /**
     * Legt den Wert der keylength-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setKeylength(Integer value) {
        this.keylength = value;
    }

}
