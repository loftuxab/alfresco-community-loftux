/**
 * Node.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package org.alfresco.example.webservice.types;

public class Node  implements java.io.Serializable {
    private org.alfresco.example.webservice.types.Reference reference;
    private java.lang.String type;
    private java.lang.String[] aspect;
    private org.alfresco.example.webservice.types.NamedValue[] properties;

    public Node() {
    }

    public Node(
           org.alfresco.example.webservice.types.Reference reference,
           java.lang.String type,
           java.lang.String[] aspect,
           org.alfresco.example.webservice.types.NamedValue[] properties) {
           this.reference = reference;
           this.type = type;
           this.aspect = aspect;
           this.properties = properties;
    }


    /**
     * Gets the reference value for this Node.
     * 
     * @return reference
     */
    public org.alfresco.example.webservice.types.Reference getReference() {
        return reference;
    }


    /**
     * Sets the reference value for this Node.
     * 
     * @param reference
     */
    public void setReference(org.alfresco.example.webservice.types.Reference reference) {
        this.reference = reference;
    }


    /**
     * Gets the type value for this Node.
     * 
     * @return type
     */
    public java.lang.String getType() {
        return type;
    }


    /**
     * Sets the type value for this Node.
     * 
     * @param type
     */
    public void setType(java.lang.String type) {
        this.type = type;
    }


    /**
     * Gets the aspect value for this Node.
     * 
     * @return aspect
     */
    public java.lang.String[] getAspect() {
        return aspect;
    }


    /**
     * Sets the aspect value for this Node.
     * 
     * @param aspect
     */
    public void setAspect(java.lang.String[] aspect) {
        this.aspect = aspect;
    }

    public java.lang.String getAspect(int i) {
        return this.aspect[i];
    }

    public void setAspect(int i, java.lang.String _value) {
        this.aspect[i] = _value;
    }


    /**
     * Gets the properties value for this Node.
     * 
     * @return properties
     */
    public org.alfresco.example.webservice.types.NamedValue[] getProperties() {
        return properties;
    }


    /**
     * Sets the properties value for this Node.
     * 
     * @param properties
     */
    public void setProperties(org.alfresco.example.webservice.types.NamedValue[] properties) {
        this.properties = properties;
    }

    public org.alfresco.example.webservice.types.NamedValue getProperties(int i) {
        return this.properties[i];
    }

    public void setProperties(int i, org.alfresco.example.webservice.types.NamedValue _value) {
        this.properties[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Node)) return false;
        Node other = (Node) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.reference==null && other.getReference()==null) || 
             (this.reference!=null &&
              this.reference.equals(other.getReference()))) &&
            ((this.type==null && other.getType()==null) || 
             (this.type!=null &&
              this.type.equals(other.getType()))) &&
            ((this.aspect==null && other.getAspect()==null) || 
             (this.aspect!=null &&
              java.util.Arrays.equals(this.aspect, other.getAspect()))) &&
            ((this.properties==null && other.getProperties()==null) || 
             (this.properties!=null &&
              java.util.Arrays.equals(this.properties, other.getProperties())));
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
        if (getReference() != null) {
            _hashCode += getReference().hashCode();
        }
        if (getType() != null) {
            _hashCode += getType().hashCode();
        }
        if (getAspect() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getAspect());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getAspect(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getProperties() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getProperties());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getProperties(), i);
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
        new org.apache.axis.description.TypeDesc(Node.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.alfresco.org/ws/model/content/1.0", "Node"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("reference");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.alfresco.org/ws/model/content/1.0", "reference"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.alfresco.org/ws/model/content/1.0", "Reference"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("type");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.alfresco.org/ws/model/content/1.0", "type"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.alfresco.org/ws/model/content/1.0", "Name"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("aspect");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.alfresco.org/ws/model/content/1.0", "aspect"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.alfresco.org/ws/model/content/1.0", "Name"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("properties");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.alfresco.org/ws/model/content/1.0", "properties"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.alfresco.org/ws/model/content/1.0", "NamedValue"));
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
