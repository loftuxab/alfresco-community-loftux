/**
 * CmisPropertyXmlDefinitionType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.alfresco.repo.cmis.ws;

public class CmisPropertyXmlDefinitionType  extends org.alfresco.repo.cmis.ws.CmisPropertyDefinitionType  implements java.io.Serializable {
    private org.alfresco.repo.cmis.ws.CmisPropertyXml defaultValue;

    private org.apache.axis.types.URI schemaURI;

    private org.alfresco.repo.cmis.ws.CmisChoiceXml[] choice;

    public CmisPropertyXmlDefinitionType() {
    }

    public CmisPropertyXmlDefinitionType(
           java.lang.String id,
           java.lang.String localName,
           org.apache.axis.types.URI localNamespace,
           java.lang.String displayName,
           java.lang.String queryName,
           java.lang.String description,
           org.alfresco.repo.cmis.ws.EnumPropertyType propertyType,
           org.alfresco.repo.cmis.ws.EnumCardinality cardinality,
           org.alfresco.repo.cmis.ws.EnumUpdatability updatability,
           java.lang.Boolean inherited,
           boolean required,
           boolean queryable,
           boolean orderable,
           java.lang.Boolean openChoice,
           org.apache.axis.message.MessageElement [] _any,
           org.alfresco.repo.cmis.ws.CmisPropertyXml defaultValue,
           org.apache.axis.types.URI schemaURI,
           org.alfresco.repo.cmis.ws.CmisChoiceXml[] choice) {
        super(
            id,
            localName,
            localNamespace,
            displayName,
            queryName,
            description,
            propertyType,
            cardinality,
            updatability,
            inherited,
            required,
            queryable,
            orderable,
            openChoice,
            _any);
        this.defaultValue = defaultValue;
        this.schemaURI = schemaURI;
        this.choice = choice;
    }


    /**
     * Gets the defaultValue value for this CmisPropertyXmlDefinitionType.
     * 
     * @return defaultValue
     */
    public org.alfresco.repo.cmis.ws.CmisPropertyXml getDefaultValue() {
        return defaultValue;
    }


    /**
     * Sets the defaultValue value for this CmisPropertyXmlDefinitionType.
     * 
     * @param defaultValue
     */
    public void setDefaultValue(org.alfresco.repo.cmis.ws.CmisPropertyXml defaultValue) {
        this.defaultValue = defaultValue;
    }


    /**
     * Gets the schemaURI value for this CmisPropertyXmlDefinitionType.
     * 
     * @return schemaURI
     */
    public org.apache.axis.types.URI getSchemaURI() {
        return schemaURI;
    }


    /**
     * Sets the schemaURI value for this CmisPropertyXmlDefinitionType.
     * 
     * @param schemaURI
     */
    public void setSchemaURI(org.apache.axis.types.URI schemaURI) {
        this.schemaURI = schemaURI;
    }


    /**
     * Gets the choice value for this CmisPropertyXmlDefinitionType.
     * 
     * @return choice
     */
    public org.alfresco.repo.cmis.ws.CmisChoiceXml[] getChoice() {
        return choice;
    }


    /**
     * Sets the choice value for this CmisPropertyXmlDefinitionType.
     * 
     * @param choice
     */
    public void setChoice(org.alfresco.repo.cmis.ws.CmisChoiceXml[] choice) {
        this.choice = choice;
    }

    public org.alfresco.repo.cmis.ws.CmisChoiceXml getChoice(int i) {
        return this.choice[i];
    }

    public void setChoice(int i, org.alfresco.repo.cmis.ws.CmisChoiceXml _value) {
        this.choice[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CmisPropertyXmlDefinitionType)) return false;
        CmisPropertyXmlDefinitionType other = (CmisPropertyXmlDefinitionType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.defaultValue==null && other.getDefaultValue()==null) || 
             (this.defaultValue!=null &&
              this.defaultValue.equals(other.getDefaultValue()))) &&
            ((this.schemaURI==null && other.getSchemaURI()==null) || 
             (this.schemaURI!=null &&
              this.schemaURI.equals(other.getSchemaURI()))) &&
            ((this.choice==null && other.getChoice()==null) || 
             (this.choice!=null &&
              java.util.Arrays.equals(this.choice, other.getChoice())));
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
        if (getDefaultValue() != null) {
            _hashCode += getDefaultValue().hashCode();
        }
        if (getSchemaURI() != null) {
            _hashCode += getSchemaURI().hashCode();
        }
        if (getChoice() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getChoice());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getChoice(), i);
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
        new org.apache.axis.description.TypeDesc(CmisPropertyXmlDefinitionType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200901", "cmisPropertyXmlDefinitionType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("defaultValue");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200901", "defaultValue"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200901", "cmisPropertyXml"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("schemaURI");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200901", "schemaURI"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "anyURI"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("choice");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200901", "choice"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200901", "cmisChoiceXml"));
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
