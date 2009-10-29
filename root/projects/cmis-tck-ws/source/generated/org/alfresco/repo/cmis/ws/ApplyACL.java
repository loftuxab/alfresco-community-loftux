/**
 * ApplyACL.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.alfresco.repo.cmis.ws;

public class ApplyACL  implements java.io.Serializable {
    private java.lang.String repositoryId;

    private java.lang.String objectId;

    private org.alfresco.repo.cmis.ws.CmisAccessControlListType addACEs;

    private org.alfresco.repo.cmis.ws.CmisAccessControlListType removeACEs;

    private org.alfresco.repo.cmis.ws.EnumACLPropagation propogationType;

    public ApplyACL() {
    }

    public ApplyACL(
           java.lang.String repositoryId,
           java.lang.String objectId,
           org.alfresco.repo.cmis.ws.CmisAccessControlListType addACEs,
           org.alfresco.repo.cmis.ws.CmisAccessControlListType removeACEs,
           org.alfresco.repo.cmis.ws.EnumACLPropagation propogationType) {
           this.repositoryId = repositoryId;
           this.objectId = objectId;
           this.addACEs = addACEs;
           this.removeACEs = removeACEs;
           this.propogationType = propogationType;
    }


    /**
     * Gets the repositoryId value for this ApplyACL.
     * 
     * @return repositoryId
     */
    public java.lang.String getRepositoryId() {
        return repositoryId;
    }


    /**
     * Sets the repositoryId value for this ApplyACL.
     * 
     * @param repositoryId
     */
    public void setRepositoryId(java.lang.String repositoryId) {
        this.repositoryId = repositoryId;
    }


    /**
     * Gets the objectId value for this ApplyACL.
     * 
     * @return objectId
     */
    public java.lang.String getObjectId() {
        return objectId;
    }


    /**
     * Sets the objectId value for this ApplyACL.
     * 
     * @param objectId
     */
    public void setObjectId(java.lang.String objectId) {
        this.objectId = objectId;
    }


    /**
     * Gets the addACEs value for this ApplyACL.
     * 
     * @return addACEs
     */
    public org.alfresco.repo.cmis.ws.CmisAccessControlListType getAddACEs() {
        return addACEs;
    }


    /**
     * Sets the addACEs value for this ApplyACL.
     * 
     * @param addACEs
     */
    public void setAddACEs(org.alfresco.repo.cmis.ws.CmisAccessControlListType addACEs) {
        this.addACEs = addACEs;
    }


    /**
     * Gets the removeACEs value for this ApplyACL.
     * 
     * @return removeACEs
     */
    public org.alfresco.repo.cmis.ws.CmisAccessControlListType getRemoveACEs() {
        return removeACEs;
    }


    /**
     * Sets the removeACEs value for this ApplyACL.
     * 
     * @param removeACEs
     */
    public void setRemoveACEs(org.alfresco.repo.cmis.ws.CmisAccessControlListType removeACEs) {
        this.removeACEs = removeACEs;
    }


    /**
     * Gets the propogationType value for this ApplyACL.
     * 
     * @return propogationType
     */
    public org.alfresco.repo.cmis.ws.EnumACLPropagation getPropogationType() {
        return propogationType;
    }


    /**
     * Sets the propogationType value for this ApplyACL.
     * 
     * @param propogationType
     */
    public void setPropogationType(org.alfresco.repo.cmis.ws.EnumACLPropagation propogationType) {
        this.propogationType = propogationType;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ApplyACL)) return false;
        ApplyACL other = (ApplyACL) obj;
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
            ((this.addACEs==null && other.getAddACEs()==null) || 
             (this.addACEs!=null &&
              this.addACEs.equals(other.getAddACEs()))) &&
            ((this.removeACEs==null && other.getRemoveACEs()==null) || 
             (this.removeACEs!=null &&
              this.removeACEs.equals(other.getRemoveACEs()))) &&
            ((this.propogationType==null && other.getPropogationType()==null) || 
             (this.propogationType!=null &&
              this.propogationType.equals(other.getPropogationType())));
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
        if (getAddACEs() != null) {
            _hashCode += getAddACEs().hashCode();
        }
        if (getRemoveACEs() != null) {
            _hashCode += getRemoveACEs().hashCode();
        }
        if (getPropogationType() != null) {
            _hashCode += getPropogationType().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ApplyACL.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200901", ">applyACL"));
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
        elemField.setFieldName("addACEs");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200901", "addACEs"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200901", "cmisAccessControlListType"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("removeACEs");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200901", "removeACEs"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200901", "cmisAccessControlListType"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("propogationType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200901", "propogationType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200901", "enumACLPropagation"));
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
