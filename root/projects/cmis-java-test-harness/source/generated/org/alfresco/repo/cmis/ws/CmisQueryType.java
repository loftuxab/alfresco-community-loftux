/**
 * CmisQueryType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.alfresco.repo.cmis.ws;

public class CmisQueryType  implements java.io.Serializable, org.apache.axis.encoding.AnyContentType {
    private java.lang.String repositoryId;

    private java.lang.String statement;

    private java.lang.Boolean searchAllVersions;

    private java.math.BigInteger maxItems;

    private java.math.BigInteger skipCount;

    private java.lang.Boolean includeAllowableActions;

    private org.alfresco.repo.cmis.ws.EnumIncludeRelationships includeRelationships;

    private java.lang.Boolean includeRenditions;

    private org.apache.axis.message.MessageElement [] _any;

    public CmisQueryType() {
    }

    public CmisQueryType(
           java.lang.String repositoryId,
           java.lang.String statement,
           java.lang.Boolean searchAllVersions,
           java.math.BigInteger maxItems,
           java.math.BigInteger skipCount,
           java.lang.Boolean includeAllowableActions,
           org.alfresco.repo.cmis.ws.EnumIncludeRelationships includeRelationships,
           java.lang.Boolean includeRenditions,
           org.apache.axis.message.MessageElement [] _any) {
           this.repositoryId = repositoryId;
           this.statement = statement;
           this.searchAllVersions = searchAllVersions;
           this.maxItems = maxItems;
           this.skipCount = skipCount;
           this.includeAllowableActions = includeAllowableActions;
           this.includeRelationships = includeRelationships;
           this.includeRenditions = includeRenditions;
           this._any = _any;
    }


    /**
     * Gets the repositoryId value for this CmisQueryType.
     * 
     * @return repositoryId
     */
    public java.lang.String getRepositoryId() {
        return repositoryId;
    }


    /**
     * Sets the repositoryId value for this CmisQueryType.
     * 
     * @param repositoryId
     */
    public void setRepositoryId(java.lang.String repositoryId) {
        this.repositoryId = repositoryId;
    }


    /**
     * Gets the statement value for this CmisQueryType.
     * 
     * @return statement
     */
    public java.lang.String getStatement() {
        return statement;
    }


    /**
     * Sets the statement value for this CmisQueryType.
     * 
     * @param statement
     */
    public void setStatement(java.lang.String statement) {
        this.statement = statement;
    }


    /**
     * Gets the searchAllVersions value for this CmisQueryType.
     * 
     * @return searchAllVersions
     */
    public java.lang.Boolean getSearchAllVersions() {
        return searchAllVersions;
    }


    /**
     * Sets the searchAllVersions value for this CmisQueryType.
     * 
     * @param searchAllVersions
     */
    public void setSearchAllVersions(java.lang.Boolean searchAllVersions) {
        this.searchAllVersions = searchAllVersions;
    }


    /**
     * Gets the maxItems value for this CmisQueryType.
     * 
     * @return maxItems
     */
    public java.math.BigInteger getMaxItems() {
        return maxItems;
    }


    /**
     * Sets the maxItems value for this CmisQueryType.
     * 
     * @param maxItems
     */
    public void setMaxItems(java.math.BigInteger maxItems) {
        this.maxItems = maxItems;
    }


    /**
     * Gets the skipCount value for this CmisQueryType.
     * 
     * @return skipCount
     */
    public java.math.BigInteger getSkipCount() {
        return skipCount;
    }


    /**
     * Sets the skipCount value for this CmisQueryType.
     * 
     * @param skipCount
     */
    public void setSkipCount(java.math.BigInteger skipCount) {
        this.skipCount = skipCount;
    }


    /**
     * Gets the includeAllowableActions value for this CmisQueryType.
     * 
     * @return includeAllowableActions
     */
    public java.lang.Boolean getIncludeAllowableActions() {
        return includeAllowableActions;
    }


    /**
     * Sets the includeAllowableActions value for this CmisQueryType.
     * 
     * @param includeAllowableActions
     */
    public void setIncludeAllowableActions(java.lang.Boolean includeAllowableActions) {
        this.includeAllowableActions = includeAllowableActions;
    }


    /**
     * Gets the includeRelationships value for this CmisQueryType.
     * 
     * @return includeRelationships
     */
    public org.alfresco.repo.cmis.ws.EnumIncludeRelationships getIncludeRelationships() {
        return includeRelationships;
    }


    /**
     * Sets the includeRelationships value for this CmisQueryType.
     * 
     * @param includeRelationships
     */
    public void setIncludeRelationships(org.alfresco.repo.cmis.ws.EnumIncludeRelationships includeRelationships) {
        this.includeRelationships = includeRelationships;
    }


    /**
     * Gets the includeRenditions value for this CmisQueryType.
     * 
     * @return includeRenditions
     */
    public java.lang.Boolean getIncludeRenditions() {
        return includeRenditions;
    }


    /**
     * Sets the includeRenditions value for this CmisQueryType.
     * 
     * @param includeRenditions
     */
    public void setIncludeRenditions(java.lang.Boolean includeRenditions) {
        this.includeRenditions = includeRenditions;
    }


    /**
     * Gets the _any value for this CmisQueryType.
     * 
     * @return _any
     */
    public org.apache.axis.message.MessageElement [] get_any() {
        return _any;
    }


    /**
     * Sets the _any value for this CmisQueryType.
     * 
     * @param _any
     */
    public void set_any(org.apache.axis.message.MessageElement [] _any) {
        this._any = _any;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CmisQueryType)) return false;
        CmisQueryType other = (CmisQueryType) obj;
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
            ((this.statement==null && other.getStatement()==null) || 
             (this.statement!=null &&
              this.statement.equals(other.getStatement()))) &&
            ((this.searchAllVersions==null && other.getSearchAllVersions()==null) || 
             (this.searchAllVersions!=null &&
              this.searchAllVersions.equals(other.getSearchAllVersions()))) &&
            ((this.maxItems==null && other.getMaxItems()==null) || 
             (this.maxItems!=null &&
              this.maxItems.equals(other.getMaxItems()))) &&
            ((this.skipCount==null && other.getSkipCount()==null) || 
             (this.skipCount!=null &&
              this.skipCount.equals(other.getSkipCount()))) &&
            ((this.includeAllowableActions==null && other.getIncludeAllowableActions()==null) || 
             (this.includeAllowableActions!=null &&
              this.includeAllowableActions.equals(other.getIncludeAllowableActions()))) &&
            ((this.includeRelationships==null && other.getIncludeRelationships()==null) || 
             (this.includeRelationships!=null &&
              this.includeRelationships.equals(other.getIncludeRelationships()))) &&
            ((this.includeRenditions==null && other.getIncludeRenditions()==null) || 
             (this.includeRenditions!=null &&
              this.includeRenditions.equals(other.getIncludeRenditions()))) &&
            ((this._any==null && other.get_any()==null) || 
             (this._any!=null &&
              java.util.Arrays.equals(this._any, other.get_any())));
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
        if (getStatement() != null) {
            _hashCode += getStatement().hashCode();
        }
        if (getSearchAllVersions() != null) {
            _hashCode += getSearchAllVersions().hashCode();
        }
        if (getMaxItems() != null) {
            _hashCode += getMaxItems().hashCode();
        }
        if (getSkipCount() != null) {
            _hashCode += getSkipCount().hashCode();
        }
        if (getIncludeAllowableActions() != null) {
            _hashCode += getIncludeAllowableActions().hashCode();
        }
        if (getIncludeRelationships() != null) {
            _hashCode += getIncludeRelationships().hashCode();
        }
        if (getIncludeRenditions() != null) {
            _hashCode += getIncludeRenditions().hashCode();
        }
        if (get_any() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(get_any());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(get_any(), i);
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
        new org.apache.axis.description.TypeDesc(CmisQueryType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200901", "cmisQueryType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("repositoryId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200901", "repositoryId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("statement");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200901", "statement"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("searchAllVersions");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200901", "searchAllVersions"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("maxItems");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200901", "maxItems"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "integer"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("skipCount");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200901", "skipCount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "integer"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("includeAllowableActions");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200901", "includeAllowableActions"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("includeRelationships");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200901", "includeRelationships"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200901", "enumIncludeRelationships"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("includeRenditions");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200901", "includeRenditions"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
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
