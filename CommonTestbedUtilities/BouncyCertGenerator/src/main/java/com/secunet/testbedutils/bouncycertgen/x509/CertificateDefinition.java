//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2015.07.24 um 04:29:34 PM CEST 
//


package com.secunet.testbedutils.bouncycertgen.x509;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java-Klasse für certificateDefinition complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="certificateDefinition">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;choice>
 *           &lt;element name="serialNumber" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *           &lt;element name="serialNumberHex" type="{http://www.w3.org/2001/XMLSchema}hexBinary" minOccurs="0"/>
 *         &lt;/choice>
 *         &lt;element name="algorithmID" type="{http://www.secunet.com}signatureAlgorithmType"/>
 *         &lt;element name="issuer" type="{http://www.secunet.com}dnType"/>
 *         &lt;choice>
 *           &lt;element name="notBefore" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *           &lt;element name="notBeforeOffset" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;/choice>
 *         &lt;choice>
 *           &lt;element name="notAfter" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *           &lt;element name="notAfterOffset" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;/choice>
 *         &lt;element name="subject" type="{http://www.secunet.com}dnType"/>
 *         &lt;element name="extensions" type="{http://www.secunet.com}extensionsType" minOccurs="0"/>
 *         &lt;element name="keyFile" type="{http://www.secunet.com}keyFileType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "certificateDefinition", propOrder = {
    "name",
    "serialNumber",
    "serialNumberHex",
    "algorithmID",
    "issuer",
    "notBefore",
    "notBeforeOffset",
    "notAfter",
    "notAfterOffset",
    "subject",
    "extensions",
    "keyFile"
})
public class CertificateDefinition {

    @XmlElement(required = true)
    protected String name;
    @XmlElement(defaultValue = "42")
    protected BigInteger serialNumber;
    @XmlElement(type = String.class, defaultValue = "2A")
    @XmlJavaTypeAdapter(HexBinaryAdapter.class)
    @XmlSchemaType(name = "hexBinary")
    protected byte[] serialNumberHex;
    @XmlElement(required = true)
    protected SignatureAlgorithmType algorithmID;
    @XmlElement(required = true)
    protected DnType issuer;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar notBefore;
    protected Integer notBeforeOffset;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar notAfter;
    protected Integer notAfterOffset;
    @XmlElement(required = true)
    protected DnType subject;
    protected ExtensionsType extensions;
    protected KeyFileType keyFile;

    /**
     * Ruft den Wert der name-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Legt den Wert der name-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Ruft den Wert der serialNumber-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getSerialNumber() {
        return serialNumber;
    }

    /**
     * Legt den Wert der serialNumber-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setSerialNumber(BigInteger value) {
        this.serialNumber = value;
    }

    /**
     * Ruft den Wert der serialNumberHex-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public byte[] getSerialNumberHex() {
        return serialNumberHex;
    }

    /**
     * Legt den Wert der serialNumberHex-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSerialNumberHex(byte[] value) {
        this.serialNumberHex = value;
    }

    /**
     * Ruft den Wert der algorithmID-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link SignatureAlgorithmType }
     *     
     */
    public SignatureAlgorithmType getAlgorithmID() {
        return algorithmID;
    }

    /**
     * Legt den Wert der algorithmID-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link SignatureAlgorithmType }
     *     
     */
    public void setAlgorithmID(SignatureAlgorithmType value) {
        this.algorithmID = value;
    }

    /**
     * Ruft den Wert der issuer-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link DnType }
     *     
     */
    public DnType getIssuer() {
        return issuer;
    }

    /**
     * Legt den Wert der issuer-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link DnType }
     *     
     */
    public void setIssuer(DnType value) {
        this.issuer = value;
    }

    /**
     * Ruft den Wert der notBefore-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getNotBefore() {
        return notBefore;
    }

    /**
     * Legt den Wert der notBefore-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setNotBefore(XMLGregorianCalendar value) {
        this.notBefore = value;
    }

    /**
     * Ruft den Wert der notBeforeOffset-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getNotBeforeOffset() {
        return notBeforeOffset;
    }

    /**
     * Legt den Wert der notBeforeOffset-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setNotBeforeOffset(Integer value) {
        this.notBeforeOffset = value;
    }

    /**
     * Ruft den Wert der notAfter-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getNotAfter() {
        return notAfter;
    }

    /**
     * Legt den Wert der notAfter-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setNotAfter(XMLGregorianCalendar value) {
        this.notAfter = value;
    }

    /**
     * Ruft den Wert der notAfterOffset-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getNotAfterOffset() {
        return notAfterOffset;
    }

    /**
     * Legt den Wert der notAfterOffset-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setNotAfterOffset(Integer value) {
        this.notAfterOffset = value;
    }

    /**
     * Ruft den Wert der subject-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link DnType }
     *     
     */
    public DnType getSubject() {
        return subject;
    }

    /**
     * Legt den Wert der subject-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link DnType }
     *     
     */
    public void setSubject(DnType value) {
        this.subject = value;
    }

    /**
     * Ruft den Wert der extensions-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ExtensionsType }
     *     
     */
    public ExtensionsType getExtensions() {
        return extensions;
    }

    /**
     * Legt den Wert der extensions-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ExtensionsType }
     *     
     */
    public void setExtensions(ExtensionsType value) {
        this.extensions = value;
    }

    /**
     * Ruft den Wert der keyFile-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link KeyFileType }
     *     
     */
    public KeyFileType getKeyFile() {
        return keyFile;
    }

    /**
     * Legt den Wert der keyFile-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link KeyFileType }
     *     
     */
    public void setKeyFile(KeyFileType value) {
        this.keyFile = value;
    }

}
