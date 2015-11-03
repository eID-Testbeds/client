//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2015.10.19 um 04:26:04 PM CEST 
//


package com.secunet.ipsmall.tobuilder.ics;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für TLSSupportedCurveType.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;simpleType name="TLSSupportedCurveType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="secp224r1"/>
 *     &lt;enumeration value="secp256r1"/>
 *     &lt;enumeration value="secp384r1"/>
 *     &lt;enumeration value="secp521r1"/>
 *     &lt;enumeration value="brainpoolP256r1"/>
 *     &lt;enumeration value="brainpoolP384r1"/>
 *     &lt;enumeration value="brainpoolP512r1"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "TLSSupportedCurveType")
@XmlEnum
public enum TLSSupportedCurveType {

    @XmlEnumValue("secp224r1")
    SECP_224_R_1("secp224r1"),
    @XmlEnumValue("secp256r1")
    SECP_256_R_1("secp256r1"),
    @XmlEnumValue("secp384r1")
    SECP_384_R_1("secp384r1"),
    @XmlEnumValue("secp521r1")
    SECP_521_R_1("secp521r1"),
    @XmlEnumValue("brainpoolP256r1")
    BRAINPOOL_P_256_R_1("brainpoolP256r1"),
    @XmlEnumValue("brainpoolP384r1")
    BRAINPOOL_P_384_R_1("brainpoolP384r1"),
    @XmlEnumValue("brainpoolP512r1")
    BRAINPOOL_P_512_R_1("brainpoolP512r1");
    private final String value;

    TLSSupportedCurveType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static TLSSupportedCurveType fromValue(String v) {
        for (TLSSupportedCurveType c: TLSSupportedCurveType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
