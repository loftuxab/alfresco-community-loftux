/**
 * ResultSetRow.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package org.alfresco.repo.webservice.types;

public class ResultSetRow  implements java.io.Serializable {
    private long rowIndex;
    private org.alfresco.repo.webservice.types.NamedValue[] column;
    private java.lang.Float score;
    private org.alfresco.repo.webservice.types.ResultSetRowNode node;

    public ResultSetRow() {
    }

    public ResultSetRow(
           long rowIndex,
           org.alfresco.repo.webservice.types.NamedValue[] column,
           java.lang.Float score,
           org.alfresco.repo.webservice.types.ResultSetRowNode node) {
           this.rowIndex = rowIndex;
           this.column = column;
           this.score = score;
           this.node = node;
    }


    /**
     * Gets the rowIndex value for this ResultSetRow.
     * 
     * @return rowIndex
     */
    public long getRowIndex() {
        return rowIndex;
    }


    /**
     * Sets the rowIndex value for this ResultSetRow.
     * 
     * @param rowIndex
     */
    public void setRowIndex(long rowIndex) {
        this.rowIndex = rowIndex;
    }


    /**
     * Gets the column value for this ResultSetRow.
     * 
     * @return column
     */
    public org.alfresco.repo.webservice.types.NamedValue[] getColumn() {
        return column;
    }


    /**
     * Sets the column value for this ResultSetRow.
     * 
     * @param column
     */
    public void setColumn(org.alfresco.repo.webservice.types.NamedValue[] column) {
        this.column = column;
    }

    public org.alfresco.repo.webservice.types.NamedValue getColumn(int i) {
        return this.column[i];
    }

    public void setColumn(int i, org.alfresco.repo.webservice.types.NamedValue _value) {
        this.column[i] = _value;
    }


    /**
     * Gets the score value for this ResultSetRow.
     * 
     * @return score
     */
    public java.lang.Float getScore() {
        return score;
    }


    /**
     * Sets the score value for this ResultSetRow.
     * 
     * @param score
     */
    public void setScore(java.lang.Float score) {
        this.score = score;
    }


    /**
     * Gets the node value for this ResultSetRow.
     * 
     * @return node
     */
    public org.alfresco.repo.webservice.types.ResultSetRowNode getNode() {
        return node;
    }


    /**
     * Sets the node value for this ResultSetRow.
     * 
     * @param node
     */
    public void setNode(org.alfresco.repo.webservice.types.ResultSetRowNode node) {
        this.node = node;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ResultSetRow)) return false;
        ResultSetRow other = (ResultSetRow) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.rowIndex == other.getRowIndex() &&
            ((this.column==null && other.getColumn()==null) || 
             (this.column!=null &&
              java.util.Arrays.equals(this.column, other.getColumn()))) &&
            ((this.score==null && other.getScore()==null) || 
             (this.score!=null &&
              this.score.equals(other.getScore()))) &&
            ((this.node==null && other.getNode()==null) || 
             (this.node!=null &&
              this.node.equals(other.getNode())));
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
        _hashCode += new Long(getRowIndex()).hashCode();
        if (getColumn() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getColumn());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getColumn(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getScore() != null) {
            _hashCode += getScore().hashCode();
        }
        if (getNode() != null) {
            _hashCode += getNode().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ResultSetRow.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.alfresco.org/ws/model/content/1.0", "ResultSetRow"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("rowIndex");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.alfresco.org/ws/model/content/1.0", "rowIndex"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("column");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.alfresco.org/ws/model/content/1.0", "column"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.alfresco.org/ws/model/content/1.0", "NamedValue"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("score");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.alfresco.org/ws/model/content/1.0", "score"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("node");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.alfresco.org/ws/model/content/1.0", "node"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.alfresco.org/ws/model/content/1.0", ">ResultSetRow>node"));
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
