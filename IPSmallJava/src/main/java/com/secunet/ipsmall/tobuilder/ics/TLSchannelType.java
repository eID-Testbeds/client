//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2015.05.15 um 03:07:49 PM CEST 
//


package com.secunet.ipsmall.tobuilder.ics;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für TLSchannelType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="TLSchannelType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="TLS-Version" maxOccurs="unbounded">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="CipherSuite" type="{http://www.secunet.com}TLSCipherSuiteType" maxOccurs="unbounded"/>
 *                   &lt;element name="SupportedCurve" type="{http://www.secunet.com}TLSSupportedCurveType" maxOccurs="unbounded" minOccurs="0"/>
 *                   &lt;element name="MinRSAKeyLength" type="{http://www.secunet.com}TLSKeyLengthType" minOccurs="0"/>
 *                   &lt;element name="MinDSAKeyLength" type="{http://www.secunet.com}TLSKeyLengthType" minOccurs="0"/>
 *                   &lt;element name="MinDHEKeyLength" type="{http://www.secunet.com}TLSKeyLengthType" minOccurs="0"/>
 *                   &lt;element name="SupportedSignatureAlgorithm" type="{http://www.secunet.com}TLSSupportedSignatureAlgorithmType" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *                 &lt;attribute name="version" use="required" type="{http://www.secunet.com}TLSVersionType" />
 *                 &lt;attribute name="enabled" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TLSchannelType", propOrder = {
    "tlsVersion"
})
public class TLSchannelType {

    @XmlElement(name = "TLS-Version", required = true)
    protected List<TLSchannelType.TLSVersion> tlsVersion;

    /**
     * Gets the value of the tlsVersion property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the tlsVersion property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTLSVersion().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TLSchannelType.TLSVersion }
     * 
     * 
     */
    public List<TLSchannelType.TLSVersion> getTLSVersion() {
        if (tlsVersion == null) {
            tlsVersion = new ArrayList<TLSchannelType.TLSVersion>();
        }
        return this.tlsVersion;
    }


    /**
     * <p>Java-Klasse für anonymous complex type.
     * 
     * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="CipherSuite" type="{http://www.secunet.com}TLSCipherSuiteType" maxOccurs="unbounded"/>
     *         &lt;element name="SupportedCurve" type="{http://www.secunet.com}TLSSupportedCurveType" maxOccurs="unbounded" minOccurs="0"/>
     *         &lt;element name="MinRSAKeyLength" type="{http://www.secunet.com}TLSKeyLengthType" minOccurs="0"/>
     *         &lt;element name="MinDSAKeyLength" type="{http://www.secunet.com}TLSKeyLengthType" minOccurs="0"/>
     *         &lt;element name="MinDHEKeyLength" type="{http://www.secunet.com}TLSKeyLengthType" minOccurs="0"/>
     *         &lt;element name="SupportedSignatureAlgorithm" type="{http://www.secunet.com}TLSSupportedSignatureAlgorithmType" maxOccurs="unbounded" minOccurs="0"/>
     *       &lt;/sequence>
     *       &lt;attribute name="version" use="required" type="{http://www.secunet.com}TLSVersionType" />
     *       &lt;attribute name="enabled" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "cipherSuite",
        "supportedCurve",
        "minRSAKeyLength",
        "minDSAKeyLength",
        "minDHEKeyLength",
        "supportedSignatureAlgorithm"
    })
    public static class TLSVersion {

        @XmlElement(name = "CipherSuite", required = true)
        @XmlSchemaType(name = "string")
        protected List<TLSCipherSuiteType> cipherSuite;
        @XmlElement(name = "SupportedCurve")
        @XmlSchemaType(name = "string")
        protected List<TLSSupportedCurveType> supportedCurve;
        @XmlElement(name = "MinRSAKeyLength")
        @XmlSchemaType(name = "unsignedInt")
        protected Long minRSAKeyLength;
        @XmlElement(name = "MinDSAKeyLength")
        @XmlSchemaType(name = "unsignedInt")
        protected Long minDSAKeyLength;
        @XmlElement(name = "MinDHEKeyLength")
        @XmlSchemaType(name = "unsignedInt")
        protected Long minDHEKeyLength;
        @XmlElement(name = "SupportedSignatureAlgorithm")
        @XmlSchemaType(name = "string")
        protected List<TLSSupportedSignatureAlgorithmType> supportedSignatureAlgorithm;
        @XmlAttribute(name = "version", required = true)
        protected TLSVersionType version;
        @XmlAttribute(name = "enabled", required = true)
        protected boolean enabled;

        /**
         * Gets the value of the cipherSuite property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the cipherSuite property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getCipherSuite().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TLSCipherSuiteType }
         * 
         * 
         */
        public List<TLSCipherSuiteType> getCipherSuite() {
            if (cipherSuite == null) {
                cipherSuite = new ArrayList<TLSCipherSuiteType>();
            }
            return this.cipherSuite;
        }

        /**
         * Gets the value of the supportedCurve property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the supportedCurve property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getSupportedCurve().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TLSSupportedCurveType }
         * 
         * 
         */
        public List<TLSSupportedCurveType> getSupportedCurve() {
            if (supportedCurve == null) {
                supportedCurve = new ArrayList<TLSSupportedCurveType>();
            }
            return this.supportedCurve;
        }

        /**
         * Ruft den Wert der minRSAKeyLength-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link Long }
         *     
         */
        public Long getMinRSAKeyLength() {
            return minRSAKeyLength;
        }

        /**
         * Legt den Wert der minRSAKeyLength-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link Long }
         *     
         */
        public void setMinRSAKeyLength(Long value) {
            this.minRSAKeyLength = value;
        }

        /**
         * Ruft den Wert der minDSAKeyLength-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link Long }
         *     
         */
        public Long getMinDSAKeyLength() {
            return minDSAKeyLength;
        }

        /**
         * Legt den Wert der minDSAKeyLength-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link Long }
         *     
         */
        public void setMinDSAKeyLength(Long value) {
            this.minDSAKeyLength = value;
        }

        /**
         * Ruft den Wert der minDHEKeyLength-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link Long }
         *     
         */
        public Long getMinDHEKeyLength() {
            return minDHEKeyLength;
        }

        /**
         * Legt den Wert der minDHEKeyLength-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link Long }
         *     
         */
        public void setMinDHEKeyLength(Long value) {
            this.minDHEKeyLength = value;
        }

        /**
         * Gets the value of the supportedSignatureAlgorithm property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the supportedSignatureAlgorithm property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getSupportedSignatureAlgorithm().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TLSSupportedSignatureAlgorithmType }
         * 
         * 
         */
        public List<TLSSupportedSignatureAlgorithmType> getSupportedSignatureAlgorithm() {
            if (supportedSignatureAlgorithm == null) {
                supportedSignatureAlgorithm = new ArrayList<TLSSupportedSignatureAlgorithmType>();
            }
            return this.supportedSignatureAlgorithm;
        }

        /**
         * Ruft den Wert der version-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link TLSVersionType }
         *     
         */
        public TLSVersionType getVersion() {
            return version;
        }

        /**
         * Legt den Wert der version-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link TLSVersionType }
         *     
         */
        public void setVersion(TLSVersionType value) {
            this.version = value;
        }

        /**
         * Ruft den Wert der enabled-Eigenschaft ab.
         * 
         */
        public boolean isEnabled() {
            return enabled;
        }

        /**
         * Legt den Wert der enabled-Eigenschaft fest.
         * 
         */
        public void setEnabled(boolean value) {
            this.enabled = value;
        }

    }

}
