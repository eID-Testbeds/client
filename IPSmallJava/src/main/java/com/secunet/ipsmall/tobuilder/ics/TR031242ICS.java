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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


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
 *         &lt;element name="SoftwareVersion">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="VersionMajor" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="VersionMinor" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="VersionSubminor" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Profiles">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="CRYPTO" type="{http://www.secunet.com}ProfileType"/>
 *                   &lt;element name="EAC" type="{http://www.secunet.com}ProfileType"/>
 *                   &lt;element name="OA" type="{http://www.secunet.com}ProfileType"/>
 *                   &lt;element name="PAOS" type="{http://www.secunet.com}ProfileType"/>
 *                   &lt;element name="UI_OFFLINE" type="{http://www.secunet.com}ProfileType"/>
 *                   &lt;element name="UI_ONLINE" type="{http://www.secunet.com}ProfileType"/>
 *                   &lt;element name="CCH" type="{http://www.secunet.com}CCHProfileType"/>
 *                   &lt;element name="PREVERIFICATION" type="{http://www.secunet.com}PreverificationProfileType"/>
 *                   &lt;element name="NO_PREVERIFICATION" type="{http://www.secunet.com}ProfileType"/>
 *                   &lt;element name="HTTP_MESSAGES" type="{http://www.secunet.com}ProfileType"/>
 *                   &lt;element name="PROXY_CONFIG" type="{http://www.secunet.com}ProfileType"/>
 *                   &lt;element name="PRESELECT_RIGHTS" type="{http://www.secunet.com}ProfileType"/>
 *                   &lt;element name="DISABLE_RIGHTS" type="{http://www.secunet.com}ProfileType"/>
 *                   &lt;element name="ACTION_STATUS" type="{http://www.secunet.com}ProfileType"/>
 *                   &lt;element name="ACTION_SHOWUI" type="{http://www.secunet.com}ProfileType"/>
 *                   &lt;element name="ACTION_SHOWUI_SETTINGS" type="{http://www.secunet.com}ProfileType"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="SupportedCryptography">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="TLSchannel_1-2" type="{http://www.secunet.com}TLSchannelType"/>
 *                   &lt;element name="TLSchannel_2" type="{http://www.secunet.com}TLSchannelType"/>
 *                   &lt;element name="TR-03110" type="{http://www.secunet.com}TR03110Type"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="CardReaderInterfaces">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="PCSC" type="{http://www.secunet.com}CardReaderInterfaceType"/>
 *                   &lt;element name="CCID" type="{http://www.secunet.com}CardReaderInterfaceType"/>
 *                   &lt;element name="Embedded" type="{http://www.secunet.com}CardReaderInterfaceType"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="ClientTrustStore">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Certificate" type="{http://www.secunet.com}ClientTrustStoreCertificateType" maxOccurs="7" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="version" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="1.2"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "softwareVersion",
    "profiles",
    "supportedCryptography",
    "cardReaderInterfaces",
    "clientTrustStore"
})
@XmlRootElement(name = "TR-03124-2_ICS")
public class TR031242ICS {

    @XmlElement(name = "SoftwareVersion", required = true)
    protected TR031242ICS.SoftwareVersion softwareVersion;
    @XmlElement(name = "Profiles", required = true)
    protected TR031242ICS.Profiles profiles;
    @XmlElement(name = "SupportedCryptography", required = true)
    protected TR031242ICS.SupportedCryptography supportedCryptography;
    @XmlElement(name = "CardReaderInterfaces", required = true)
    protected TR031242ICS.CardReaderInterfaces cardReaderInterfaces;
    @XmlElement(name = "ClientTrustStore", required = true)
    protected TR031242ICS.ClientTrustStore clientTrustStore;
    @XmlAttribute(name = "version", required = true)
    protected String version;

    /**
     * Ruft den Wert der softwareVersion-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link TR031242ICS.SoftwareVersion }
     *     
     */
    public TR031242ICS.SoftwareVersion getSoftwareVersion() {
        return softwareVersion;
    }

    /**
     * Legt den Wert der softwareVersion-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link TR031242ICS.SoftwareVersion }
     *     
     */
    public void setSoftwareVersion(TR031242ICS.SoftwareVersion value) {
        this.softwareVersion = value;
    }

