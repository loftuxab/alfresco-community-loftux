/**
 * Predicate.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package org.alfresco.example.webservice.types;

public class Predicate  implements java.io.Serializable {
    private org.alfresco.example.webservice.types.Reference[] node;
    private org.alfresco.example.webservice.types.Store store;
    private org.alfresco.example.webservice.types.Query query;

    public Predicate() {
    }

    public Predicate(
           org.alfresco.example.webservice.types.Reference[] node,
           org.alfresco.example.webservice.types.Store store,
           org.alfresco.example.webservice.types.Query query) {
           this.node = node;
           this.store = store;
           this.query = query;
    }


    /**
     * Gets the node value for this Predicate.
     * 
     * @return node
     */
    public org.alfresco.example.webservice.types.Reference[] getNode() {
        return node;
    }


    /**
     * Sets the node value for this Predicate.
     * 
     * @param node
     */
    public void setNode(org.alfresco.example.webservice.types.Reference[] node) {
        this.node = node;
    }

    public org.alfresco.example.webservice.types.Reference getNode(int i) {
        return this.node[i];
    }

    public void setNode(int i, org.alfresco.example.webservice.types.Reference _value) {
        this.node[i] = _value;
    }


    /**
     * Gets the store value for this Predicate.
     * 
     * @return store
     */
    public org.alfresco.example.webservice.types.Store getStore() {
        return store;
    }


    /**
     * Sets the store value for this Predicate.
     * 
     * @param store
     */
    public void setStore(org.alfresco.example.webservice.types.Store store) {
        this.store = store;
    }


    /**
     * Gets the query value for this Predicate.
     * 
     * @return query
     */
    public org.alfresco.example.webservice.types.Query getQuery() {
        return query;
    }


    /**
     * Sets the query value for this Predicate.
     * 
     * @param query
     */
    public void setQuery(org.alfresco.example.webservice.types.Query query) {
        this.query = query;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Predicate)) return false;
        Predicate other = (Predicate) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.node==null && other.getNode()==null) || 
             (this.node!=null &&
              java.util.Arrays.equals(this.node, other.getNode()))) &&
            ((this.store==null && other.getStore()==null) || 
             (this.store!=null &&
              this.store.equals(other.getStore()))) &&
            ((this.query==null && other.getQuery()==null) || 
             (this.query!=null &&
              this.query.equals(other.getQuery())));
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
        if (getNode() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getNode());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getNode(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getStore() != null) {
            _hashCode += getStore().hashCode();
        }
        if (getQuery() != null) {
            _hashCode += getQuery().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Predicate.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.alfresco.org/ws/model/content/1.0", "Predicate"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("node");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.alfresco.org/ws/model/content/1.0", "node"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.alfresco.org/ws/model/content/1.0", "Reference"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("store");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.alfresco.org/ws/model/content/1.0", "store"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.alfresco.org/ws/model/content/1.0", "Store"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("query");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.alfresco.org/ws/model/content/1.0", "query"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.alfresco.org/ws/model/content/1.0", "Query"));
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
