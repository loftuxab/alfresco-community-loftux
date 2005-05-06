package org.alfresco.repo.search;

import org.alfresco.repo.ref.ChildAssocRef;
import org.alfresco.repo.ref.NodeRef;

/**
 * Component API for indexing. Delegates to the real index retrieved from the
 * {@link #indexerAndSearcherFactory}
 * 
 * Transactional support is free.
 * 
 * @see Indexer
 * 
 * @author andyh
 * 
 */
public class IndexerComponent implements Indexer
{
    private IndexerAndSearcher indexerAndSearcherFactory;

    public void setIndexerAndSearcherFactory(IndexerAndSearcher indexerAndSearcherFactory)
    {
        this.indexerAndSearcherFactory = indexerAndSearcherFactory;
    }

    public void createNode(ChildAssocRef relationshipRef)
    {
        Indexer indexer = indexerAndSearcherFactory.getIndexer(
                relationshipRef.getChildRef().getStoreRef());
        indexer.createNode(relationshipRef);
    }

    public void updateNode(NodeRef nodeRef)
    {
        Indexer indexer = indexerAndSearcherFactory.getIndexer(nodeRef.getStoreRef());
        indexer.updateNode(nodeRef);
    }

    public void deleteNode(ChildAssocRef relationshipRef)
    {
        Indexer indexer = indexerAndSearcherFactory.getIndexer(
                relationshipRef.getChildRef().getStoreRef());
        indexer.deleteNode(relationshipRef);
    }

    public void createChildRelationship(ChildAssocRef relationshipRef)
    {
        Indexer indexer = indexerAndSearcherFactory.getIndexer(
                relationshipRef.getChildRef().getStoreRef());
        indexer.createChildRelationship(relationshipRef);
    }

    public void updateChildRelationship(ChildAssocRef relationshipBeforeRef, ChildAssocRef relationshipAfterRef)
    {
        Indexer indexer = indexerAndSearcherFactory.getIndexer(
                relationshipBeforeRef.getChildRef().getStoreRef());
        indexer.updateChildRelationship(relationshipBeforeRef, relationshipAfterRef);
    }

    public void deleteChildRelationship(ChildAssocRef relationshipRef)
    {
        Indexer indexer = indexerAndSearcherFactory.getIndexer(
                relationshipRef.getChildRef().getStoreRef());
        indexer.deleteChildRelationship(relationshipRef);
    }

}
