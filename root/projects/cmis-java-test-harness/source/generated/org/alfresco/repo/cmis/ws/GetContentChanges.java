/**
 * GetContentChanges.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.alfresco.repo.cmis.ws;

public class GetContentChanges  implements java.io.Serializable {
    private java.lang.String repositoryId;

    private java.lang.String changeToken;

    private java.math.BigInteger maxItems;

    private java.lang.Boolean includeACL;

    private java.lang.Boolean includeProperties;

    private java.lang.String filter;

    public GetContentChanges() {
    }

    public GetContentChanges(
           java.lang.String repositoryId,
           java.lang.String changeToken,
           java.math.BigInteger maxItems,
           java.lang.Boolean includeACL,
           java.lang.Boolean includeProperties,
           java.lang.String filter) {
           this.repositoryId = repositoryId;
           this.changeToken = changeToken;
           this.maxItems = maxItems;
           this.includeACL = includeACL;
           this.includeProperties = includeProperties;
           this.filter = filter;
    }


    /**
     * Gets the repositoryId value for this GetContentChanges.
     * 
     * @return repositoryId
     */
    public java.lang.String getRepositoryId() {
        return repositoryId;
    }


    /**
     * Sets the repositoryId value for this GetContentChanges.
     * 
     * @param repositoryId
     */
    public void setRepositoryId(java.lang.String repositoryId) {
        this.repositoryId = repositoryId;
    }


    /**
     * Gets the changeToken value for this GetContentChanges.
     * 
     * @return changeToken
     */
    public java.lang.String getChangeToken() {
        return changeToken;
    }


    /**
     * Sets the changeToken value for this GetContentChanges.
     * 
     * @param changeToken
     */
    public void setChangeToken(java.lang.String changeToken) {
        this.changeToken = changeToken;
    }


    /**
     * Gets the maxItems value for this GetContentChanges.
     * 
     * @return maxItems
     */
    public java.math.BigInteger getMaxItems() {
        return maxItems;
    }


    /**
     * Sets the maxItems value for this GetContentChanges.
     * 
     * @param maxItems
     */
    public void setMaxItems(java.math.BigInteger maxItems) {
        this.maxItems = maxItems;
    }


    /**
     * Gets the includeACL value for this GetContentChanges.
     * 
     * @return includeACL
     */
    public java.lang.Boolean getIncludeACL() {
        return includeACL;
    }


    /**
     * Sets the includeACL value for this GetContentChanges.
     * 
     * @param includeACL
     */
    public void setIncludeACL(java.lang.Boolean includeACL) {
        this.includeACL = includeACL;
    }


    /**
     * Gets the includeProperties value for this GetContentChanges.
     * 
     * @return includeProperties
     */
    public java.lang.Boolean getIncludeProperties() {
        return includeProperties;
    }


    /**
     * Sets the includeProperties value for this GetContentChanges.
     * 
     * @param includeProperties
     */
    public void setIncludeProperties(java.lang.Boolean includeProperties) {
        this.includeProperties = includeProperties;
    }


    /**
     * Gets the filter value for this GetContentChanges.
     * 
     * @return filter
     */
    public java.lang.String getFilter() {
        return filter;
    }


    /**
     * Sets the filter value for this GetContentChanges.
     * 
     * @param filter
     */
    public void setFilter(java.lang.String filter) {
        this.filter = filter;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GetContentChanges)) return false;
        GetContentChanges other = (GetContentChanges) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.repositoryId==null && other.getRepositoryId()==null) || 
             (this.repositoryId!=null &&
              this.repositoryId.equals(other.getRepositoryId()))) &&
            ((this.changeToken==null && other.getChangeToken()==null) || 
             (this.changeToken!=null &&
              this.changeToken.equals(other.getChangeToken()))) &&
            ((this.maxItems==null && other.getMaxItems()==null) || 
             (this.maxItems!=null &&
              this.maxItems.equals(other.getMaxItems()))) &&
            ((this.includeACL==null && other.getIncludeACL()==null) || 
             (this.includeACL!=null &&
              this.includeACL.equals(other.getIncludeACL()))) &&
            ((this.includeProperties==null && other.getIncludeProperties()==null) || 
             (this.includeProperties!=null &&
              this.includeProperties.equals(other.getIncludeProperties()))) &&
            ((this.filter==null && other.getFilter()==null) || 
             (this.filter!=null &&
              this.filter.equals(other.getFilter())));
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
        if (getRepositoryId() != null) {
            _hashCode += getRepositoryId().hashCode();
        }
        if (getChangeToken() != null) {
            _hashCode += getChangeToken().hashCode();
        }
        if (getMaxItems() != null) {
            _hashCode += getMaxItems().hashCode();
        }
        if (getIncludeACL() != null) {
            _hashCode += getIncludeACL().hashCode();
        }
        if (getIncludeProperties() != null) {
            _hashCode += getIncludeProperties().hashCode();
        }
        if (getFilter() != null) {
            _hashCode += getFilter().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GetContentChanges.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200901", ">getContentChanges"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("repositoryId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200901", "repositoryId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("changeToken");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200901", "changeToken"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("maxItems");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200901", "maxItems"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "integer"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("includeACL");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200901", "includeACL"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("includeProperties");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200901", "includeProperties"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("filter");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200901", "filter"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
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
