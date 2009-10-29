/**
 * CmisPropertyHtml.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.alfresco.repo.cmis.ws;

public class CmisPropertyHtml  extends org.alfresco.repo.cmis.ws.CmisProperty  implements java.io.Serializable {
    private org.alfresco.repo.cmis.ws.CmisPropertyHtmlValue[] value;

    public CmisPropertyHtml() {
    }

    public CmisPropertyHtml(
           java.lang.Object pdid,
           java.lang.Object localname,
           java.lang.Object displayname,
           org.alfresco.repo.cmis.ws.CmisPropertyHtmlValue[] value) {
        super(
            pdid,
            localname,
            displayname);
        this.value = value;
    }


    /**
     * Gets the value value for this CmisPropertyHtml.
     * 
     * @return value
     */
    public org.alfresco.repo.cmis.ws.CmisPropertyHtmlValue[] getValue() {
        return value;
    }


    /**
     * Sets the value value for this CmisPropertyHtml.
     * 
     * @param value
     */
    public void setValue(org.alfresco.repo.cmis.ws.CmisPropertyHtmlValue[] value) {
        this.value = value;
    }

    public org.alfresco.repo.cmis.ws.CmisPropertyHtmlValue getValue(int i) {
        return this.value[i];
    }

    public void setValue(int i, org.alfresco.repo.cmis.ws.CmisPropertyHtmlValue _value) {
        this.value[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CmisPropertyHtml)) return false;
        CmisPropertyHtml other = (CmisPropertyHtml) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.value==null && other.getValue()==null) || 
             (this.value!=null &&
              java.util.Arrays.equals(this.value, other.getValue())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = super.hashCode();
        if (getValue() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getValue());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getValue(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(CmisPropertyHtml.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200901", "cmisPropertyHtml"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("value");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200901", "value"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200901", ">cmisPropertyHtml>value"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
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
