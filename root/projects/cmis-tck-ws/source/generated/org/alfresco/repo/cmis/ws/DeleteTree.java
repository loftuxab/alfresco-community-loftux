/**
 * DeleteTree.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.alfresco.repo.cmis.ws;

public class DeleteTree  implements java.io.Serializable {
    private java.lang.String repositoryId;

    private java.lang.String folderId;

    private org.alfresco.repo.cmis.ws.EnumUnfileObject unfileObject;

    private java.lang.Boolean continueOnFailure;

    public DeleteTree() {
    }

    public DeleteTree(
           java.lang.String repositoryId,
           java.lang.String folderId,
           org.alfresco.repo.cmis.ws.EnumUnfileObject unfileObject,
           java.lang.Boolean continueOnFailure) {
           this.repositoryId = repositoryId;
           this.folderId = folderId;
           this.unfileObject = unfileObject;
           this.continueOnFailure = continueOnFailure;
    }


    /**
     * Gets the repositoryId value for this DeleteTree.
     * 
     * @return repositoryId
     */
    public java.lang.String getRepositoryId() {
        return repositoryId;
    }


    /**
     * Sets the repositoryId value for this DeleteTree.
     * 
     * @param repositoryId
     */
    public void setRepositoryId(java.lang.String repositoryId) {
        this.repositoryId = repositoryId;
    }


    /**
     * Gets the folderId value for this DeleteTree.
     * 
     * @return folderId
     */
    public java.lang.String getFolderId() {
        return folderId;
    }


    /**
     * Sets the folderId value for this DeleteTree.
     * 
     * @param folderId
     */
    public void setFolderId(java.lang.String folderId) {
        this.folderId = folderId;
    }


    /**
     * Gets the unfileObject value for this DeleteTree.
     * 
     * @return unfileObject
     */
    public org.alfresco.repo.cmis.ws.EnumUnfileObject getUnfileObject() {
        return unfileObject;
    }


    /**
     * Sets the unfileObject value for this DeleteTree.
     * 
     * @param unfileObject
     */
    public void setUnfileObject(org.alfresco.repo.cmis.ws.EnumUnfileObject unfileObject) {
        this.unfileObject = unfileObject;
    }


    /**
     * Gets the continueOnFailure value for this DeleteTree.
     * 
     * @return continueOnFailure
     */
    public java.lang.Boolean getContinueOnFailure() {
        return continueOnFailure;
    }


    /**
     * Sets the continueOnFailure value for this DeleteTree.
     * 
     * @param continueOnFailure
     */
    public void setContinueOnFailure(java.lang.Boolean continueOnFailure) {
        this.continueOnFailure = continueOnFailure;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof DeleteTree)) return false;
        DeleteTree other = (DeleteTree) obj;
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
            ((this.unfileObject==null && other.getUnfileObject()==null) || 
             (this.unfileObject!=null &&
              this.unfileObject.equals(other.getUnfileObject()))) &&
            ((this.continueOnFailure==null && other.getContinueOnFailure()==null) || 
             (this.continueOnFailure!=null &&
              this.continueOnFailure.equals(other.getContinueOnFailure())));
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
        if (getUnfileObject() != null) {
            _hashCode += getUnfileObject().hashCode();
        }
        if (getContinueOnFailure() != null) {
            _hashCode += getContinueOnFailure().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(DeleteTree.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200901", ">deleteTree"));
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
        elemField.setFieldName("unfileObject");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200901", "unfileObject"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200901", "enumUnfileObject"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("continueOnFailure");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200901", "continueOnFailure"));
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
