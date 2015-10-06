//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2015.07.24 um 04:29:34 PM CEST 
//


package com.secunet.testbedutils.bouncycertgen.x509;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für DSAAlgorithms.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;simpleType name="DSAAlgorithms">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="SHA1withDSA"/>
 *     &lt;enumeration value="SHA224withDSA"/>
 *     &lt;enumeration value="SHA256withDSA"/>
 *     &lt;enumeration value="SHA384withDSA"/>
 *     &lt;enumeration value="SHA512withDSA"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "DSAAlgorithms")
@XmlEnum
public enum DSAAlgorithms {

    @XmlEnumValue("SHA1withDSA")
    SHA_1_WITH_DSA("SHA1withDSA"),
    @XmlEnumValue("SHA224withDSA")
    SHA_224_WITH_DSA("SHA224withDSA"),
    @XmlEnumValue("SHA256withDSA")
    SHA_256_WITH_DSA("SHA256withDSA"),
    @XmlEnumValue("SHA384withDSA")
    SHA_384_WITH_DSA("SHA384withDSA"),
    @XmlEnumValue("SHA512withDSA")
    SHA_512_WITH_DSA("SHA512withDSA");
    private final String value;

    DSAAlgorithms(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static DSAAlgorithms fromValue(String v) {
        for (DSAAlgorithms c: DSAAlgorithms.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