    /**
     * Ruft den Wert der profiles-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link TR031242ICS.Profiles }
     *     
     */
    public TR031242ICS.Profiles getProfiles() {
        return profiles;
    }

    /**
     * Legt den Wert der profiles-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link TR031242ICS.Profiles }
     *     
     */
    public void setProfiles(TR031242ICS.Profiles value) {
        this.profiles = value;
    }

    /**
     * Ruft den Wert der supportedCryptography-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link TR031242ICS.SupportedCryptography }
     *     
     */
    public TR031242ICS.SupportedCryptography getSupportedCryptography() {
        return supportedCryptography;
    }

    /**
     * Legt den Wert der supportedCryptography-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link TR031242ICS.SupportedCryptography }
     *     
     */
    public void setSupportedCryptography(TR031242ICS.SupportedCryptography value) {
        this.supportedCryptography = value;
    }

    /**
     * Ruft den Wert der cardReaderInterfaces-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link TR031242ICS.CardReaderInterfaces }
     *     
     */
    public TR031242ICS.CardReaderInterfaces getCardReaderInterfaces() {
        return cardReaderInterfaces;
    }

    /**
     * Legt den Wert der cardReaderInterfaces-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link TR031242ICS.CardReaderInterfaces }
     *     
     */
    public void setCardReaderInterfaces(TR031242ICS.CardReaderInterfaces value) {
        this.cardReaderInterfaces = value;
    }

    /**
     * Ruft den Wert der clientTrustStore-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link TR031242ICS.ClientTrustStore }
     *     
     */
    public TR031242ICS.ClientTrustStore getClientTrustStore() {
        return clientTrustStore;
    }

    /**
     * Legt den Wert der clientTrustStore-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link TR031242ICS.ClientTrustStore }
     *     
     */
    public void setClientTrustStore(TR031242ICS.ClientTrustStore value) {
        this.clientTrustStore = value;
    }

    /**
     * Ruft den Wert der version-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVersion() {
        return version;
    }

    /**
     * Legt den Wert der version-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVersion(String value) {
        this.version = value;
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
     *         &lt;element name="PCSC" type="{http://www.secunet.com}CardReaderInterfaceType"/>
     *         &lt;element name="CCID" type="{http://www.secunet.com}CardReaderInterfaceType"/>
     *         &lt;element name="Embedded" type="{http://www.secunet.com}CardReaderInterfaceType"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "pcsc",
        "ccid",
        "embedded"
    })
    public static class CardReaderInterfaces {

        @XmlElement(name = "PCSC", required = true)
        protected CardReaderInterfaceType pcsc;
        @XmlElement(name = "CCID", required = true)
        protected CardReaderInterfaceType ccid;
        @XmlElement(name = "Embedded", required = true)
        protected CardReaderInterfaceType embedded;

        /**
         * Ruft den Wert der pcsc-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link CardReaderInterfaceType }
         *     
         */
        public CardReaderInterfaceType getPCSC() {
            return pcsc;
        }

        /**
         * Legt den Wert der pcsc-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link CardReaderInterfaceType }
         *     
         */
        public void setPCSC(CardReaderInterfaceType value) {
            this.pcsc = value;
        }

        /**
         * Ruft den Wert der ccid-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link CardReaderInterfaceType }
         *     
         */
        public CardReaderInterfaceType getCCID() {
            return ccid;
        }

        /**
         * Legt den Wert der ccid-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link CardReaderInterfaceType }
         *     
         */
        public void setCCID(CardReaderInterfaceType value) {
            this.ccid = value;
        }

        /**
         * Ruft den Wert der embedded-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link CardReaderInterfaceType }
         *     
         */
        public CardReaderInterfaceType getEmbedded() {
            return embedded;
        }

