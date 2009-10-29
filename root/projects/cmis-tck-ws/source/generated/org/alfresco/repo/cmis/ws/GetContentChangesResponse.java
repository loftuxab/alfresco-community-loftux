/**
 * GetContentChangesResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.alfresco.repo.cmis.ws;

public class GetContentChangesResponse  implements java.io.Serializable {
    private org.alfresco.repo.cmis.ws.CmisObjectType[] changedObject;

    private java.lang.String changeToken;

    public GetContentChangesResponse() {
    }

    public GetContentChangesResponse(
           org.alfresco.repo.cmis.ws.CmisObjectType[] changedObject,
           java.lang.String changeToken) {
           this.changedObject = changedObject;
           this.changeToken = changeToken;
    }


    /**
     * Gets the changedObject value for this GetContentChangesResponse.
     * 
     * @return changedObject
     */
    public org.alfresco.repo.cmis.ws.CmisObjectType[] getChangedObject() {
        return changedObject;
    }


    /**
     * Sets the changedObject value for this GetContentChangesResponse.
     * 
     * @param changedObject
     */
    public void setChangedObject(org.alfresco.repo.cmis.ws.CmisObjectType[] changedObject) {
        this.changedObject = changedObject;
    }

    public org.alfresco.repo.cmis.ws.CmisObjectType getChangedObject(int i) {
        return this.changedObject[i];
    }

    public void setChangedObject(int i, org.alfresco.repo.cmis.ws.CmisObjectType _value) {
        this.changedObject[i] = _value;
    }


    /**
     * Gets the changeToken value for this GetContentChangesResponse.
     * 
     * @return changeToken
     */
    public java.lang.String getChangeToken() {
        return changeToken;
    }


    /**
     * Sets the changeToken value for this GetContentChangesResponse.
     * 
     * @param changeToken
     */
    public void setChangeToken(java.lang.String changeToken) {
        this.changeToken = changeToken;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GetContentChangesResponse)) return false;
        GetContentChangesResponse other = (GetContentChangesResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.changedObject==null && other.getChangedObject()==null) || 
             (this.changedObject!=null &&
              java.util.Arrays.equals(this.changedObject, other.getChangedObject()))) &&
            ((this.changeToken==null && other.getChangeToken()==null) || 
             (this.changeToken!=null &&
              this.changeToken.equals(other.getChangeToken())));
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
        if (getChangedObject() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getChangedObject());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getChangedObject(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getChangeToken() != null) {
            _hashCode += getChangeToken().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GetContentChangesResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200901", ">getContentChangesResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("changedObject");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200901", "changedObject"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200901", "cmisObjectType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("changeToken");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200901", "changeToken"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
