/*
 * Created on Mar 24, 2005
 *
 * TODO Comment this class
 * 
 * 
 */
package com.activiti.repo.search.impl;

import com.activiti.repo.ref.ChildRelationshipRef;
import com.activiti.repo.ref.NodeRef;
import com.activiti.repo.search.Indexer;

/**
 * A no action indexer - the indexing is done automatically along with persistence
 * 
 * TODO: Rename to Adaptor?
 * @author andyh
 *
 */
public class NoActionIndexer implements Indexer
{

   public void createNode(ChildRelationshipRef relationshipRef)
   {
     return;
   }

   public void updateNode(NodeRef nodeRef)
   {
      return;
   }

   public void deleteNode(ChildRelationshipRef relationshipRef)
   {
     return;
   }

   public void createChildRelationship(ChildRelationshipRef relationshipRef)
   {
      return;
   }

   public void updateChildRelationship(ChildRelationshipRef relationshipBeforeRef, ChildRelationshipRef relationshipAfterRef)
   {
     return;
   }

   public void deleteChildRelationship(ChildRelationshipRef relationshipRef)
   {
      return;
   }


}
