
package com.secunet.ipsmall.tobuilder.ics;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für CCHProfileType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="CCHProfileType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.secunet.com}ProfileType">
 *       &lt;attribute name="trIndex_b" use="required" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CCHProfileType", namespace = "http://www.secunet.com")
public class CCHProfileType
    extends ProfileType
{

    @XmlAttribute(name = "trIndex_b", required = true)
    @XmlSchemaType(name = "unsignedInt")
    protected long trIndexB;

    /**
     * Ruft den Wert der trIndexB-Eigenschaft ab.
     * 
     */
    public long getTrIndexB() {
        return trIndexB;
    }

    /**
     * Legt den Wert der trIndexB-Eigenschaft fest.
     * 
     */
    public void setTrIndexB(long value) {
        this.trIndexB = value;
    }

}
