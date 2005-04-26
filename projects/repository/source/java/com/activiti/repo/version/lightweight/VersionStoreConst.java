/**
 * Created on Apr 21, 2005
 */
package com.activiti.repo.version.lightweight;

import com.activiti.repo.dictionary.ClassRef;
import com.activiti.repo.ref.QName;
import com.activiti.repo.ref.qname.QNamePattern;
import com.activiti.repo.ref.qname.SimpleQNamePattern;
import com.activiti.repo.version.Version;

/**
 * interface conating the constants used by the light weight 
 * version store implementation
 * 
 * @author Roy Wetherall
 */
public interface VersionStoreConst
{
    /**
     * Namespace
     */
    public static final String NAMESPACE_URI = "http://www.activiti.com/lightWeightVersionStore/1.0";
    
    /**
     * Version history type
     */
    public static final String TYPE_VERSION_HISTORY = "versionHistory";
    public static final QName TYPE_QNAME_VERSION_HISTORY = QName.createQName(NAMESPACE_URI, TYPE_VERSION_HISTORY);
    public static final ClassRef CLASS_REF_VERSION_HISTORY = new ClassRef(TYPE_QNAME_VERSION_HISTORY);
    
    /**
     * Version history properties and associations
     */
    public static final String PROP_VERSIONED_NODE_ID = "versionedNodeId";
    public static final QName PROP_QNAME_VERSIONED_NODE_ID = QName.createQName(NAMESPACE_URI, PROP_VERSIONED_NODE_ID);        
    public static final QName ASSOC_ROOT_VERSION = QName.createQName(NAMESPACE_URI, "rootVersion");
    
    /**
     * Verison type
     */
    public static final String TYPE_VERSION = "version";
    public static final QName TYPE_QNAME_VERSION = QName.createQName(NAMESPACE_URI, TYPE_VERSION);
    public static final ClassRef CLASS_REF_VERSION = new ClassRef(TYPE_QNAME_VERSION);
    
    /**
     * Version type properties and associations
     */
    public static final QName PROP_QNAME_VERSION_LABEL = QName.createQName(NAMESPACE_URI, Version.PROP_VERSION_LABEL);
    public static final QName PROP_QNAME_VERSION_NUMBER = QName.createQName(NAMESPACE_URI, Version.PROP_VERSION_NUMBER);
    public static final QName PROP_QNAME_VERSION_CREATED_DATE = QName.createQName(NAMESPACE_URI, Version.PROP_CREATED_DATE);
    public static final QName PROP_QNAME_FROZEN_NODE_ID = QName.createQName(NAMESPACE_URI, Version.PROP_FROZEN_NODE_ID);
    public static final QName PROP_QNAME_FROZEN_NODE_TYPE = QName.createQName(NAMESPACE_URI, Version.PROP_FROZEN_NODE_TYPE);
    public static final QName PROP_QNAME_FROZEN_NODE_STORE_PROTOCOL = QName.createQName(NAMESPACE_URI, Version.PROP_FROZEN_NODE_STORE_PROTOCOL);
    public static final QName PROP_QNAME_FROZEN_NODE_STORE_ID = QName.createQName(NAMESPACE_URI, Version.PROP_FROZEN_NODE_STORE_ID);
    public static final QName ASSOC_SUCCESSOR = QName.createQName(NAMESPACE_URI, "successor");    
    
    /**
     * Versioned attribute type
     * TODO should be named versionedProperty
     */
    public static final String TYPE_VERSIONED_ATTRIBUTE = "versionedAttribute";
    public static final QName TYPE_QNAME_VERSIONED_ATTRIBUTE = QName.createQName(NAMESPACE_URI, TYPE_VERSIONED_ATTRIBUTE);
    public static final ClassRef CLASS_REF_VERSIONED_ATTRIBUTE = new ClassRef(TYPE_QNAME_VERSIONED_ATTRIBUTE);
    
    /**
     * Versioned attribute properties
     */
    public static final String PROP_QNAME = "qname";
    public static final String PROP_VALUE = "value";
    public static final QName PROP_QNAME_QNAME = QName.createQName(NAMESPACE_URI, PROP_QNAME);
    public static final QName PROP_QNAME_VALUE = QName.createQName(NAMESPACE_URI, PROP_VALUE);
    
    /**
     * Versioned child assoc type
     */
    public static final String TYPE_VERSIONED_CHILD_ASSOC = "versionedChildAssoc";
    public static final QName TYPE_QNAME_VERSIONED_CHILD_ASSOC = QName.createQName(NAMESPACE_URI, TYPE_VERSIONED_CHILD_ASSOC);
    public static final ClassRef CLASS_REF_VERSIONED_CHILD_ASSOC = new ClassRef(TYPE_QNAME_VERSIONED_CHILD_ASSOC);
    
    /**
     * Versioned child assoc properties
     */
    public static final String PROP_ASSOC_QNAME = "assocQName";
    public static final String PROP_IS_PRIMARY = "isPrimary";
    public static final String PROP_NTH_SIBLING = "nthSibling";
    public static final QName PROP_QNAME_ASSOC_QNAME = QName.createQName(NAMESPACE_URI, PROP_ASSOC_QNAME);
    public static final QName PROP_QNAME_IS_PRIMARY = QName.createQName(NAMESPACE_URI, PROP_IS_PRIMARY);
    public static final QName PROP_QNAME_NTH_SIBLING = QName.createQName(NAMESPACE_URI, PROP_NTH_SIBLING);
    
    /**
     * Child relationship names
     */
    public static final String CHILD_VERSION_HISTORIES = "versionHistory";
    public static final String CHILD_VERSIONS = "version";
    public static final String CHILD_VERSIONED_ATTRIBUTES = "versionedAttributes";
    public static final String CHILD_VERSIONED_CHILD_ASSOCS = "versionedChildAssocs";
    
    public static final QName CHILD_QNAME_VERSION_HISTORIES = QName.createQName(NAMESPACE_URI, CHILD_VERSION_HISTORIES);
    public static final QName CHILD_QNAME_VERSIONS = QName.createQName(NAMESPACE_URI, CHILD_VERSIONS);
    public static final QName CHILD_QNAME_VERSIONED_ATTRIBUTES = QName.createQName(NAMESPACE_URI, CHILD_VERSIONED_ATTRIBUTES);
    public static final QName CHILD_QNAME_VERSIONED_CHILD_ASSOCS = QName.createQName(NAMESPACE_URI, CHILD_VERSIONED_CHILD_ASSOCS);
    public static final QNamePattern CHILD_QNAME_PATTERN_VERSIONED_CHILD_ASSOCS = new SimpleQNamePattern(CHILD_QNAME_VERSIONED_CHILD_ASSOCS);
}
