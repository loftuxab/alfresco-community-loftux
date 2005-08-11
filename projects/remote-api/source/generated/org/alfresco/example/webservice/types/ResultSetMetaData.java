/**
 * ResultSetMetaData.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package org.alfresco.example.webservice.types;

public class ResultSetMetaData  implements java.io.Serializable {
    private org.alfresco.example.webservice.types.ValueDefinition[] valueDef;
    private org.alfresco.example.webservice.types.ClassDefinition[] classDef;

    public ResultSetMetaData() {
    }

    public ResultSetMetaData(
           org.alfresco.example.webservice.types.ValueDefinition[] valueDef,
           org.alfresco.example.webservice.types.ClassDefinition[] classDef) {
           this.valueDef = valueDef;
           this.classDef = classDef;
    }


    /**
     * Gets the valueDef value for this ResultSetMetaData.
     * 
     * @return valueDef
     */
    public org.alfresco.example.webservice.types.ValueDefinition[] getValueDef() {
        return valueDef;
    }


    /**
     * Sets the valueDef value for this ResultSetMetaData.
     * 
     * @param valueDef
     */
    public void setValueDef(org.alfresco.example.webservice.types.ValueDefinition[] valueDef) {
        this.valueDef = valueDef;
    }

    public org.alfresco.example.webservice.types.ValueDefinition getValueDef(int i) {
        return this.valueDef[i];
    }

    public void setValueDef(int i, org.alfresco.example.webservice.types.ValueDefinition _value) {
        this.valueDef[i] = _value;
    }


    /**
     * Gets the classDef value for this ResultSetMetaData.
     * 
     * @return classDef
     */
    public org.alfresco.example.webservice.types.ClassDefinition[] getClassDef() {
        return classDef;
    }


    /**
     * Sets the classDef value for this ResultSetMetaData.
     * 
     * @param classDef
     */
    public void setClassDef(org.alfresco.example.webservice.types.ClassDefinition[] classDef) {
        this.classDef = classDef;
    }

    public org.alfresco.example.webservice.types.ClassDefinition getClassDef(int i) {
        return this.classDef[i];
    }

    public void setClassDef(int i, org.alfresco.example.webservice.types.ClassDefinition _value) {
        this.classDef[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ResultSetMetaData)) return false;
        ResultSetMetaData other = (ResultSetMetaData) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.valueDef==null && other.getValueDef()==null) || 
             (this.valueDef!=null &&
              java.util.Arrays.equals(this.valueDef, other.getValueDef()))) &&
            ((this.classDef==null && other.getClassDef()==null) || 
             (this.classDef!=null &&
              java.util.Arrays.equals(this.classDef, other.getClassDef())));
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
        if (getValueDef() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getValueDef());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getValueDef(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getClassDef() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getClassDef());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getClassDef(), i);
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
        new org.apache.axis.description.TypeDesc(ResultSetMetaData.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.alfresco.org/ws/model/content/1.0", "ResultSetMetaData"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("valueDef");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.alfresco.org/ws/model/content/1.0", "valueDef"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.alfresco.org/ws/model/content/1.0", "ValueDefinition"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("classDef");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.alfresco.org/ws/model/content/1.0", "classDef"));
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
