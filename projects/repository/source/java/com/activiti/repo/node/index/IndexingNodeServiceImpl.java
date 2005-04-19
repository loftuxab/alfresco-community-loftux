package com.activiti.repo.node.index;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.activiti.repo.dictionary.ClassRef;
import com.activiti.repo.node.AssociationExistsException;
import com.activiti.repo.node.InvalidAspectException;
import com.activiti.repo.node.InvalidNodeRefException;
import com.activiti.repo.node.InvalidStoreRefException;
import com.activiti.repo.node.NodeService;
import com.activiti.repo.node.PropertyException;
import com.activiti.repo.node.StoreExistsException;
import com.activiti.repo.ref.ChildAssocRef;
import com.activiti.repo.ref.EntityRef;
import com.activiti.repo.ref.NodeRef;
import com.activiti.repo.ref.Path;
import com.activiti.repo.ref.QName;
import com.activiti.repo.ref.StoreRef;
import com.activiti.repo.search.Indexer;
import com.activiti.repo.search.IndexerComponent;

/**
 * A lightweight <code>NodeService</code> that delegates the work to a
 * <i>proper</i> <code>NodeService</code>, but also ensures that the
 * required calls are made to the {@link com.activiti.repo.search.Indexer indexer}.
 * <p>
 * The use of a delegate to perform all the <b>node</b> manipulation means that
 * implementations of the stores can be swapped in and out but still get indexed.
 * 
 * @author Derek Hulley
 */
public class IndexingNodeServiceImpl implements NodeService
{
    private final NodeService nodeServiceDelegate;
    private final Indexer indexer;

    public IndexingNodeServiceImpl(NodeService nodeServiceDelegate, Indexer indexer)
    {
        this.nodeServiceDelegate = nodeServiceDelegate;
        this.indexer = indexer;
    }
    
    /**
     * Delegates to the assigned {@link #storeServiceDelegate} before using the
     * {@link #indexer} to update the search index.
     * 
     * @see IndexerComponent#createNode(ChildAssocRef)
     */
    public StoreRef createStore(String protocol, String identifier) throws StoreExistsException
    {
        StoreRef storeRef = nodeServiceDelegate.createStore(protocol, identifier);
        // get the root node
        NodeRef rootNodeRef = getRootNode(storeRef);
        // index it
        ChildAssocRef rootAssocRef = new ChildAssocRef(null, null, rootNodeRef);
        indexer.createNode(rootAssocRef);
        // done
        return storeRef;
    }

    /**
     * Direct delegation to assigned {@link #storeServiceDelegate}
     */
    public boolean exists(StoreRef storeRef)
    {
        return nodeServiceDelegate.exists(storeRef);
    }

    /**
     * Direct delegation to assigned {@link #storeServiceDelegate}
     */
    public boolean exists(NodeRef nodeRef)
    {
        return nodeServiceDelegate.exists(nodeRef);
    }
    
    /**
     * Direct delegation to assigned {@link #storeServiceDelegate}
     */
    public NodeRef getRootNode(StoreRef storeRef) throws InvalidStoreRefException
    {
        return nodeServiceDelegate.getRootNode(storeRef);
    }

    /**
     * @see #createNode(NodeRef, QName, String, Map<QName,Serializable>)
     */
    public ChildAssocRef createNode(NodeRef parentRef,
            QName qname,
            ClassRef typeRef) throws InvalidNodeRefException
    {
        return this.createNode(parentRef, qname, typeRef, null);
    }

    /**
     * Delegates to the assigned {@link #nodeServiceDelegate} before using the
     * {@link #indexer} to update the search index.
     * 
     * @see IndexerComponent#createNode(ChildAssocRef)
     */
    public ChildAssocRef createNode(NodeRef parentRef,
            QName qname,
            ClassRef typeRef,
            Map<QName, Serializable> properties) throws InvalidNodeRefException
    {
        // call delegate
        ChildAssocRef assocRef = nodeServiceDelegate.createNode(parentRef, qname, typeRef, properties);
        // update index
        indexer.createNode(assocRef);
        // done
        return assocRef;
    }

    /**
     * Direct delegation to assigned {@link #nodeServiceDelegate}
     */
    public ClassRef getType(NodeRef nodeRef) throws InvalidNodeRefException
    {
        return nodeServiceDelegate.getType(nodeRef);
    }
    
