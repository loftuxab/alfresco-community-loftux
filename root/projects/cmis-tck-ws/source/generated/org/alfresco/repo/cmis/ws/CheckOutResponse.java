/**
 * CheckOutResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.alfresco.repo.cmis.ws;

public class CheckOutResponse  implements java.io.Serializable {
    private java.lang.String documentId;

    private boolean contentCopied;

    /* This is an extension element to hold any
     * 							repository or
     * 							vendor-specific extensions */
    private org.alfresco.repo.cmis.ws.CmisExtensionType extension;

    public CheckOutResponse() {
    }

    public CheckOutResponse(
           java.lang.String documentId,
           boolean contentCopied,
           org.alfresco.repo.cmis.ws.CmisExtensionType extension) {
           this.documentId = documentId;
           this.contentCopied = contentCopied;
           this.extension = extension;
    }


    /**
     * Gets the documentId value for this CheckOutResponse.
     * 
     * @return documentId
     */
    public java.lang.String getDocumentId() {
        return documentId;
    }


    /**
     * Sets the documentId value for this CheckOutResponse.
     * 
     * @param documentId
     */
    public void setDocumentId(java.lang.String documentId) {
        this.documentId = documentId;
    }


    /**
     * Gets the contentCopied value for this CheckOutResponse.
     * 
     * @return contentCopied
     */
    public boolean isContentCopied() {
        return contentCopied;
    }


    /**
     * Sets the contentCopied value for this CheckOutResponse.
     * 
     * @param contentCopied
     */
    public void setContentCopied(boolean contentCopied) {
        this.contentCopied = contentCopied;
    }


    /**
     * Gets the extension value for this CheckOutResponse.
     * 
     * @return extension   * This is an extension element to hold any
     * 							repository or
     * 							vendor-specific extensions
     */
    public org.alfresco.repo.cmis.ws.CmisExtensionType getExtension() {
        return extension;
    }


    /**
     * Sets the extension value for this CheckOutResponse.
     * 
     * @param extension   * This is an extension element to hold any
     * 							repository or
     * 							vendor-specific extensions
     */
    public void setExtension(org.alfresco.repo.cmis.ws.CmisExtensionType extension) {
        this.extension = extension;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CheckOutResponse)) return false;
        CheckOutResponse other = (CheckOutResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.documentId==null && other.getDocumentId()==null) || 
             (this.documentId!=null &&
              this.documentId.equals(other.getDocumentId()))) &&
            this.contentCopied == other.isContentCopied() &&
            ((this.extension==null && other.getExtension()==null) || 
             (this.extension!=null &&
              this.extension.equals(other.getExtension())));
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
        if (getDocumentId() != null) {
            _hashCode += getDocumentId().hashCode();
        }
        _hashCode += (isContentCopied() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        if (getExtension() != null) {
            _hashCode += getExtension().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(CheckOutResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">checkOutResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("documentId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "documentId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("contentCopied");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "contentCopied"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("extension");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "extension"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "cmisExtensionType"));
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
