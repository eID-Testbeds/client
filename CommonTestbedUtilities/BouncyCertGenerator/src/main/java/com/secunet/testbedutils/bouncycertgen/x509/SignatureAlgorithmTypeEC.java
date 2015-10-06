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
 * <p>Java-Klasse für signatureAlgorithmTypeEC complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="signatureAlgorithmTypeEC">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.secunet.com>ECDSAAlgortihms">
 *       &lt;attribute name="curve" default="secp256k1">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="sect233k1"/>
 *             &lt;enumeration value="sect233r1"/>
 *             &lt;enumeration value="sect239k1"/>
 *             &lt;enumeration value="sect283k1"/>
 *             &lt;enumeration value="sect283r1"/>
 *             &lt;enumeration value="sect409k1"/>
 *             &lt;enumeration value="sect409r1"/>
 *             &lt;enumeration value="sect571k1"/>
 *             &lt;enumeration value="sect571r1"/>
 *             &lt;enumeration value="secp192k1"/>
 *             &lt;enumeration value="secp192r1"/>
 *             &lt;enumeration value="secp224k1"/>
 *             &lt;enumeration value="secp224r1"/>
 *             &lt;enumeration value="secp256k1"/>
 *             &lt;enumeration value="secp256r1"/>
 *             &lt;enumeration value="secp384r1"/>
 *             &lt;enumeration value="secp521r1"/>
 *             &lt;enumeration value="brainpoolP256r1"/>
 *             &lt;enumeration value="brainpoolP384r1"/>
 *             &lt;enumeration value="brainpoolP512r1"/>
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
@XmlType(name = "signatureAlgorithmTypeEC", propOrder = {
    "value"
})
public class SignatureAlgorithmTypeEC {

    @XmlValue
    protected ECDSAAlgortihms value;
    @XmlAttribute(name = "curve")
    protected String curve;

    /**
     * Ruft den Wert der value-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ECDSAAlgortihms }
     *     
     */
    public ECDSAAlgortihms getValue() {
        return value;
    }

    /**
     * Legt den Wert der value-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ECDSAAlgortihms }
     *     
     */
    public void setValue(ECDSAAlgortihms value) {
        this.value = value;
    }

    /**
     * Ruft den Wert der curve-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCurve() {
        if (curve == null) {
            return "secp256k1";
        } else {
            return curve;
        }
    }

    /**
     * Legt den Wert der curve-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCurve(String value) {
        this.curve = value;
    }

}
