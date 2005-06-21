package org.alfresco.repo.importer;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

/**
 * The Importer interface encapusulates the strategy for importing 
 * a node into the Repository. 
 * 
 * @author David Caruana
 *
 */
public interface Importer
{
    /**
     * @return  the root node to import into
     */
    public NodeRef getRootRef();
    
    /**
     * @return  the root child association type to import under
     */
    public QName getRootAssocType();

    /**
     * Import a node
     * 
     * @param node  the node description
     * @return  the node ref of the imported node
     */
    public NodeRef importNode(ImportNode node);
}
