/**
 * GetTypeChildrenResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.alfresco.repo.cmis.ws;

public class GetTypeChildrenResponse  implements java.io.Serializable {
    private org.alfresco.repo.cmis.ws.CmisTypeDefinitionType[] type;

    private boolean hasMoreItems;

    public GetTypeChildrenResponse() {
    }

    public GetTypeChildrenResponse(
           org.alfresco.repo.cmis.ws.CmisTypeDefinitionType[] type,
           boolean hasMoreItems) {
           this.type = type;
           this.hasMoreItems = hasMoreItems;
    }


    /**
     * Gets the type value for this GetTypeChildrenResponse.
     * 
     * @return type
     */
    public org.alfresco.repo.cmis.ws.CmisTypeDefinitionType[] getType() {
        return type;
    }


    /**
     * Sets the type value for this GetTypeChildrenResponse.
     * 
     * @param type
     */
    public void setType(org.alfresco.repo.cmis.ws.CmisTypeDefinitionType[] type) {
        this.type = type;
    }

    public org.alfresco.repo.cmis.ws.CmisTypeDefinitionType getType(int i) {
        return this.type[i];
    }

    public void setType(int i, org.alfresco.repo.cmis.ws.CmisTypeDefinitionType _value) {
        this.type[i] = _value;
    }


    /**
     * Gets the hasMoreItems value for this GetTypeChildrenResponse.
     * 
     * @return hasMoreItems
     */
    public boolean isHasMoreItems() {
        return hasMoreItems;
    }


    /**
     * Sets the hasMoreItems value for this GetTypeChildrenResponse.
     * 
     * @param hasMoreItems
     */
    public void setHasMoreItems(boolean hasMoreItems) {
        this.hasMoreItems = hasMoreItems;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GetTypeChildrenResponse)) return false;
        GetTypeChildrenResponse other = (GetTypeChildrenResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.type==null && other.getType()==null) || 
             (this.type!=null &&
              java.util.Arrays.equals(this.type, other.getType()))) &&
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
        if (getType() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getType());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getType(), i);
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
        new org.apache.axis.description.TypeDesc(GetTypeChildrenResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200901", ">getTypeChildrenResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("type");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200901", "type"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200901", "cmisTypeDefinitionType"));
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
