/**
 * CmisProperty.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.alfresco.repo.cmis.ws;

public class CmisProperty  implements java.io.Serializable {
    private java.lang.Object pdid;  // attribute

    private java.lang.Object localname;  // attribute

    private java.lang.Object displayname;  // attribute

    public CmisProperty() {
    }

    public CmisProperty(
           java.lang.Object pdid,
           java.lang.Object localname,
           java.lang.Object displayname) {
           this.pdid = pdid;
           this.localname = localname;
           this.displayname = displayname;
    }


    /**
     * Gets the pdid value for this CmisProperty.
     * 
     * @return pdid
     */
    public java.lang.Object getPdid() {
        return pdid;
    }


    /**
     * Sets the pdid value for this CmisProperty.
     * 
     * @param pdid
     */
    public void setPdid(java.lang.Object pdid) {
        this.pdid = pdid;
    }


    /**
     * Gets the localname value for this CmisProperty.
     * 
     * @return localname
     */
    public java.lang.Object getLocalname() {
        return localname;
    }


    /**
     * Sets the localname value for this CmisProperty.
     * 
     * @param localname
     */
    public void setLocalname(java.lang.Object localname) {
        this.localname = localname;
    }


    /**
     * Gets the displayname value for this CmisProperty.
     * 
     * @return displayname
     */
    public java.lang.Object getDisplayname() {
        return displayname;
    }


    /**
     * Sets the displayname value for this CmisProperty.
     * 
     * @param displayname
     */
    public void setDisplayname(java.lang.Object displayname) {
        this.displayname = displayname;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CmisProperty)) return false;
        CmisProperty other = (CmisProperty) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.pdid==null && other.getPdid()==null) || 
             (this.pdid!=null &&
              this.pdid.equals(other.getPdid()))) &&
            ((this.localname==null && other.getLocalname()==null) || 
             (this.localname!=null &&
              this.localname.equals(other.getLocalname()))) &&
            ((this.displayname==null && other.getDisplayname()==null) || 
             (this.displayname!=null &&
              this.displayname.equals(other.getDisplayname())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getPdid() != null) {
            _hashCode += getPdid().hashCode();
        }
        if (getLocalname() != null) {
            _hashCode += getLocalname().hashCode();
        }
        if (getDisplayname() != null) {
            _hashCode += getDisplayname().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(CmisProperty.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200901", "cmisProperty"));
        org.apache.axis.description.AttributeDesc attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("pdid");
        attrField.setXmlName(new javax.xml.namespace.QName("", "pdid"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("localname");
        attrField.setXmlName(new javax.xml.namespace.QName("", "localname"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("displayname");
        attrField.setXmlName(new javax.xml.namespace.QName("", "displayname"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(attrField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
