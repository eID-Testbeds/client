
package com.secunet.ipsmall.tobuilder.ics;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für TR03110Type complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="TR03110Type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="PACE" maxOccurs="8">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="PACE-DH-GM-3DES-CBC-CBC"/>
 *               &lt;enumeration value="PACE-DH-GM-AES-CBC-CMAC-128"/>
 *               &lt;enumeration value="PACE-DH-GM-AES-CBC-CMAC-192"/>
 *               &lt;enumeration value="PACE-DH-GM-AES-CBC-CMAC-256"/>
 *               &lt;enumeration value="PACE-ECDH-GM-3DES-CBC-CBC"/>
 *               &lt;enumeration value="PACE-ECDH-GM-AES-CBC-CMAC-128"/>
 *               &lt;enumeration value="PACE-ECDH-GM-AES-CBC-CMAC-192"/>
 *               &lt;enumeration value="PACE-ECDH-GM-AES-CBC-CMAC-256"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="TA" maxOccurs="11" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="TA-RSA-v1-5-SHA-1"/>
 *               &lt;enumeration value="TA-RSA-v1-5-SHA-256"/>
 *               &lt;enumeration value="TA-RSA-v1-5-SHA-512"/>
 *               &lt;enumeration value="TA-RSA-PSS-SHA-1"/>
 *               &lt;enumeration value="TA-RSA-PSS-SHA-256"/>
 *               &lt;enumeration value="TA-RSA-PSS-SHA-512"/>
 *               &lt;enumeration value="TA-ECDSA-SHA-1"/>
 *               &lt;enumeration value="TA-ECDSA-SHA-224"/>
 *               &lt;enumeration value="TA-ECDSA-SHA-256"/>
 *               &lt;enumeration value="TA-ECDSA-SHA-384"/>
 *               &lt;enumeration value="TA-ECDSA-SHA-512"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="CA" maxOccurs="8" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="CA-DH-3DES-CBC-CBC"/>
 *               &lt;enumeration value="CA-DH-AES-CBC-CMAC-128"/>
 *               &lt;enumeration value="CA-DH-AES-CBC-CMAC-192"/>
 *               &lt;enumeration value="CA-DH-AES-CBC-CMAC-256"/>
 *               &lt;enumeration value="CA-ECDH-3DES-CBC-CBC"/>
 *               &lt;enumeration value="CA-ECDH-AES-CBC-CMAC-128"/>
 *               &lt;enumeration value="CA-ECDH-AES-CBC-CMAC-192"/>
 *               &lt;enumeration value="CA-ECDH-AES-CBC-CMAC-256"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
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
@XmlType(name = "TR03110Type", namespace = "http://www.secunet.com", propOrder = {
    "pace",
    "ta",
    "ca"
})
public class TR03110Type {

    @XmlElement(name = "PACE", namespace = "http://www.secunet.com", required = true)
    protected List<String> pace;
    @XmlElement(name = "TA", namespace = "http://www.secunet.com")
    protected List<String> ta;
    @XmlElement(name = "CA", namespace = "http://www.secunet.com")
    protected List<String> ca;

    /**
     * Gets the value of the pace property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the pace property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPACE().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getPACE() {
        if (pace == null) {
            pace = new ArrayList<String>();
        }
        return this.pace;
    }

    /**
     * Gets the value of the ta property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the ta property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTA().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getTA() {
        if (ta == null) {
            ta = new ArrayList<String>();
        }
        return this.ta;
    }

    /**
     * Gets the value of the ca property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the ca property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCA().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getCA() {
        if (ca == null) {
            ca = new ArrayList<String>();
        }
        return this.ca;
    }

}
