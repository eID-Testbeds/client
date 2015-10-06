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
 * <p>Java-Klasse für RSAAlgorithms.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;simpleType name="RSAAlgorithms">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="SHA224withRSA"/>
 *     &lt;enumeration value="SHA256withRSA"/>
 *     &lt;enumeration value="SHA384withRSA"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "RSAAlgorithms")
@XmlEnum
public enum RSAAlgorithms {

    @XmlEnumValue("SHA224withRSA")
    SHA_224_WITH_RSA("SHA224withRSA"),
    @XmlEnumValue("SHA256withRSA")
    SHA_256_WITH_RSA("SHA256withRSA"),
    @XmlEnumValue("SHA384withRSA")
    SHA_384_WITH_RSA("SHA384withRSA");
    private final String value;

    RSAAlgorithms(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static RSAAlgorithms fromValue(String v) {
        for (RSAAlgorithms c: RSAAlgorithms.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
