package org.alfresco.opencmis.dictionary;

import java.io.Serializable;

import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

/**
 * CMIS Property Accessor (get and set property values)
 * 
 * @author davidc
 */
public interface CMISPropertyAccessor
{
    /**
     * Get the CMIS Property Name
     * 
     * @return String
     */
    String getName();

    /**
     * Get the (directly) mapped Alfresco property (if a direct mapping exists)
     * 
     * @return QName
     */
    QName getMappedProperty();

    /**
     * Set the property value for a node
     * 
     * @param nodeRef NodeRef
     * @param value Serializable
     */
    void setValue(NodeRef nodeRef, Serializable value);

    /**
     * Get the property value for a node or an association
     * 
     * @param nodeInfo CMISNodeInfo
     * @return Serializable
     */
    Serializable getValue(CMISNodeInfo nodeInfo);

    /**
     * Creates a node info object form the given node ref.
     */
    CMISNodeInfo createNodeInfo(NodeRef nodeRef);

    /**
     * Creates a node info object form the given association ref.
     */
    CMISNodeInfo createNodeInfo(AssociationRef assocRef);
}
