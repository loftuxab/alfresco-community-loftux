/**
 * EnumAllowableActionsKey.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.alfresco.repo.cmis.ws;

public class EnumAllowableActionsKey implements java.io.Serializable {
    private java.lang.String _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected EnumAllowableActionsKey(java.lang.String value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final java.lang.String _value1 = "canGetDescendents.Folder";
    public static final java.lang.String _value2 = "canGetChildren.Folder";
    public static final java.lang.String _value3 = "canGetParents.Folder";
    public static final java.lang.String _value4 = "canGetFolderParent.Object";
    public static final java.lang.String _value5 = "canCreateDocument.Type";
    public static final java.lang.String _value6 = "canCreateDocument.Folder";
    public static final java.lang.String _value7 = "canCreateFolder.Type";
    public static final java.lang.String _value8 = "canCreateFolder.Folder";
    public static final java.lang.String _value9 = "canCreateRelationship.Type";
    public static final java.lang.String _value10 = "canCreateRelationship.Source";
    public static final java.lang.String _value11 = "canCreateRelationship.Target";
    public static final java.lang.String _value12 = "canCreatePolicy.Type";
    public static final java.lang.String _value13 = "canGetProperties.Object";
    public static final java.lang.String _value14 = "canViewContent.Object";
    public static final java.lang.String _value15 = "canUpdateProperties.Object";
    public static final java.lang.String _value16 = "canMove.Object";
    public static final java.lang.String _value17 = "canMove.Target";
    public static final java.lang.String _value18 = "canMove.Source";
    public static final java.lang.String _value19 = "canDelete.Object";
    public static final java.lang.String _value20 = "canDeleteTree.Folder";
    public static final java.lang.String _value21 = "canSetContent.Document";
    public static final java.lang.String _value22 = "canDeleteContent.Document";
    public static final java.lang.String _value23 = "canAddToFolder.Object";
    public static final java.lang.String _value24 = "canAddToFolder.Folder";
    public static final java.lang.String _value25 = "canRemoveFromFolder.Object";
    public static final java.lang.String _value26 = "canRemoveFromFolder.Folder";
    public static final java.lang.String _value27 = "canCheckout.Document";
    public static final java.lang.String _value28 = "canCancelCheckout.Document";
    public static final java.lang.String _value29 = "canCheckin.Document";
    public static final java.lang.String _value30 = "canGetAllVersions.VersionSeries";
    public static final java.lang.String _value31 = "canGetObjectRelationships.Object";
    public static final java.lang.String _value32 = "canAddPolicy.Object";
    public static final java.lang.String _value33 = "canAddPolicy.Policy";
    public static final java.lang.String _value34 = "canRemovePolicy.Object";
    public static final java.lang.String _value35 = "canRemovePolicy.Policy";
    public static final java.lang.String _value36 = "canGetAppliedPolicies.Object";
    public static final java.lang.String _value37 = "canGetACL.Object";
    public static final java.lang.String _value38 = "canApplyACL.Object";
    public static final EnumAllowableActionsKey value1 = new EnumAllowableActionsKey(_value1);
    public static final EnumAllowableActionsKey value2 = new EnumAllowableActionsKey(_value2);
    public static final EnumAllowableActionsKey value3 = new EnumAllowableActionsKey(_value3);
    public static final EnumAllowableActionsKey value4 = new EnumAllowableActionsKey(_value4);
    public static final EnumAllowableActionsKey value5 = new EnumAllowableActionsKey(_value5);
    public static final EnumAllowableActionsKey value6 = new EnumAllowableActionsKey(_value6);
    public static final EnumAllowableActionsKey value7 = new EnumAllowableActionsKey(_value7);
    public static final EnumAllowableActionsKey value8 = new EnumAllowableActionsKey(_value8);
    public static final EnumAllowableActionsKey value9 = new EnumAllowableActionsKey(_value9);
    public static final EnumAllowableActionsKey value10 = new EnumAllowableActionsKey(_value10);
    public static final EnumAllowableActionsKey value11 = new EnumAllowableActionsKey(_value11);
    public static final EnumAllowableActionsKey value12 = new EnumAllowableActionsKey(_value12);
    public static final EnumAllowableActionsKey value13 = new EnumAllowableActionsKey(_value13);
    public static final EnumAllowableActionsKey value14 = new EnumAllowableActionsKey(_value14);
    public static final EnumAllowableActionsKey value15 = new EnumAllowableActionsKey(_value15);
    public static final EnumAllowableActionsKey value16 = new EnumAllowableActionsKey(_value16);
    public static final EnumAllowableActionsKey value17 = new EnumAllowableActionsKey(_value17);
    public static final EnumAllowableActionsKey value18 = new EnumAllowableActionsKey(_value18);
    public static final EnumAllowableActionsKey value19 = new EnumAllowableActionsKey(_value19);
    public static final EnumAllowableActionsKey value20 = new EnumAllowableActionsKey(_value20);
    public static final EnumAllowableActionsKey value21 = new EnumAllowableActionsKey(_value21);
    public static final EnumAllowableActionsKey value22 = new EnumAllowableActionsKey(_value22);
    public static final EnumAllowableActionsKey value23 = new EnumAllowableActionsKey(_value23);
    public static final EnumAllowableActionsKey value24 = new EnumAllowableActionsKey(_value24);
    public static final EnumAllowableActionsKey value25 = new EnumAllowableActionsKey(_value25);
    public static final EnumAllowableActionsKey value26 = new EnumAllowableActionsKey(_value26);
    public static final EnumAllowableActionsKey value27 = new EnumAllowableActionsKey(_value27);
    public static final EnumAllowableActionsKey value28 = new EnumAllowableActionsKey(_value28);
    public static final EnumAllowableActionsKey value29 = new EnumAllowableActionsKey(_value29);
    public static final EnumAllowableActionsKey value30 = new EnumAllowableActionsKey(_value30);
    public static final EnumAllowableActionsKey value31 = new EnumAllowableActionsKey(_value31);
    public static final EnumAllowableActionsKey value32 = new EnumAllowableActionsKey(_value32);
    public static final EnumAllowableActionsKey value33 = new EnumAllowableActionsKey(_value33);
    public static final EnumAllowableActionsKey value34 = new EnumAllowableActionsKey(_value34);
    public static final EnumAllowableActionsKey value35 = new EnumAllowableActionsKey(_value35);
    public static final EnumAllowableActionsKey value36 = new EnumAllowableActionsKey(_value36);
    public static final EnumAllowableActionsKey value37 = new EnumAllowableActionsKey(_value37);
    public static final EnumAllowableActionsKey value38 = new EnumAllowableActionsKey(_value38);
    public java.lang.String getValue() { return _value_;}
    public static EnumAllowableActionsKey fromValue(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        EnumAllowableActionsKey enumeration = (EnumAllowableActionsKey)
            _table_.get(value);
        if (enumeration==null) throw new java.lang.IllegalArgumentException();
        return enumeration;
    }
    public static EnumAllowableActionsKey fromString(java.lang.String value)
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
        new org.apache.axis.description.TypeDesc(EnumAllowableActionsKey.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "enumAllowableActionsKey"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
