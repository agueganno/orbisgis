//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.11.29 at 03:37:47 PM CET 
//


package net.opengis.se._2_0.core;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import net.opengis.fes._2.LiteralType;


/**
 * <p>Java class for MapItemType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MapItemType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/se/2.0/core}Key"/>
 *         &lt;element ref="{http://www.opengis.net/se/2.0/core}Value"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MapItemType", propOrder = {
    "key",
    "value"
})
public class MapItemType {

    @XmlElement(name = "Key", required = true)
    protected LiteralType key;
    @XmlElement(name = "Value", required = true)
    protected ParameterValueType value;

    /**
     * Gets the value of the key property.
     * 
     * @return
     *     possible object is
     *     {@link LiteralType }
     *     
     */
    public LiteralType getKey() {
        return key;
    }

    /**
     * Sets the value of the key property.
     * 
     * @param value
     *     allowed object is
     *     {@link LiteralType }
     *     
     */
    public void setKey(LiteralType value) {
        this.key = value;
    }

    /**
     * Gets the value of the value property.
     * 
     * @return
     *     possible object is
     *     {@link ParameterValueType }
     *     
     */
    public ParameterValueType getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     {@link ParameterValueType }
     *     
     */
    public void setValue(ParameterValueType value) {
        this.value = value;
    }

}