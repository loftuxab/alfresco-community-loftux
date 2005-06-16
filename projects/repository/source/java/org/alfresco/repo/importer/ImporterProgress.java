package org.alfresco.repo.importer;

import java.io.Serializable;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;


/**
 * Callback interface for monitoring progress of an import.
 * 
 * @author David Caruana
 *
 */
public interface ImporterProgress
{
    /**
     * Report creation of a node.
     * 
     * @param nodeRef  the node ref
     * @param parentRef  the parent ref
     * @param assocName  the child association type name
     * @param childName  the child association name
     */
    public void nodeCreated(NodeRef nodeRef, NodeRef parentRef, QName assocName, QName childName);

    /**
     * Report setting of a property
     * 
     * @param nodeRef  the node ref
     * @param property  the property name
     * @param value  the property value
     */
    public void propertySet(NodeRef nodeRef, QName property, Serializable value);
    
    /**
     * Report addition of an aspect
     * 
     * @param nodeRef  the node ref
     * @param aspect  the aspect
     */
    public void aspectAdded(NodeRef nodeRef, QName aspect);
}
