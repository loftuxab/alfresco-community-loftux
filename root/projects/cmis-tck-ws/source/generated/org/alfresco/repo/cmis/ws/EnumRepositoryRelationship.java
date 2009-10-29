/**
 * EnumRepositoryRelationship.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.alfresco.repo.cmis.ws;

public class EnumRepositoryRelationship implements java.io.Serializable {
    private java.lang.String _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected EnumRepositoryRelationship(java.lang.String value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final java.lang.String _self = "self";
    public static final java.lang.String _replica = "replica";
    public static final java.lang.String _peer = "peer";
    public static final java.lang.String _parent = "parent";
    public static final java.lang.String _child = "child";
    public static final java.lang.String _archive = "archive";
    public static final EnumRepositoryRelationship self = new EnumRepositoryRelationship(_self);
    public static final EnumRepositoryRelationship replica = new EnumRepositoryRelationship(_replica);
    public static final EnumRepositoryRelationship peer = new EnumRepositoryRelationship(_peer);
    public static final EnumRepositoryRelationship parent = new EnumRepositoryRelationship(_parent);
    public static final EnumRepositoryRelationship child = new EnumRepositoryRelationship(_child);
    public static final EnumRepositoryRelationship archive = new EnumRepositoryRelationship(_archive);
    public java.lang.String getValue() { return _value_;}
    public static EnumRepositoryRelationship fromValue(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        EnumRepositoryRelationship enumeration = (EnumRepositoryRelationship)
            _table_.get(value);
        if (enumeration==null) throw new java.lang.IllegalArgumentException();
        return enumeration;
    }
    public static EnumRepositoryRelationship fromString(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        return fromValue(value);
    }
    public boolean equals(java.lang.Object obj) {return (obj == this);}
    public int hashCode() { return toString().hashCode();}
    public java.lang.String toString() { return _value_;}
    public java.lang.Object readResolve() throws java.io.ObjectStreamException { return fromValue(_value_);}
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new org.apache.axis.encoding.ser.EnumSerializer(
            _javaType, _xmlType);
    }
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new org.apache.axis.encoding.ser.EnumDeserializer(
            _javaType, _xmlType);
    }
    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(EnumRepositoryRelationship.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200901", "enumRepositoryRelationship"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
