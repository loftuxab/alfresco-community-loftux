/*
 * Created on Mar 24, 2005
 */
package com.activiti.repo.search;

import com.activiti.repo.ref.ChildRelationshipRef;
import com.activiti.repo.ref.NodeRef;

/**
 * Component API for indexing.
 * 
 * Transactional support is free.
 * 
 * @see Indexer
 * 
 * TODO: Support for Spring and IOC. Avoid the singleton pattern.
 * 
 * @author andyh
 *
 */
public class IndexerComponent implements Indexer
{

   /*
    * Indexer implementation
    * 
    * All just get the real index and delegate
    * 
    */
   
   public void createNode(ChildRelationshipRef relationshipRef)
   {
      Indexer indexer = IndexerAndSearcherFactory.getInstance().getIndexer(relationshipRef.getParentRef().getStoreRef());
      indexer.createNode(relationshipRef);
   }

   public void updateNode(NodeRef nodeRef)
   {
      Indexer indexer = IndexerAndSearcherFactory.getInstance().getIndexer(nodeRef.getStoreRef());
      indexer.updateNode(nodeRef);
   }

   public void deleteNode(ChildRelationshipRef relationshipRef)
   {
      Indexer indexer = IndexerAndSearcherFactory.getInstance().getIndexer(relationshipRef.getParentRef().getStoreRef());
      indexer.deleteNode(relationshipRef);
   }

   public void createChildRelationship(ChildRelationshipRef relationshipRef)
   {
      Indexer indexer = IndexerAndSearcherFactory.getInstance().getIndexer(relationshipRef.getParentRef().getStoreRef());
      indexer.createChildRelationship(relationshipRef);
   }

   public void updateChildRelationship(ChildRelationshipRef relationshipBeforeRef, ChildRelationshipRef relationshipAfterRef)
   {
      Indexer indexer = IndexerAndSearcherFactory.getInstance().getIndexer(relationshipBeforeRef.getParentRef().getStoreRef());
      indexer.updateChildRelationship(relationshipBeforeRef, relationshipAfterRef);
   }

   public void deleteChildRelationship(ChildRelationshipRef relationshipRef)
   {
      Indexer indexer = IndexerAndSearcherFactory.getInstance().getIndexer(relationshipRef.getParentRef().getStoreRef());
      indexer.deleteChildRelationship(relationshipRef);
   }

 
}
