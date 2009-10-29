/**
 * GetContentStream.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.alfresco.repo.cmis.ws;

public class GetContentStream  implements java.io.Serializable {
    private java.lang.String repositoryId;

    private java.lang.String documentId;

    private java.lang.String streamId;

    public GetContentStream() {
    }

    public GetContentStream(
           java.lang.String repositoryId,
           java.lang.String documentId,
           java.lang.String streamId) {
           this.repositoryId = repositoryId;
           this.documentId = documentId;
           this.streamId = streamId;
    }


    /**
     * Gets the repositoryId value for this GetContentStream.
     * 
     * @return repositoryId
     */
    public java.lang.String getRepositoryId() {
        return repositoryId;
    }


    /**
     * Sets the repositoryId value for this GetContentStream.
     * 
     * @param repositoryId
     */
    public void setRepositoryId(java.lang.String repositoryId) {
        this.repositoryId = repositoryId;
    }


    /**
     * Gets the documentId value for this GetContentStream.
     * 
     * @return documentId
     */
    public java.lang.String getDocumentId() {
        return documentId;
    }


    /**
     * Sets the documentId value for this GetContentStream.
     * 
     * @param documentId
     */
    public void setDocumentId(java.lang.String documentId) {
        this.documentId = documentId;
    }


    /**
     * Gets the streamId value for this GetContentStream.
     * 
     * @return streamId
     */
    public java.lang.String getStreamId() {
        return streamId;
    }


    /**
     * Sets the streamId value for this GetContentStream.
     * 
     * @param streamId
     */
    public void setStreamId(java.lang.String streamId) {
        this.streamId = streamId;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GetContentStream)) return false;
        GetContentStream other = (GetContentStream) obj;
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
            ((this.documentId==null && other.getDocumentId()==null) || 
             (this.documentId!=null &&
              this.documentId.equals(other.getDocumentId()))) &&
            ((this.streamId==null && other.getStreamId()==null) || 
             (this.streamId!=null &&
              this.streamId.equals(other.getStreamId())));
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
        if (getDocumentId() != null) {
            _hashCode += getDocumentId().hashCode();
        }
        if (getStreamId() != null) {
            _hashCode += getStreamId().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GetContentStream.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200901", ">getContentStream"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("repositoryId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200901", "repositoryId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("documentId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200901", "documentId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("streamId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200901", "streamId"));
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
