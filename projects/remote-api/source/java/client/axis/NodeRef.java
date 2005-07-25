/**
 * NodeRef.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package client.axis;

public class NodeRef  implements java.io.Serializable {
    private java.lang.String id;
    private client.axis.StoreRef storeRef;

    public NodeRef() {
    }

    public NodeRef(
           java.lang.String id,
           client.axis.StoreRef storeRef) {
           this.id = id;
           this.storeRef = storeRef;
    }


    /**
     * Gets the id value for this NodeRef.
     * 
     * @return id
     */
    public java.lang.String getId() {
        return id;
    }


    /**
     * Sets the id value for this NodeRef.
     * 
     * @param id
     */
    public void setId(java.lang.String id) {
        this.id = id;
    }


    /**
     * Gets the storeRef value for this NodeRef.
     * 
     * @return storeRef
     */
    public client.axis.StoreRef getStoreRef() {
        return storeRef;
    }


    /**
     * Sets the storeRef value for this NodeRef.
     * 
     * @param storeRef
     */
    public void setStoreRef(client.axis.StoreRef storeRef) {
        this.storeRef = storeRef;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof NodeRef)) return false;
        NodeRef other = (NodeRef) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.id==null && other.getId()==null) || 
             (this.id!=null &&
              this.id.equals(other.getId()))) &&
            ((this.storeRef==null && other.getStoreRef()==null) || 
             (this.storeRef!=null &&
              this.storeRef.equals(other.getStoreRef())));
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
        if (getId() != null) {
            _hashCode += getId().hashCode();
        }
        if (getStoreRef() != null) {
            _hashCode += getStoreRef().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(NodeRef.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://node.webservice.repo.alfresco.org", "NodeRef"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("id");
        elemField.setXmlName(new javax.xml.namespace.QName("http://node.webservice.repo.alfresco.org", "id"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("storeRef");
        elemField.setXmlName(new javax.xml.namespace.QName("http://node.webservice.repo.alfresco.org", "storeRef"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://node.webservice.repo.alfresco.org", "StoreRef"));
        elemField.setNillable(true);
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