    /**
     * Delegates to the assigned {@link #nodeServiceDelegate} before using the
     * {@link #indexer} to update the search index.
     * 
     * @see IndexerComponent#updateNode(NodeRef)
     */
    public void addAspect(
            NodeRef nodeRef,
            ClassRef aspectRef,
            Map<QName, Serializable> aspectProperties)
            throws InvalidNodeRefException, InvalidAspectException, PropertyException
    {
        // call delegate
        nodeServiceDelegate.addAspect(nodeRef, aspectRef, aspectProperties);
        // update index
        indexer.updateNode(nodeRef);
    }

    /**
     * Delegates to the assigned {@link #nodeServiceDelegate} before using the
     * {@link #indexer} to update the search index.
     * 
     * @see IndexerComponent#updateNode(NodeRef)
     */
    public void removeAspect(
            NodeRef nodeRef,
            ClassRef aspectRef)
            throws InvalidNodeRefException, InvalidAspectException
    {
        // call delegate
        nodeServiceDelegate.removeAspect(nodeRef, aspectRef);
        // update index
        indexer.updateNode(nodeRef);
    }

    /**
     * Direct delegation to assigned {@link #nodeServiceDelegate}
     */
    public boolean hasAspect(NodeRef nodeRef, ClassRef aspectRef) throws InvalidNodeRefException, InvalidAspectException
    {
        return nodeServiceDelegate.hasAspect(nodeRef, aspectRef);
    }

    /**
     * Direct delegation to assigned {@link #nodeServiceDelegate}
     */
    public Set<ClassRef> getAspects(NodeRef nodeRef) throws InvalidNodeRefException
    {
        return nodeServiceDelegate.getAspects(nodeRef);
    }

    /**
     * Delegates to the assigned {@link #nodeServiceDelegate} before using the
     * {@link #indexer} to update the search index.
     * 
     * @see IndexerComponent#deleteNode(ChildAssocRef)
     */
    public void deleteNode(NodeRef nodeRef) throws InvalidNodeRefException
    {
        // TODO: The NodeService should just return the element refs deleted
        
        // get the first primary assoc to this node - it is the last path element
        Path path = getPath(nodeRef);
        Path.ChildAssocElement element = (Path.ChildAssocElement) path.last();
        ChildAssocRef assoc = element.getRef();
        // call delegate
        nodeServiceDelegate.deleteNode(nodeRef);
        // update index
        indexer.deleteNode(assoc);
        // done
    }

    /**
     * Delegates to the assigned {@link #nodeServiceDelegate} before using the
     * {@link #indexer} to update the search index.
     * 
     * @see IndexerComponent#createChildRelationship(ChildAssocRef)
     */
    public ChildAssocRef addChild(NodeRef parentRef, NodeRef childRef, QName qname) throws InvalidNodeRefException
    {
        // call delegate
        ChildAssocRef assoc = nodeServiceDelegate.addChild(parentRef, childRef, qname);
        // update index
        indexer.createChildRelationship(assoc);
        // done
        return assoc;
    }

    /**
     * Delegates to the assigned {@link #nodeServiceDelegate} before using the
     * {@link #indexer} to update the search index.
     * 
     * @see IndexerComponent#deleteChildRelationship(ChildAssocRef)
     * @see IndexerComponent#deleteNode(ChildAssocRef)
     */
    public Collection<EntityRef> removeChild(NodeRef parentRef, NodeRef childRef) throws InvalidNodeRefException
    {
        // call delegate
        Collection<EntityRef> entityRefs = nodeServiceDelegate.removeChild(parentRef, childRef);
        // update index
        for (EntityRef ref : entityRefs)
        {
            if (ref instanceof ChildAssocRef)
            {
                ChildAssocRef assoc = (ChildAssocRef) ref;
                if (assoc.isPrimary())
                {
                    // the node will have been deleted as well
                    indexer.deleteNode(assoc);
                }
                else
                {
                    indexer.deleteChildRelationship(assoc);
                }
            }
        }
        return entityRefs;
    }

    /**
     * Delegates to the assigned {@link #nodeServiceDelegate} before using the
     * {@link #indexer} to update the search index.
     * 
     * @see IndexerComponent#deleteChildRelationship(ChildAssocRef)
     * @see IndexerComponent#deleteNode(ChildAssocRef)
     */
    public Collection<EntityRef> removeChildren(NodeRef parentRef, QName qname) throws InvalidNodeRefException
    {
        // call delegate
        Collection<EntityRef> entityRefs = nodeServiceDelegate.removeChildren(parentRef, qname);
        // update index
        for (EntityRef ref : entityRefs)
        {
            if (ref instanceof ChildAssocRef)
            {
                ChildAssocRef assoc = (ChildAssocRef) ref;
                if (assoc.isPrimary())
                {
                    // the node will have been deleted as well
                    indexer.deleteNode(assoc);
                }
                else
                {
                    indexer.deleteChildRelationship(assoc);
                }
            }
        }
        return entityRefs;
    }

