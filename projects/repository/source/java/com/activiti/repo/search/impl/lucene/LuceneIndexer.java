/*
 * Created on Mar 24, 2005
 * 
 */
package com.activiti.repo.search.impl.lucene;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.transaction.Status;
import javax.transaction.xa.XAResource;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;

import com.activiti.repo.domain.Node;
import com.activiti.repo.node.NodeService;
import com.activiti.repo.ref.ChildAssocRef;
import com.activiti.repo.ref.NodeRef;
import com.activiti.repo.ref.Path;
import com.activiti.repo.ref.StoreRef;
import com.activiti.repo.search.Indexer;
import com.activiti.repo.search.IndexerException;
import com.activiti.repo.search.impl.lucene.analysis.PathTokenFilter;

/**
 * The implementation of the lucene based indexer. Supports basic transactional
 * behaviour if used on its own.
 * 
 * @author andyh
 * 
 */
public class LuceneIndexer extends LuceneBase implements Indexer
{
   /**
    * The node service we use to get information about nodes
    */
   private NodeService nodeService;

   /**
    * A list of all deletoins we have made - at merge these deletions need to be
    * made against the main index.
    * 
    * TODO: Consider if this informantion needs to be persisted for recovery
    */

   private Set<NodeRef> deletions = new HashSet<NodeRef>();

   /**
    * A list of all nodes we have altered This list is used to drive the
    * background full text seach index which is to time consuming to do as part
    * of the transaction. The commit of the list of nodes to reindex is done as
    * part of the transaction.
    * 
    * TODO: Condsider persistence and recovery
    */

   private Set<NodeRef> fts = new HashSet<NodeRef>();

   /**
    * The status of this index - follows javax.transaction.Status
    */
   
   private int status = Status.STATUS_UNKNOWN;

   /**
    * Has this index been modified?
    */
   
   private boolean isModified = false;
   
   /**
    * Setter for getting the node service via IOC
    * Used in the Spring container
    * 
    * @param nodeService
    */

   public void setNodeService(NodeService nodeService)
   {
      this.nodeService = nodeService;
   }

   /**
    * Utility method to check we are in the correct state to do work 
    * Also keeps track of the dirty flag.
    *
    */
   
   private void checkAbleToDoWork()
   {
      switch (status)
      {
      case Status.STATUS_UNKNOWN:
         status = Status.STATUS_ACTIVE;
         break;
      case Status.STATUS_ACTIVE:
         // OK
         break;
      default:
         // All other states are a problem
         throw new IndexerException(buildErrorString());
      }
      isModified = true;
   }

   /**
    * Utility method to report errors about invalid state.
    * 
    * @return
    */
   private String buildErrorString()
   {
      StringBuffer buffer = new StringBuffer();
      buffer.append("The indexer is unable to accept more work: ");
      switch (status)
      {
      case Status.STATUS_COMMITTED:
         buffer.append("The indexer has been committed");
         break;
      case Status.STATUS_COMMITTING:
         buffer.append("The indexer is committing");
         break;
      case Status.STATUS_MARKED_ROLLBACK:
         buffer.append("The indexer is marked for rollback");
         break;
      case Status.STATUS_PREPARED:
         buffer.append("The indexer is prepared to commit");
         break;
      case Status.STATUS_PREPARING:
         buffer.append("The indexer is preparing to commit");
         break;
      case Status.STATUS_ROLLEDBACK:
         buffer.append("The indexer has been rolled back");
         break;
      case Status.STATUS_ROLLING_BACK:
         buffer.append("The indexer is rolling back");
         break;
      case Status.STATUS_UNKNOWN:
         buffer.append("The indexer is in an unknown state");
         break;
      default:
         break;
      }
      return buffer.toString();
   }

   /*
    * Indexer Implementation
    */
   
   public void createNode(ChildAssocRef relationshipRef) throws IndexerException
   {
      checkAbleToDoWork();
      try
      {
         reindex(relationshipRef.getParentRef());
         reindex(relationshipRef.getChildRef());
      }
      catch (IOException e)
      {
         throw new IndexerException(e);
      }
   }

   public void updateNode(NodeRef nodeRef) throws IndexerException
   {
      checkAbleToDoWork();
      try
      {
         reindex(nodeRef);
      }
      catch (IOException e)
      {
         throw new IndexerException(e);
      }
   }

   public void deleteNode(ChildAssocRef relationshipRef) throws IndexerException
   {
      checkAbleToDoWork();
      try
      {
         delete(relationshipRef.getChildRef());
      }
      catch (IOException e)
      {
         throw new IndexerException(e);
      }
   }

   public void createChildRelationship(ChildAssocRef relationshipRef) throws IndexerException
   {
      checkAbleToDoWork();
      try
      {
         // TODO: Optimise
         reindex(relationshipRef.getParentRef());
         reindex(relationshipRef.getChildRef());
      }
      catch (IOException e)
      {
         throw new IndexerException(e);
      }
   }

