/**
 * NodeDefinition.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package org.alfresco.example.webservice.types;

public class NodeDefinition  implements java.io.Serializable {
    private org.alfresco.example.webservice.types.ClassDefinition type;
    private org.alfresco.example.webservice.types.ClassDefinition[] aspect;

    public NodeDefinition() {
    }

    public NodeDefinition(
           org.alfresco.example.webservice.types.ClassDefinition type,
           org.alfresco.example.webservice.types.ClassDefinition[] aspect) {
           this.type = type;
           this.aspect = aspect;
    }


    /**
     * Gets the type value for this NodeDefinition.
     * 
     * @return type
     */
    public org.alfresco.example.webservice.types.ClassDefinition getType() {
        return type;
    }


    /**
     * Sets the type value for this NodeDefinition.
     * 
     * @param type
     */
    public void setType(org.alfresco.example.webservice.types.ClassDefinition type) {
        this.type = type;
    }


    /**
     * Gets the aspect value for this NodeDefinition.
     * 
     * @return aspect
     */
    public org.alfresco.example.webservice.types.ClassDefinition[] getAspect() {
        return aspect;
    }


    /**
     * Sets the aspect value for this NodeDefinition.
     * 
     * @param aspect
     */
    public void setAspect(org.alfresco.example.webservice.types.ClassDefinition[] aspect) {
        this.aspect = aspect;
    }

    public org.alfresco.example.webservice.types.ClassDefinition getAspect(int i) {
        return this.aspect[i];
    }

    public void setAspect(int i, org.alfresco.example.webservice.types.ClassDefinition _value) {
        this.aspect[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof NodeDefinition)) return false;
        NodeDefinition other = (NodeDefinition) obj;
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
              this.type.equals(other.getType()))) &&
            ((this.aspect==null && other.getAspect()==null) || 
             (this.aspect!=null &&
              java.util.Arrays.equals(this.aspect, other.getAspect())));
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
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(NodeDefinition.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.alfresco.org/ws/model/content/1.0", "NodeDefinition"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("type");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.alfresco.org/ws/model/content/1.0", "type"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.alfresco.org/ws/model/content/1.0", "ClassDefinition"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("aspect");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.alfresco.org/ws/model/content/1.0", "aspect"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.alfresco.org/ws/model/content/1.0", "ClassDefinition"));
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
