//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2015.07.24 um 04:29:34 PM CEST 
//


package com.secunet.testbedutils.bouncycertgen.x509;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für cRLDistributionPointsType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="cRLDistributionPointsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="DistributionPoint" maxOccurs="unbounded">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="distributionPoint" type="{http://www.secunet.com}distributionPointType" minOccurs="0"/>
 *                   &lt;element name="cRLIssuer" type="{http://www.secunet.com}GeneralNamesType" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="critical" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cRLDistributionPointsType", propOrder = {
    "distributionPoint"
})
public class CRLDistributionPointsType {

    @XmlElement(name = "DistributionPoint", required = true)
    protected List<CRLDistributionPointsType.DistributionPoint> distributionPoint;
    @XmlAttribute(name = "critical")
    protected Boolean critical;

    /**
     * Gets the value of the distributionPoint property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the distributionPoint property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDistributionPoint().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CRLDistributionPointsType.DistributionPoint }
     * 
     * 
     */
    public List<CRLDistributionPointsType.DistributionPoint> getDistributionPoint() {
        if (distributionPoint == null) {
            distributionPoint = new ArrayList<CRLDistributionPointsType.DistributionPoint>();
        }
        return this.distributionPoint;
    }

    /**
     * Ruft den Wert der critical-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isCritical() {
        if (critical == null) {
            return false;
        } else {
            return critical;
        }
    }

    /**
     * Legt den Wert der critical-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setCritical(Boolean value) {
        this.critical = value;
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
     *         &lt;element name="distributionPoint" type="{http://www.secunet.com}distributionPointType" minOccurs="0"/>
     *         &lt;element name="cRLIssuer" type="{http://www.secunet.com}GeneralNamesType" minOccurs="0"/>
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
        "distributionPoint",
        "crlIssuer"
    })
    public static class DistributionPoint {

        protected DistributionPointType distributionPoint;
        @XmlElement(name = "cRLIssuer")
        protected GeneralNamesType crlIssuer;

        /**
         * Ruft den Wert der distributionPoint-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link DistributionPointType }
         *     
         */
        public DistributionPointType getDistributionPoint() {
            return distributionPoint;
        }

        /**
         * Legt den Wert der distributionPoint-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link DistributionPointType }
         *     
         */
        public void setDistributionPoint(DistributionPointType value) {
            this.distributionPoint = value;
        }

        /**
         * Ruft den Wert der crlIssuer-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link GeneralNamesType }
         *     
         */
        public GeneralNamesType getCRLIssuer() {
            return crlIssuer;
        }

        /**
         * Legt den Wert der crlIssuer-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link GeneralNamesType }
         *     
         */
        public void setCRLIssuer(GeneralNamesType value) {
            this.crlIssuer = value;
        }

    }

}
