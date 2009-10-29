/**
 * ApplyACLResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.alfresco.repo.cmis.ws;

public class ApplyACLResponse  implements java.io.Serializable {
    private org.alfresco.repo.cmis.ws.CmisAccessControlListType[] ACL;

    private boolean exact;

    public ApplyACLResponse() {
    }

    public ApplyACLResponse(
           org.alfresco.repo.cmis.ws.CmisAccessControlListType[] ACL,
           boolean exact) {
           this.ACL = ACL;
           this.exact = exact;
    }


    /**
     * Gets the ACL value for this ApplyACLResponse.
     * 
     * @return ACL
     */
    public org.alfresco.repo.cmis.ws.CmisAccessControlListType[] getACL() {
        return ACL;
    }


    /**
     * Sets the ACL value for this ApplyACLResponse.
     * 
     * @param ACL
     */
    public void setACL(org.alfresco.repo.cmis.ws.CmisAccessControlListType[] ACL) {
        this.ACL = ACL;
    }

    public org.alfresco.repo.cmis.ws.CmisAccessControlListType getACL(int i) {
        return this.ACL[i];
    }

    public void setACL(int i, org.alfresco.repo.cmis.ws.CmisAccessControlListType _value) {
        this.ACL[i] = _value;
    }


    /**
     * Gets the exact value for this ApplyACLResponse.
     * 
     * @return exact
     */
    public boolean isExact() {
        return exact;
    }


    /**
     * Sets the exact value for this ApplyACLResponse.
     * 
     * @param exact
     */
    public void setExact(boolean exact) {
        this.exact = exact;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ApplyACLResponse)) return false;
        ApplyACLResponse other = (ApplyACLResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.ACL==null && other.getACL()==null) || 
             (this.ACL!=null &&
              java.util.Arrays.equals(this.ACL, other.getACL()))) &&
            this.exact == other.isExact();
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
        if (getACL() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getACL());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getACL(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        _hashCode += (isExact() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ApplyACLResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200901", ">applyACLResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ACL");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200901", "ACL"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200901", "cmisAccessControlListType"));
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("exact");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200901", "exact"));
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
