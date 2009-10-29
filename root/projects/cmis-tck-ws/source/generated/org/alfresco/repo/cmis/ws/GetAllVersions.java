/**
 * GetAllVersions.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.alfresco.repo.cmis.ws;

public class GetAllVersions  implements java.io.Serializable {
    private java.lang.String repositoryId;

    private java.lang.String versionSeriesId;

    private java.lang.String filter;

    private java.lang.Boolean includeAllowableActions;

    private org.alfresco.repo.cmis.ws.EnumIncludeRelationships includeRelationships;

    public GetAllVersions() {
    }

    public GetAllVersions(
           java.lang.String repositoryId,
           java.lang.String versionSeriesId,
           java.lang.String filter,
           java.lang.Boolean includeAllowableActions,
           org.alfresco.repo.cmis.ws.EnumIncludeRelationships includeRelationships) {
           this.repositoryId = repositoryId;
           this.versionSeriesId = versionSeriesId;
           this.filter = filter;
           this.includeAllowableActions = includeAllowableActions;
           this.includeRelationships = includeRelationships;
    }


    /**
     * Gets the repositoryId value for this GetAllVersions.
     * 
     * @return repositoryId
     */
    public java.lang.String getRepositoryId() {
        return repositoryId;
    }


    /**
     * Sets the repositoryId value for this GetAllVersions.
     * 
     * @param repositoryId
     */
    public void setRepositoryId(java.lang.String repositoryId) {
        this.repositoryId = repositoryId;
    }


    /**
     * Gets the versionSeriesId value for this GetAllVersions.
     * 
     * @return versionSeriesId
     */
    public java.lang.String getVersionSeriesId() {
        return versionSeriesId;
    }


    /**
     * Sets the versionSeriesId value for this GetAllVersions.
     * 
     * @param versionSeriesId
     */
    public void setVersionSeriesId(java.lang.String versionSeriesId) {
        this.versionSeriesId = versionSeriesId;
    }


    /**
     * Gets the filter value for this GetAllVersions.
     * 
     * @return filter
     */
    public java.lang.String getFilter() {
        return filter;
    }


    /**
     * Sets the filter value for this GetAllVersions.
     * 
     * @param filter
     */
    public void setFilter(java.lang.String filter) {
        this.filter = filter;
    }


    /**
     * Gets the includeAllowableActions value for this GetAllVersions.
     * 
     * @return includeAllowableActions
     */
    public java.lang.Boolean getIncludeAllowableActions() {
        return includeAllowableActions;
    }


    /**
     * Sets the includeAllowableActions value for this GetAllVersions.
     * 
     * @param includeAllowableActions
     */
    public void setIncludeAllowableActions(java.lang.Boolean includeAllowableActions) {
        this.includeAllowableActions = includeAllowableActions;
    }


    /**
     * Gets the includeRelationships value for this GetAllVersions.
     * 
     * @return includeRelationships
     */
    public org.alfresco.repo.cmis.ws.EnumIncludeRelationships getIncludeRelationships() {
        return includeRelationships;
    }


    /**
     * Sets the includeRelationships value for this GetAllVersions.
     * 
     * @param includeRelationships
     */
    public void setIncludeRelationships(org.alfresco.repo.cmis.ws.EnumIncludeRelationships includeRelationships) {
        this.includeRelationships = includeRelationships;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GetAllVersions)) return false;
        GetAllVersions other = (GetAllVersions) obj;
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
            ((this.versionSeriesId==null && other.getVersionSeriesId()==null) || 
             (this.versionSeriesId!=null &&
              this.versionSeriesId.equals(other.getVersionSeriesId()))) &&
            ((this.filter==null && other.getFilter()==null) || 
             (this.filter!=null &&
              this.filter.equals(other.getFilter()))) &&
            ((this.includeAllowableActions==null && other.getIncludeAllowableActions()==null) || 
             (this.includeAllowableActions!=null &&
              this.includeAllowableActions.equals(other.getIncludeAllowableActions()))) &&
            ((this.includeRelationships==null && other.getIncludeRelationships()==null) || 
             (this.includeRelationships!=null &&
              this.includeRelationships.equals(other.getIncludeRelationships())));
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
        if (getVersionSeriesId() != null) {
            _hashCode += getVersionSeriesId().hashCode();
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
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GetAllVersions.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200901", ">getAllVersions"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("repositoryId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200901", "repositoryId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("versionSeriesId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200901", "versionSeriesId"));
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
