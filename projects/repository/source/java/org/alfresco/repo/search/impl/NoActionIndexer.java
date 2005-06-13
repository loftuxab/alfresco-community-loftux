/*
 * Created on Mar 24, 2005
 * 
 * TODO Comment this class
 * 
 * 
 */
package org.alfresco.repo.search.impl;

import org.alfresco.repo.search.Indexer;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;

/**
 * A no action indexer - the indexing is done automatically along with
 * persistence
 * 
 * TODO: Rename to Adaptor?
 * 
 * @author andyh
 * 
 */
public class NoActionIndexer implements Indexer
{

    public void createNode(ChildAssociationRef relationshipRef)
    {
        return;
    }

    public void updateNode(NodeRef nodeRef)
    {
        return;
    }

    public void deleteNode(ChildAssociationRef relationshipRef)
    {
        return;
    }

    public void createChildRelationship(ChildAssociationRef relationshipRef)
    {
        return;
    }

    public void updateChildRelationship(ChildAssociationRef relationshipBeforeRef, ChildAssociationRef relationshipAfterRef)
    {
        return;
    }

    public void deleteChildRelationship(ChildAssociationRef relationshipRef)
    {
        return;
    }

}
