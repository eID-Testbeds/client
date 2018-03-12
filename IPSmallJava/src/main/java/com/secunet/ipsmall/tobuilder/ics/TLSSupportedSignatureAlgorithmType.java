
package com.secunet.ipsmall.tobuilder.ics;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für TLSSupportedSignatureAlgorithmType.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;simpleType name="TLSSupportedSignatureAlgorithmType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="MD5withRSA"/>
 *     &lt;enumeration value="MD5withDSA"/>
 *     &lt;enumeration value="MD5withECDSA"/>
 *     &lt;enumeration value="SHA1withRSA"/>
 *     &lt;enumeration value="SHA1withDSA"/>
 *     &lt;enumeration value="SHA1withECDSA"/>
 *     &lt;enumeration value="SHA224withRSA"/>
 *     &lt;enumeration value="SHA224withDSA"/>
 *     &lt;enumeration value="SHA224withECDSA"/>
 *     &lt;enumeration value="SHA256withRSA"/>
 *     &lt;enumeration value="SHA256withDSA"/>
 *     &lt;enumeration value="SHA256withECDSA"/>
 *     &lt;enumeration value="SHA384withRSA"/>
 *     &lt;enumeration value="SHA384withDSA"/>
 *     &lt;enumeration value="SHA384withECDSA"/>
 *     &lt;enumeration value="SHA512withRSA"/>
 *     &lt;enumeration value="SHA512withDSA"/>
 *     &lt;enumeration value="SHA512withECDSA"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "TLSSupportedSignatureAlgorithmType", namespace = "http://www.secunet.com")
@XmlEnum
public enum TLSSupportedSignatureAlgorithmType {

    @XmlEnumValue("MD5withRSA")
    MD_5_WITH_RSA("MD5withRSA"),
    @XmlEnumValue("MD5withDSA")
    MD_5_WITH_DSA("MD5withDSA"),
    @XmlEnumValue("MD5withECDSA")
    MD_5_WITH_ECDSA("MD5withECDSA"),
    @XmlEnumValue("SHA1withRSA")
    SHA_1_WITH_RSA("SHA1withRSA"),
    @XmlEnumValue("SHA1withDSA")
    SHA_1_WITH_DSA("SHA1withDSA"),
    @XmlEnumValue("SHA1withECDSA")
    SHA_1_WITH_ECDSA("SHA1withECDSA"),
    @XmlEnumValue("SHA224withRSA")
    SHA_224_WITH_RSA("SHA224withRSA"),
    @XmlEnumValue("SHA224withDSA")
    SHA_224_WITH_DSA("SHA224withDSA"),
    @XmlEnumValue("SHA224withECDSA")
    SHA_224_WITH_ECDSA("SHA224withECDSA"),
    @XmlEnumValue("SHA256withRSA")
    SHA_256_WITH_RSA("SHA256withRSA"),
    @XmlEnumValue("SHA256withDSA")
    SHA_256_WITH_DSA("SHA256withDSA"),
    @XmlEnumValue("SHA256withECDSA")
    SHA_256_WITH_ECDSA("SHA256withECDSA"),
    @XmlEnumValue("SHA384withRSA")
    SHA_384_WITH_RSA("SHA384withRSA"),
    @XmlEnumValue("SHA384withDSA")
    SHA_384_WITH_DSA("SHA384withDSA"),
    @XmlEnumValue("SHA384withECDSA")
    SHA_384_WITH_ECDSA("SHA384withECDSA"),
    @XmlEnumValue("SHA512withRSA")
    SHA_512_WITH_RSA("SHA512withRSA"),
    @XmlEnumValue("SHA512withDSA")
    SHA_512_WITH_DSA("SHA512withDSA"),
    @XmlEnumValue("SHA512withECDSA")
    SHA_512_WITH_ECDSA("SHA512withECDSA");
    private final String value;

    TLSSupportedSignatureAlgorithmType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static TLSSupportedSignatureAlgorithmType fromValue(String v) {
        for (TLSSupportedSignatureAlgorithmType c: TLSSupportedSignatureAlgorithmType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