   public void updateChildRelationship(ChildAssocRef relationshipBeforeRef,
         ChildAssocRef relationshipAfterRef) throws IndexerException
   {
      checkAbleToDoWork();
      try
      {
         // TODO: Optimise
         reindex(relationshipBeforeRef.getParentRef());
         reindex(relationshipBeforeRef.getChildRef());
      }
      catch (IOException e)
      {
         throw new IndexerException(e);
      }
   }

   public void deleteChildRelationship(ChildAssocRef relationshipRef) throws IndexerException
   {
      checkAbleToDoWork();
      try
      {
         // TODO: Optimise
         reindex(relationshipRef.getParentRef());
         reindex(relationshipRef.getChildRef());
      }
      catch (IOException e)
      {
         throw new IndexerException(e);
      }
   }

   /**
    * Generate an indexer 
    * 
    * @param storeRef
    * @param deltaId
    * @return
    */
   public static LuceneIndexer getUpdateIndexer(StoreRef storeRef, String deltaId)
   {
      LuceneIndexer indexer = new LuceneIndexer();
      try
      {
         indexer.initialise(storeRef, deltaId);
      }
      catch (IOException e)
      {
         throw new IndexerException(e);
      }
      return indexer;
   }

   /*
    * Transactional support
    * Used by the resource mananger for indexers.
    */
   
   /**
    * Commit this index
    */
   
   public void commit()
   {
      switch (status)
      {
      case Status.STATUS_COMMITTING:
         throw new IndexerException("Unable to commit: Transaction is committing");
      case Status.STATUS_COMMITTED:
         throw new IndexerException("Unable to commit: Transaction is commited ");
      case Status.STATUS_ROLLING_BACK:
         throw new IndexerException("Unable to commit: Transaction is rolling back");
      case Status.STATUS_ROLLEDBACK:
         throw new IndexerException("Unable to commit: Transaction is aleady rolled back");
      case Status.STATUS_MARKED_ROLLBACK:
         throw new IndexerException("Unable to commit: Transaction is marked for roll back");
      case Status.STATUS_PREPARING:
         throw new IndexerException("Unable to commit: Transaction is preparing");
      case Status.STATUS_ACTIVE:
         // special case - commit from active
         prepare();
      // drop through to do the commit;
      default:
         status = Status.STATUS_COMMITTING;
         try
         {
            // Build the deletion terms
            Set<Term> terms = new HashSet<Term>();
            for (NodeRef nodeRef : deletions)
            {
               terms.add(new Term("ID", nodeRef.getId()));
            }
            // Merge
            mergeDeltaIntoMain(terms);
            status = Status.STATUS_COMMITTED;
         }
         catch (IOException e)
         {
            // If anything goes wrong we try and do a roll back
            rollback();
            throw new IndexerException(e);
         }
         finally
         {
            // Make sure we tidy up 
            deleteDelta();
         }
         break;
      }
   }

   /**
    * Prepare to commit 
    * 
    * At the moment this makes sure we have all the locks
    * 
    * TODO: This is not doing proper serialisation against the index as would 
    * a data base transaction.
    * 
    * @return
    */
   public int prepare()
   {

      switch (status)
      {
      case Status.STATUS_COMMITTING:
         throw new IndexerException("Unable to prepare: Transaction is committing");
      case Status.STATUS_COMMITTED:
         throw new IndexerException("Unable to prepare: Transaction is commited ");
      case Status.STATUS_ROLLING_BACK:
         throw new IndexerException("Unable to prepare: Transaction is rolling back");
      case Status.STATUS_ROLLEDBACK:
         throw new IndexerException("Unable to prepare: Transaction is aleady rolled back");
      case Status.STATUS_MARKED_ROLLBACK:
         throw new IndexerException("Unable to prepare: Transaction is marked for roll back");
      case Status.STATUS_PREPARING:
         throw new IndexerException("Unable to prepare: Transaction is already preparing");
      case Status.STATUS_PREPARED:
         throw new IndexerException("Unable to prepare: Transaction is already prepared");
      default:
         status = Status.STATUS_PREPARING;
         try
         {
            saveDelta();
            prepareToMergeIntoMain();
            status = Status.STATUS_PREPARED;
            return XAResource.XA_OK;
         }
         catch (IOException e)
         {
            status = Status.STATUS_MARKED_ROLLBACK;
            throw new IndexerException(e);
         }
      }
   }

   /**
    * Has this index been modified?
    * @return
    */
   public boolean isModified()
   {
      return isModified;
   }

   /**
    * Return the javax.transaction.Status
    * integer status code
    * 
    * @return
    */
   public int getStatus()
   {
      return status;
   }

   /**
    * Roll back the index changes (this just means they are never added)
    *
    */
   
   public void rollback()
   {
      switch (status)
      {
      case Status.STATUS_COMMITTING:
         throw new IndexerException("Unable to roll back: Transaction is committing");
      case Status.STATUS_COMMITTED:
         throw new IndexerException("Unable to roll back: Transaction is commited ");
      case Status.STATUS_ROLLING_BACK:
         throw new IndexerException("Unable to roll back: Transaction is rolling back");
      case Status.STATUS_ROLLEDBACK:
         throw new IndexerException("Unable to roll back: Transaction is aleady rolled back");
      default:
         status = Status.STATUS_ROLLING_BACK;
         deleteDelta();
         status = Status.STATUS_ROLLEDBACK;
         break;
      }
   }