    /**
     * Delegates to the assigned {@link #nodeServiceDelegate} before using the
     * {@link #indexer} to update the search index.
     * 
     * @see IndexerComponent#updateNode(NodeRef)
     */
    public void setProperties(NodeRef nodeRef, Map<QName, Serializable> properties) throws InvalidNodeRefException
    {
        // call delegate
        nodeServiceDelegate.setProperties(nodeRef, properties);
        // update index
        indexer.updateNode(nodeRef);
    }

    /**
     * Delegates to the assigned {@link #nodeServiceDelegate} before using the
     * {@link #indexer} to update the search index.
     * 
     * @see IndexerComponent#updateNode(NodeRef)
     */
    public void setProperty(NodeRef nodeRef, QName qname, Serializable value) throws InvalidNodeRefException
    {
        // call delegate
        nodeServiceDelegate.setProperty(nodeRef, qname, value);
        // update index
        indexer.updateNode(nodeRef);
    }

    /**
     * Direct delegation to assigned {@link #nodeServiceDelegate}
     */
    public void createAssociation(NodeRef sourceRef, NodeRef targetRef, QName qname) throws InvalidNodeRefException, AssociationExistsException
    {
        nodeServiceDelegate.createAssociation(sourceRef, targetRef, qname);
    }

    /**
     * Direct delegation to assigned {@link #nodeServiceDelegate}
     */
    public void removeAssociation(NodeRef sourceRef, NodeRef targetRef, QName qname) throws InvalidNodeRefException
    {
        nodeServiceDelegate.removeAssociation(sourceRef, targetRef, qname);
    }

    /**
     * Direct delegation to assigned {@link #nodeServiceDelegate}
     */
    public Collection<NodeRef> getAssociationSources(NodeRef targetRef, QName qname) throws InvalidNodeRefException
    {
        return nodeServiceDelegate.getAssociationSources(targetRef, qname);
    }

    /**
     * Direct delegation to assigned {@link #nodeServiceDelegate}
     */
    public Collection<NodeRef> getAssociationTargets(NodeRef sourceRef, QName qname) throws InvalidNodeRefException
    {
        return nodeServiceDelegate.getAssociationTargets(sourceRef, qname);
    }

    /**
     * Direct delegation to assigned {@link #nodeServiceDelegate}
     */
    public Collection<ChildAssocRef> getChildAssocs(NodeRef nodeRef) throws InvalidNodeRefException
    {
        return nodeServiceDelegate.getChildAssocs(nodeRef);
    }

    /**
     * Direct delegation to assigned {@link #nodeServiceDelegate}
     */
    public Path getPath(NodeRef nodeRef) throws InvalidNodeRefException
    {
        return nodeServiceDelegate.getPath(nodeRef);
    }

    /**
     * Direct delegation to assigned {@link #nodeServiceDelegate}
     */
    public Collection<Path> getPaths(NodeRef nodeRef, boolean primaryOnly) throws InvalidNodeRefException
    {
        return nodeServiceDelegate.getPaths(nodeRef, primaryOnly);
    }

    /**
     * Direct delegation to assigned {@link #nodeServiceDelegate}
     */
    public NodeRef getPrimaryParent(NodeRef nodeRef) throws InvalidNodeRefException
    {
        return nodeServiceDelegate.getPrimaryParent(nodeRef);
    }

    /**
     * Direct delegation to assigned {@link #nodeServiceDelegate}
     */
    public Collection<NodeRef> getParents(NodeRef nodeRef) throws InvalidNodeRefException
    {
        return nodeServiceDelegate.getParents(nodeRef);
    }

    /**
     * Direct delegation to assigned {@link #nodeServiceDelegate}
     */
    public Map<QName, Serializable> getProperties(NodeRef nodeRef) throws InvalidNodeRefException
    {
        return nodeServiceDelegate.getProperties(nodeRef);
    }

    /**
     * Direct delegation to assigned {@link #nodeServiceDelegate}
     */
    public Serializable getProperty(NodeRef nodeRef, QName qname) throws InvalidNodeRefException
    {
        return nodeServiceDelegate.getProperty(nodeRef, qname);
    }
}