        /**
         * Legt den Wert der embedded-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link CardReaderInterfaceType }
         *     
         */
        public void setEmbedded(CardReaderInterfaceType value) {
            this.embedded = value;
        }

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
     *         &lt;element name="Certificate" type="{http://www.secunet.com}ClientTrustStoreCertificateType" maxOccurs="7" minOccurs="0"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "certificate"
    })
    public static class ClientTrustStore {

        @XmlElement(name = "Certificate")
        protected List<ClientTrustStoreCertificateType> certificate;

        /**
         * Gets the value of the certificate property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the certificate property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getCertificate().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link ClientTrustStoreCertificateType }
         * 
         * 
         */
        public List<ClientTrustStoreCertificateType> getCertificate() {
            if (certificate == null) {
                certificate = new ArrayList<ClientTrustStoreCertificateType>();
            }
            return this.certificate;
        }

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
     *         &lt;element name="CRYPTO" type="{http://www.secunet.com}ProfileType"/>
     *         &lt;element name="EAC" type="{http://www.secunet.com}ProfileType"/>
     *         &lt;element name="OA" type="{http://www.secunet.com}ProfileType"/>
     *         &lt;element name="PAOS" type="{http://www.secunet.com}ProfileType"/>
     *         &lt;element name="UI_OFFLINE" type="{http://www.secunet.com}ProfileType"/>
     *         &lt;element name="UI_ONLINE" type="{http://www.secunet.com}ProfileType"/>
     *         &lt;element name="CCH" type="{http://www.secunet.com}CCHProfileType"/>
     *         &lt;element name="PREVERIFICATION" type="{http://www.secunet.com}PreverificationProfileType"/>
     *         &lt;element name="NO_PREVERIFICATION" type="{http://www.secunet.com}ProfileType"/>
     *         &lt;element name="HTTP_MESSAGES" type="{http://www.secunet.com}ProfileType"/>
     *         &lt;element name="PROXY_CONFIG" type="{http://www.secunet.com}ProfileType"/>
     *         &lt;element name="PRESELECT_RIGHTS" type="{http://www.secunet.com}ProfileType"/>
     *         &lt;element name="DISABLE_RIGHTS" type="{http://www.secunet.com}ProfileType"/>
     *         &lt;element name="ACTION_STATUS" type="{http://www.secunet.com}ProfileType"/>
     *         &lt;element name="ACTION_SHOWUI" type="{http://www.secunet.com}ProfileType"/>
     *         &lt;element name="ACTION_SHOWUI_SETTINGS" type="{http://www.secunet.com}ProfileType"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "crypto",
        "eac",
        "oa",
        "paos",
        "uioffline",
        "uionline",
        "cch",
        "preverification",
        "nopreverification",
        "httpmessages",
        "proxyconfig",
        "preselectrights",
        "disablerights",
        "actionstatus",
        "actionshowui",
        "actionshowuisettings"
    })
    public static class Profiles {

        @XmlElement(name = "CRYPTO", required = true)
        protected ProfileType crypto;
        @XmlElement(name = "EAC", required = true)
        protected ProfileType eac;
        @XmlElement(name = "OA", required = true)
        protected ProfileType oa;
        @XmlElement(name = "PAOS", required = true)
        protected ProfileType paos;
        @XmlElement(name = "UI_OFFLINE", required = true)
        protected ProfileType uioffline;
        @XmlElement(name = "UI_ONLINE", required = true)
        protected ProfileType uionline;
        @XmlElement(name = "CCH", required = true)
        protected CCHProfileType cch;
        @XmlElement(name = "PREVERIFICATION", required = true)
        protected PreverificationProfileType preverification;
        @XmlElement(name = "NO_PREVERIFICATION", required = true)
        protected ProfileType nopreverification;
        @XmlElement(name = "HTTP_MESSAGES", required = true)
        protected ProfileType httpmessages;
        @XmlElement(name = "PROXY_CONFIG", required = true)
        protected ProfileType proxyconfig;
        @XmlElement(name = "PRESELECT_RIGHTS", required = true)
        protected ProfileType preselectrights;
        @XmlElement(name = "DISABLE_RIGHTS", required = true)
        protected ProfileType disablerights;
        @XmlElement(name = "ACTION_STATUS", required = true)
        protected ProfileType actionstatus;
        @XmlElement(name = "ACTION_SHOWUI", required = true)
        protected ProfileType actionshowui;
        @XmlElement(name = "ACTION_SHOWUI_SETTINGS", required = true)
        protected ProfileType actionshowuisettings;

        /**
         * Ruft den Wert der crypto-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link ProfileType }
         *     
         */
        public ProfileType getCRYPTO() {
            return crypto;
        }

        /**
         * Legt den Wert der crypto-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link ProfileType }
         *     
         */
        public void setCRYPTO(ProfileType value) {
            this.crypto = value;
        }

        /**
         * Ruft den Wert der eac-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link ProfileType }
         *     
         */
        public ProfileType getEAC() {
            return eac;
        }

        /**
         * Legt den Wert der eac-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link ProfileType }
         *     
         */
        public void setEAC(ProfileType value) {
            this.eac = value;
        }

        /**
         * Ruft den Wert der oa-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link ProfileType }
         *     
         */
        public ProfileType getOA() {
            return oa;
        }

        /**
         * Legt den Wert der oa-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link ProfileType }
         *     
         */
        public void setOA(ProfileType value) {
            this.oa = value;
        }

        /**
         * Ruft den Wert der paos-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link ProfileType }
         *     
         */
        public ProfileType getPAOS() {
            return paos;
        }

        /**
         * Legt den Wert der paos-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link ProfileType }
         *     
         */
        public void setPAOS(ProfileType value) {
            this.paos = value;
        }

        /**
         * Ruft den Wert der uioffline-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link ProfileType }
         *     
         */
        public ProfileType getUIOFFLINE() {
            return uioffline;
        }

        /**
         * Legt den Wert der uioffline-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link ProfileType }
         *     
         */
        public void setUIOFFLINE(ProfileType value) {
            this.uioffline = value;
        }

        /**
         * Ruft den Wert der uionline-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link ProfileType }
         *     
         */
        public ProfileType getUIONLINE() {
            return uionline;
        }

        /**
         * Legt den Wert der uionline-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link ProfileType }
         *     
         */
        public void setUIONLINE(ProfileType value) {
            this.uionline = value;
        }

        /**
         * Ruft den Wert der cch-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link CCHProfileType }
         *     
         */
        public CCHProfileType getCCH() {
            return cch;
        }

        /**
         * Legt den Wert der cch-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link CCHProfileType }
         *     
         */
        public void setCCH(CCHProfileType value) {
            this.cch = value;
        }

        /**
         * Ruft den Wert der preverification-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link PreverificationProfileType }
         *     
         */
        public PreverificationProfileType getPREVERIFICATION() {
            return preverification;
        }

        /**
         * Legt den Wert der preverification-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link PreverificationProfileType }
         *     
         */
        public void setPREVERIFICATION(PreverificationProfileType value) {
            this.preverification = value;
        }

        /**
         * Ruft den Wert der nopreverification-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link ProfileType }
         *     
         */
        public ProfileType getNOPREVERIFICATION() {
            return nopreverification;
        }

        /**
         * Legt den Wert der nopreverification-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link ProfileType }
         *     
         */
        public void setNOPREVERIFICATION(ProfileType value) {
            this.nopreverification = value;
        }

        /**
         * Ruft den Wert der httpmessages-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link ProfileType }
         *     
         */
        public ProfileType getHTTPMESSAGES() {
            return httpmessages;
        }

        /**
         * Legt den Wert der httpmessages-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link ProfileType }
         *     
         */
        public void setHTTPMESSAGES(ProfileType value) {
            this.httpmessages = value;
        }

        /**
         * Ruft den Wert der proxyconfig-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link ProfileType }
         *     
         */
        public ProfileType getPROXYCONFIG() {
            return proxyconfig;
        }

        /**
         * Legt den Wert der proxyconfig-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link ProfileType }
         *     
         */
        public void setPROXYCONFIG(ProfileType value) {
            this.proxyconfig = value;
        }

        /**
         * Ruft den Wert der preselectrights-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link ProfileType }
         *     
         */
        public ProfileType getPRESELECTRIGHTS() {
            return preselectrights;
        }

        /**
         * Legt den Wert der preselectrights-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link ProfileType }
         *     
         */
        public void setPRESELECTRIGHTS(ProfileType value) {
            this.preselectrights = value;
        }

        /**
         * Ruft den Wert der disablerights-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link ProfileType }
         *     
         */
        public ProfileType getDISABLERIGHTS() {
            return disablerights;
        }

        /**
         * Legt den Wert der disablerights-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link ProfileType }
         *     
         */
        public void setDISABLERIGHTS(ProfileType value) {
            this.disablerights = value;
        }

        /**
         * Ruft den Wert der actionstatus-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link ProfileType }
         *     
         */
        public ProfileType getACTIONSTATUS() {
            return actionstatus;
        }

        /**
         * Legt den Wert der actionstatus-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link ProfileType }
         *     
         */
        public void setACTIONSTATUS(ProfileType value) {
            this.actionstatus = value;
        }

        /**
         * Ruft den Wert der actionshowui-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link ProfileType }
         *     
         */
        public ProfileType getACTIONSHOWUI() {
            return actionshowui;
        }

        /**
         * Legt den Wert der actionshowui-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link ProfileType }
         *     
         */
        public void setACTIONSHOWUI(ProfileType value) {
            this.actionshowui = value;
        }

        /**
         * Ruft den Wert der actionshowuisettings-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link ProfileType }
         *     
         */
        public ProfileType getACTIONSHOWUISETTINGS() {
            return actionshowuisettings;
        }

        /**
         * Legt den Wert der actionshowuisettings-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link ProfileType }
         *     
         */
        public void setACTIONSHOWUISETTINGS(ProfileType value) {
            this.actionshowuisettings = value;
        }

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
     *         &lt;element name="Name" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="VersionMajor" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="VersionMinor" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="VersionSubminor" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "name",
        "versionMajor",
        "versionMinor",
        "versionSubminor"
    })
    public static class SoftwareVersion {

        @XmlElement(name = "Name", required = true)
        protected String name;
        @XmlElement(name = "VersionMajor", required = true)
        protected String versionMajor;
        @XmlElement(name = "VersionMinor", required = true)
        protected String versionMinor;
        @XmlElement(name = "VersionSubminor", required = true)
        protected String versionSubminor;

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
         * Ruft den Wert der versionMajor-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getVersionMajor() {
            return versionMajor;
        }

        /**
         * Legt den Wert der versionMajor-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setVersionMajor(String value) {
            this.versionMajor = value;
        }

        /**
         * Ruft den Wert der versionMinor-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getVersionMinor() {
            return versionMinor;
        }

        /**
         * Legt den Wert der versionMinor-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setVersionMinor(String value) {
            this.versionMinor = value;
        }

        /**
         * Ruft den Wert der versionSubminor-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getVersionSubminor() {
            return versionSubminor;
        }

        /**
         * Legt den Wert der versionSubminor-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setVersionSubminor(String value) {
            this.versionSubminor = value;
        }

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
     *         &lt;element name="TLSchannel_1-2" type="{http://www.secunet.com}TLSchannelType"/>
     *         &lt;element name="TLSchannel_2" type="{http://www.secunet.com}TLSchannelType"/>
     *         &lt;element name="TR-03110" type="{http://www.secunet.com}TR03110Type"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "tlSchannel12",
        "tlSchannel2",
        "tr03110"
    })
    public static class SupportedCryptography {

        @XmlElement(name = "TLSchannel_1-2", required = true)
        protected TLSchannelType tlSchannel12;
        @XmlElement(name = "TLSchannel_2", required = true)
        protected TLSchannelType tlSchannel2;
        @XmlElement(name = "TR-03110", required = true)
        protected TR03110Type tr03110;

        /**
         * Ruft den Wert der tlSchannel12-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link TLSchannelType }
         *     
         */
        public TLSchannelType getTLSchannel12() {
            return tlSchannel12;
        }

        /**
         * Legt den Wert der tlSchannel12-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link TLSchannelType }
         *     
         */
        public void setTLSchannel12(TLSchannelType value) {
            this.tlSchannel12 = value;
        }

        /**
         * Ruft den Wert der tlSchannel2-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link TLSchannelType }
         *     
         */
        public TLSchannelType getTLSchannel2() {
            return tlSchannel2;
        }

        /**
         * Legt den Wert der tlSchannel2-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link TLSchannelType }
         *     
         */
        public void setTLSchannel2(TLSchannelType value) {
            this.tlSchannel2 = value;
        }

        /**
         * Ruft den Wert der tr03110-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link TR03110Type }
         *     
         */
        public TR03110Type getTR03110() {
            return tr03110;
        }

        /**
         * Legt den Wert der tr03110-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link TR03110Type }
         *     
         */
        public void setTR03110(TR03110Type value) {
            this.tr03110 = value;
        }

    }

}
