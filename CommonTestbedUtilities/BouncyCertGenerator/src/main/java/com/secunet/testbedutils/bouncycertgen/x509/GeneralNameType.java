//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2015.03.05 um 09:35:13 AM CET 
//


package com.secunet.testbedutils.bouncycertgen.x509;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für GeneralNameType.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;simpleType name="GeneralNameType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="rfc822Name"/>
 *     &lt;enumeration value="dNSName"/>
 *     &lt;enumeration value="uniformResourceIdentifier"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "GeneralNameType")
@XmlEnum
public enum GeneralNameType {

    @XmlEnumValue("rfc822Name")
    RFC_822_NAME("rfc822Name"),
    @XmlEnumValue("dNSName")
    D_NS_NAME("dNSName"),
    @XmlEnumValue("uniformResourceIdentifier")
    UNIFORM_RESOURCE_IDENTIFIER("uniformResourceIdentifier");
    private final String value;

    GeneralNameType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static GeneralNameType fromValue(String v) {
        for (GeneralNameType c: GeneralNameType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
