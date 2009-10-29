/**
 * GetCheckedOutDocsResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.alfresco.repo.cmis.ws;

public class GetCheckedOutDocsResponse  implements java.io.Serializable {
    private org.alfresco.repo.cmis.ws.CmisObjectType[] object;

    private boolean hasMoreItems;

    public GetCheckedOutDocsResponse() {
    }

    public GetCheckedOutDocsResponse(
           org.alfresco.repo.cmis.ws.CmisObjectType[] object,
           boolean hasMoreItems) {
           this.object = object;
           this.hasMoreItems = hasMoreItems;
    }


    /**
     * Gets the object value for this GetCheckedOutDocsResponse.
     * 
     * @return object
     */
    public org.alfresco.repo.cmis.ws.CmisObjectType[] getObject() {
        return object;
    }


    /**
     * Sets the object value for this GetCheckedOutDocsResponse.
     * 
     * @param object
     */
    public void setObject(org.alfresco.repo.cmis.ws.CmisObjectType[] object) {
        this.object = object;
    }

    public org.alfresco.repo.cmis.ws.CmisObjectType getObject(int i) {
        return this.object[i];
    }

    public void setObject(int i, org.alfresco.repo.cmis.ws.CmisObjectType _value) {
        this.object[i] = _value;
    }


    /**
     * Gets the hasMoreItems value for this GetCheckedOutDocsResponse.
     * 
     * @return hasMoreItems
     */
    public boolean isHasMoreItems() {
        return hasMoreItems;
    }


    /**
     * Sets the hasMoreItems value for this GetCheckedOutDocsResponse.
     * 
     * @param hasMoreItems
     */
    public void setHasMoreItems(boolean hasMoreItems) {
        this.hasMoreItems = hasMoreItems;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GetCheckedOutDocsResponse)) return false;
        GetCheckedOutDocsResponse other = (GetCheckedOutDocsResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.object==null && other.getObject()==null) || 
             (this.object!=null &&
              java.util.Arrays.equals(this.object, other.getObject()))) &&
            this.hasMoreItems == other.isHasMoreItems();
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
        if (getObject() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getObject());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getObject(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        _hashCode += (isHasMoreItems() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GetCheckedOutDocsResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200901", ">getCheckedOutDocsResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("object");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200901", "object"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200901", "cmisObjectType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("hasMoreItems");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200901", "hasMoreItems"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
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
