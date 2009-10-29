/**
 * GetRelationships.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.alfresco.repo.cmis.ws;

public class GetRelationships  implements java.io.Serializable {
    private java.lang.String repositoryId;

    private java.lang.String objectId;

    private org.alfresco.repo.cmis.ws.EnumRelationshipDirection direction;

    private java.lang.String typeId;

    private java.lang.Boolean includeSubRelationshipTypes;

    private java.lang.String filter;

    private java.lang.Boolean includeAllowableActions;

    private org.alfresco.repo.cmis.ws.EnumIncludeRelationships includeRelationships;

    private java.math.BigInteger maxItems;

    private java.math.BigInteger skipCount;

    public GetRelationships() {
    }

    public GetRelationships(
           java.lang.String repositoryId,
           java.lang.String objectId,
           org.alfresco.repo.cmis.ws.EnumRelationshipDirection direction,
           java.lang.String typeId,
           java.lang.Boolean includeSubRelationshipTypes,
           java.lang.String filter,
           java.lang.Boolean includeAllowableActions,
           org.alfresco.repo.cmis.ws.EnumIncludeRelationships includeRelationships,
           java.math.BigInteger maxItems,
           java.math.BigInteger skipCount) {
           this.repositoryId = repositoryId;
           this.objectId = objectId;
           this.direction = direction;
           this.typeId = typeId;
           this.includeSubRelationshipTypes = includeSubRelationshipTypes;
           this.filter = filter;
           this.includeAllowableActions = includeAllowableActions;
           this.includeRelationships = includeRelationships;
           this.maxItems = maxItems;
           this.skipCount = skipCount;
    }


    /**
     * Gets the repositoryId value for this GetRelationships.
     * 
     * @return repositoryId
     */
    public java.lang.String getRepositoryId() {
        return repositoryId;
    }


    /**
     * Sets the repositoryId value for this GetRelationships.
     * 
     * @param repositoryId
     */
    public void setRepositoryId(java.lang.String repositoryId) {
        this.repositoryId = repositoryId;
    }


    /**
     * Gets the objectId value for this GetRelationships.
     * 
     * @return objectId
     */
    public java.lang.String getObjectId() {
        return objectId;
    }


    /**
     * Sets the objectId value for this GetRelationships.
     * 
     * @param objectId
     */
    public void setObjectId(java.lang.String objectId) {
        this.objectId = objectId;
    }


    /**
     * Gets the direction value for this GetRelationships.
     * 
     * @return direction
     */
    public org.alfresco.repo.cmis.ws.EnumRelationshipDirection getDirection() {
        return direction;
    }


    /**
     * Sets the direction value for this GetRelationships.
     * 
     * @param direction
     */
    public void setDirection(org.alfresco.repo.cmis.ws.EnumRelationshipDirection direction) {
        this.direction = direction;
    }


    /**
     * Gets the typeId value for this GetRelationships.
     * 
     * @return typeId
     */
    public java.lang.String getTypeId() {
        return typeId;
    }


    /**
     * Sets the typeId value for this GetRelationships.
     * 
     * @param typeId
     */
    public void setTypeId(java.lang.String typeId) {
        this.typeId = typeId;
    }


    /**
     * Gets the includeSubRelationshipTypes value for this GetRelationships.
     * 
     * @return includeSubRelationshipTypes
     */
    public java.lang.Boolean getIncludeSubRelationshipTypes() {
        return includeSubRelationshipTypes;
    }


    /**
     * Sets the includeSubRelationshipTypes value for this GetRelationships.
     * 
     * @param includeSubRelationshipTypes
     */
    public void setIncludeSubRelationshipTypes(java.lang.Boolean includeSubRelationshipTypes) {
        this.includeSubRelationshipTypes = includeSubRelationshipTypes;
    }


    /**
     * Gets the filter value for this GetRelationships.
     * 
     * @return filter
     */
    public java.lang.String getFilter() {
        return filter;
    }


    /**
     * Sets the filter value for this GetRelationships.
     * 
     * @param filter
     */
    public void setFilter(java.lang.String filter) {
        this.filter = filter;
    }


    /**
     * Gets the includeAllowableActions value for this GetRelationships.
     * 
     * @return includeAllowableActions
     */
    public java.lang.Boolean getIncludeAllowableActions() {
        return includeAllowableActions;
    }


    /**
     * Sets the includeAllowableActions value for this GetRelationships.
     * 
     * @param includeAllowableActions
     */
    public void setIncludeAllowableActions(java.lang.Boolean includeAllowableActions) {
        this.includeAllowableActions = includeAllowableActions;
    }


    /**
     * Gets the includeRelationships value for this GetRelationships.
     * 
     * @return includeRelationships
     */
    public org.alfresco.repo.cmis.ws.EnumIncludeRelationships getIncludeRelationships() {
        return includeRelationships;
    }


    /**
     * Sets the includeRelationships value for this GetRelationships.
     * 
     * @param includeRelationships
     */
    public void setIncludeRelationships(org.alfresco.repo.cmis.ws.EnumIncludeRelationships includeRelationships) {
        this.includeRelationships = includeRelationships;
    }


    /**
     * Gets the maxItems value for this GetRelationships.
     * 
     * @return maxItems
     */
    public java.math.BigInteger getMaxItems() {
        return maxItems;
    }


    /**
     * Sets the maxItems value for this GetRelationships.
     * 
     * @param maxItems
     */
    public void setMaxItems(java.math.BigInteger maxItems) {
        this.maxItems = maxItems;
    }


    /**
     * Gets the skipCount value for this GetRelationships.
     * 
     * @return skipCount
     */
    public java.math.BigInteger getSkipCount() {
        return skipCount;
    }


    /**
     * Sets the skipCount value for this GetRelationships.
     * 
     * @param skipCount
     */
    public void setSkipCount(java.math.BigInteger skipCount) {
        this.skipCount = skipCount;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GetRelationships)) return false;
        GetRelationships other = (GetRelationships) obj;
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
            ((this.objectId==null && other.getObjectId()==null) || 
             (this.objectId!=null &&
              this.objectId.equals(other.getObjectId()))) &&
            ((this.direction==null && other.getDirection()==null) || 
             (this.direction!=null &&
              this.direction.equals(other.getDirection()))) &&
            ((this.typeId==null && other.getTypeId()==null) || 
             (this.typeId!=null &&
              this.typeId.equals(other.getTypeId()))) &&
            ((this.includeSubRelationshipTypes==null && other.getIncludeSubRelationshipTypes()==null) || 
             (this.includeSubRelationshipTypes!=null &&
              this.includeSubRelationshipTypes.equals(other.getIncludeSubRelationshipTypes()))) &&
            ((this.filter==null && other.getFilter()==null) || 
             (this.filter!=null &&
              this.filter.equals(other.getFilter()))) &&
            ((this.includeAllowableActions==null && other.getIncludeAllowableActions()==null) || 
             (this.includeAllowableActions!=null &&
              this.includeAllowableActions.equals(other.getIncludeAllowableActions()))) &&
            ((this.includeRelationships==null && other.getIncludeRelationships()==null) || 
             (this.includeRelationships!=null &&
              this.includeRelationships.equals(other.getIncludeRelationships()))) &&
            ((this.maxItems==null && other.getMaxItems()==null) || 
             (this.maxItems!=null &&
              this.maxItems.equals(other.getMaxItems()))) &&
            ((this.skipCount==null && other.getSkipCount()==null) || 
             (this.skipCount!=null &&
              this.skipCount.equals(other.getSkipCount())));
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
        if (getObjectId() != null) {
            _hashCode += getObjectId().hashCode();
        }
        if (getDirection() != null) {
            _hashCode += getDirection().hashCode();
        }
        if (getTypeId() != null) {
            _hashCode += getTypeId().hashCode();
        }
        if (getIncludeSubRelationshipTypes() != null) {
            _hashCode += getIncludeSubRelationshipTypes().hashCode();
        }
        if (getFilter() != null) {
            _hashCode += getFilter().hashCode();
        }
        if (getIncludeAllowableActions() != null) {
            _hashCode += getIncludeAllowableActions().hashCode();
        }
        if (getIncludeRelationships() != null) {
            _hashCode += getIncludeRelationships().hashCode();
        }
        if (getMaxItems() != null) {
            _hashCode += getMaxItems().hashCode();
        }
        if (getSkipCount() != null) {
            _hashCode += getSkipCount().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GetRelationships.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200901", ">getRelationships"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("repositoryId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200901", "repositoryId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("objectId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200901", "objectId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("direction");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200901", "direction"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200901", "enumRelationshipDirection"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("typeId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200901", "typeId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("includeSubRelationshipTypes");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200901", "includeSubRelationshipTypes"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("filter");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200901", "filter"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("includeAllowableActions");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200901", "includeAllowableActions"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("includeRelationships");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200901", "includeRelationships"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200901", "enumIncludeRelationships"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("maxItems");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200901", "maxItems"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "integer"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("skipCount");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200901", "skipCount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "integer"));
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