   /**
    * Mark this index for roll back only.
    * This action can not be reversed.
    * It will reject all other work and only allow roll back.
    *
    */
   
   public void setRollbackOnly()
   {
      switch (status)
      {
      case Status.STATUS_COMMITTING:
         throw new IndexerException("Unable to mark for rollback: Transaction is committing");
      case Status.STATUS_COMMITTED:
         throw new IndexerException("Unable to mark for rollback: Transaction is commited");
      default:
         status = Status.STATUS_MARKED_ROLLBACK;
         break;
      }
   }  
   
   /*
    * Implementation
    */
   
   private void reindex(NodeRef nodeRef) throws IOException
   {
      Set<NodeRef> refs = delete(nodeRef);
      index(refs);
   }

   private Set<NodeRef> delete(NodeRef nodeRef) throws IOException
   {
      Set<NodeRef> refs = new HashSet<NodeRef>();

      refs.addAll(deleteContainerAndBelow(nodeRef, getDeltaRamReader()));
      refs.addAll(deleteContainerAndBelow(nodeRef, getDeltaReader()));

      deletions.addAll(refs);
      return refs;
   }

   private Set<NodeRef> deleteContainerAndBelow(NodeRef nodeRef, IndexReader reader) throws IOException
   {
      Set<NodeRef> refs = new HashSet<NodeRef>();

      int count = reader.delete(new Term("ID", nodeRef.getId()));
      refs.add(nodeRef);

      TermDocs td = reader.termDocs(new Term("ANCESTOR", nodeRef.getId()));
      while (td.next())
      {
         int doc = td.doc();
         Document document = reader.document(doc);
         String id = document.get("ID");
         NodeRef ref = new NodeRef(store, id);
         refs.add(ref);
         reader.delete(doc);
      }
      return refs;
   }

   private void index(Set<NodeRef> nodeRefs) throws IOException
   {
      for (NodeRef ref : nodeRefs)
      {
         index(ref);
      }
   }

   private void index(NodeRef nodeRef) throws IOException
   {
      Document doc = createDocument(nodeRef);
      IndexWriter writer = getDeltaRamWriter();
      writer.addDocument(doc);
      chechAndMergeToDisk(10000);
   }

   private Document createDocument(NodeRef nodeRef)
   {
      // Lucene flags in order are: Stored, indexed, tokenised
      // ID
      Document doc = new Document();
      doc.add(new Field("ID", nodeRef.getId(), true, true, false));

      // Properties
      Map<String, String> properties = nodeService.getProperties(nodeRef);
      for (String propertyName : properties.keySet())
      {
         String value = properties.get(propertyName);
         if (propertyName != null)
         {
            doc.add(new Field("@" + propertyName, value, true, true, false));
         }
      }

      // Parents
      for (NodeRef parent : nodeService.getParents(nodeRef))
      {
         doc.add(new Field("PARENT", parent.getId(), true, true, false));
      }

      // Paths

      if (nodeService.getType(nodeRef).equals(Node.TYPE_CONTAINER))
      {
         StringBuffer pathBuffer = new StringBuffer();
         StringBuffer parentBuffer = new StringBuffer();
         
         ArrayList<NodeRef> parentsInDepthOrderStartingWithSelf = new ArrayList<NodeRef>();
         Collection<Path> paths = nodeService.getPaths(nodeRef, false);
         for(Iterator<Path> it = paths.iterator(); it.hasNext(); /**/)
         {
            Path path = it.next();
            pathBuffer.append(path.toString());
            
            for(Iterator<Path.Element> elit = path.iterator(); elit.hasNext(); /**/)
            {
               Path.Element element = elit.next();
               if(!(element instanceof Path.ChildAssocElement))
               {
                  throw new IndexerException("Confused path: "+path);
               }
               Path.ChildAssocElement cae = (Path.ChildAssocElement)element;
               parentsInDepthOrderStartingWithSelf.add(0, cae.getRef().getParentRef());
               if(!elit.hasNext())
               {
                  parentsInDepthOrderStartingWithSelf.add(0, cae.getRef().getChildRef());
               }
            }
            
            for(NodeRef ref: parentsInDepthOrderStartingWithSelf)
            {
               if(parentBuffer.length() > 0)
               {
                  parentBuffer.append(" ");
               }
               parentBuffer.append(ref.getId());
            }
            
            parentsInDepthOrderStartingWithSelf.clear();
            
            if(it.hasNext())
            {
               pathBuffer.append(PathTokenFilter.PATH_SEPARATOR);
               if(parentBuffer.length() > 0)
               {
                  parentBuffer.append(" ");
               }
               parentBuffer.append(PathTokenFilter.PATH_SEPARATOR);
            }
            
            
         }
         doc.add(new Field("PATH", pathBuffer.toString(), true, true, true));
         doc.add(new Field("ANCESTOR", parentBuffer.toString(), true, true, true));
         
      }

      return doc;
   }

 
}
