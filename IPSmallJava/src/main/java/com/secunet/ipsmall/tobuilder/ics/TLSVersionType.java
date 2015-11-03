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
 * <p>Java-Klasse für TLSVersionType.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;simpleType name="TLSVersionType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="tls10"/>
 *     &lt;enumeration value="tls11"/>
 *     &lt;enumeration value="tls12"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "TLSVersionType")
@XmlEnum
public enum TLSVersionType {

    @XmlEnumValue("tls10")
    TLS_10("tls10"),
    @XmlEnumValue("tls11")
    TLS_11("tls11"),
    @XmlEnumValue("tls12")
    TLS_12("tls12");
    private final String value;

    TLSVersionType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static TLSVersionType fromValue(String v) {
        for (TLSVersionType c: TLSVersionType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
