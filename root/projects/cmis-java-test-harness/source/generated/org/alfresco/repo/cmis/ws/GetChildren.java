/**
 * GetChildren.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.alfresco.repo.cmis.ws;

public class GetChildren  implements java.io.Serializable {
    private java.lang.String repositoryId;

    private java.lang.String folderId;

    private java.lang.String filter;

    private java.lang.Boolean includeAllowableActions;

    private org.alfresco.repo.cmis.ws.EnumIncludeRelationships includeRelationships;

    private java.lang.Boolean includeRenditions;

    private java.lang.Boolean includeACL;

    private java.math.BigInteger maxItems;

    private java.math.BigInteger skipCount;

    private java.lang.String orderBy;

    public GetChildren() {
    }

    public GetChildren(
           java.lang.String repositoryId,
           java.lang.String folderId,
           java.lang.String filter,
           java.lang.Boolean includeAllowableActions,
           org.alfresco.repo.cmis.ws.EnumIncludeRelationships includeRelationships,
           java.lang.Boolean includeRenditions,
           java.lang.Boolean includeACL,
           java.math.BigInteger maxItems,
           java.math.BigInteger skipCount,
           java.lang.String orderBy) {
           this.repositoryId = repositoryId;
           this.folderId = folderId;
           this.filter = filter;
           this.includeAllowableActions = includeAllowableActions;
           this.includeRelationships = includeRelationships;
           this.includeRenditions = includeRenditions;
           this.includeACL = includeACL;
           this.maxItems = maxItems;
           this.skipCount = skipCount;
           this.orderBy = orderBy;
    }


    /**
     * Gets the repositoryId value for this GetChildren.
     * 
     * @return repositoryId
     */
    public java.lang.String getRepositoryId() {
        return repositoryId;
    }


    /**
     * Sets the repositoryId value for this GetChildren.
     * 
     * @param repositoryId
     */
    public void setRepositoryId(java.lang.String repositoryId) {
        this.repositoryId = repositoryId;
    }


    /**
     * Gets the folderId value for this GetChildren.
     * 
     * @return folderId
     */
    public java.lang.String getFolderId() {
        return folderId;
    }


    /**
     * Sets the folderId value for this GetChildren.
     * 
     * @param folderId
     */
    public void setFolderId(java.lang.String folderId) {
        this.folderId = folderId;
    }


    /**
     * Gets the filter value for this GetChildren.
     * 
     * @return filter
     */
    public java.lang.String getFilter() {
        return filter;
    }


    /**
     * Sets the filter value for this GetChildren.
     * 
     * @param filter
     */
    public void setFilter(java.lang.String filter) {
        this.filter = filter;
    }


    /**
     * Gets the includeAllowableActions value for this GetChildren.
     * 
     * @return includeAllowableActions
     */
    public java.lang.Boolean getIncludeAllowableActions() {
        return includeAllowableActions;
    }


    /**
     * Sets the includeAllowableActions value for this GetChildren.
     * 
     * @param includeAllowableActions
     */
    public void setIncludeAllowableActions(java.lang.Boolean includeAllowableActions) {
        this.includeAllowableActions = includeAllowableActions;
    }


    /**
     * Gets the includeRelationships value for this GetChildren.
     * 
     * @return includeRelationships
     */
    public org.alfresco.repo.cmis.ws.EnumIncludeRelationships getIncludeRelationships() {
        return includeRelationships;
    }


    /**
     * Sets the includeRelationships value for this GetChildren.
     * 
     * @param includeRelationships
     */
    public void setIncludeRelationships(org.alfresco.repo.cmis.ws.EnumIncludeRelationships includeRelationships) {
        this.includeRelationships = includeRelationships;
    }


    /**
     * Gets the includeRenditions value for this GetChildren.
     * 
     * @return includeRenditions
     */
    public java.lang.Boolean getIncludeRenditions() {
        return includeRenditions;
    }


    /**
     * Sets the includeRenditions value for this GetChildren.
     * 
     * @param includeRenditions
     */
    public void setIncludeRenditions(java.lang.Boolean includeRenditions) {
        this.includeRenditions = includeRenditions;
    }


    /**
     * Gets the includeACL value for this GetChildren.
     * 
     * @return includeACL
     */
    public java.lang.Boolean getIncludeACL() {
        return includeACL;
    }


    /**
     * Sets the includeACL value for this GetChildren.
     * 
     * @param includeACL
     */
    public void setIncludeACL(java.lang.Boolean includeACL) {
        this.includeACL = includeACL;
    }


    /**
     * Gets the maxItems value for this GetChildren.
     * 
     * @return maxItems
     */
    public java.math.BigInteger getMaxItems() {
        return maxItems;
    }


    /**
     * Sets the maxItems value for this GetChildren.
     * 
     * @param maxItems
     */
    public void setMaxItems(java.math.BigInteger maxItems) {
        this.maxItems = maxItems;
    }


    /**
     * Gets the skipCount value for this GetChildren.
     * 
     * @return skipCount
     */
    public java.math.BigInteger getSkipCount() {
        return skipCount;
    }


    /**
     * Sets the skipCount value for this GetChildren.
     * 
     * @param skipCount
     */
    public void setSkipCount(java.math.BigInteger skipCount) {
        this.skipCount = skipCount;
    }


    /**
     * Gets the orderBy value for this GetChildren.
     * 
     * @return orderBy
     */
    public java.lang.String getOrderBy() {
        return orderBy;
    }


    /**
     * Sets the orderBy value for this GetChildren.
     * 
     * @param orderBy
     */
    public void setOrderBy(java.lang.String orderBy) {
        this.orderBy = orderBy;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GetChildren)) return false;
        GetChildren other = (GetChildren) obj;
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
            ((this.folderId==null && other.getFolderId()==null) || 
             (this.folderId!=null &&
              this.folderId.equals(other.getFolderId()))) &&
            ((this.filter==null && other.getFilter()==null) || 
             (this.filter!=null &&
              this.filter.equals(other.getFilter()))) &&
            ((this.includeAllowableActions==null && other.getIncludeAllowableActions()==null) || 
             (this.includeAllowableActions!=null &&
              this.includeAllowableActions.equals(other.getIncludeAllowableActions()))) &&
            ((this.includeRelationships==null && other.getIncludeRelationships()==null) || 
             (this.includeRelationships!=null &&
              this.includeRelationships.equals(other.getIncludeRelationships()))) &&
            ((this.includeRenditions==null && other.getIncludeRenditions()==null) || 
             (this.includeRenditions!=null &&
              this.includeRenditions.equals(other.getIncludeRenditions()))) &&
            ((this.includeACL==null && other.getIncludeACL()==null) || 
             (this.includeACL!=null &&
              this.includeACL.equals(other.getIncludeACL()))) &&
            ((this.maxItems==null && other.getMaxItems()==null) || 
             (this.maxItems!=null &&
              this.maxItems.equals(other.getMaxItems()))) &&
            ((this.skipCount==null && other.getSkipCount()==null) || 
             (this.skipCount!=null &&
              this.skipCount.equals(other.getSkipCount()))) &&
            ((this.orderBy==null && other.getOrderBy()==null) || 
             (this.orderBy!=null &&
              this.orderBy.equals(other.getOrderBy())));
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
        if (getFolderId() != null) {
            _hashCode += getFolderId().hashCode();
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
        if (getIncludeRenditions() != null) {
            _hashCode += getIncludeRenditions().hashCode();
        }
        if (getIncludeACL() != null) {
            _hashCode += getIncludeACL().hashCode();
        }
        if (getMaxItems() != null) {
            _hashCode += getMaxItems().hashCode();
        }
        if (getSkipCount() != null) {
            _hashCode += getSkipCount().hashCode();
        }
        if (getOrderBy() != null) {
            _hashCode += getOrderBy().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GetChildren.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200901", ">getChildren"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("repositoryId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200901", "repositoryId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("folderId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200901", "folderId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
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
        elemField.setFieldName("includeRenditions");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200901", "includeRenditions"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
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
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("orderBy");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200901", "orderBy"));
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
