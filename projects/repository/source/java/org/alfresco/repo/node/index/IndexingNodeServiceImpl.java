package org.alfresco.repo.node.index;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.repo.dictionary.ClassRef;
import org.alfresco.repo.node.AbstractNodeServiceImpl;
import org.alfresco.repo.node.AssociationExistsException;
import org.alfresco.repo.node.InvalidAspectException;
import org.alfresco.repo.node.InvalidNodeRefException;
import org.alfresco.repo.node.InvalidStoreRefException;
import org.alfresco.repo.node.NodeService;
import org.alfresco.repo.node.PropertyException;
import org.alfresco.repo.node.StoreExistsException;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.ref.ChildAssocRef;
import org.alfresco.repo.ref.EntityRef;
import org.alfresco.repo.ref.NodeAssocRef;
import org.alfresco.repo.ref.NodeRef;
import org.alfresco.repo.ref.Path;
import org.alfresco.repo.ref.QName;
import org.alfresco.repo.ref.StoreRef;
import org.alfresco.repo.ref.qname.QNamePattern;
import org.alfresco.repo.search.Indexer;
import org.alfresco.repo.search.IndexerComponent;

/**
 * A lightweight <code>NodeService</code> that delegates the work to a
 * <i>proper</i> <code>NodeService</code>, but also ensures that the
 * required calls are made to the {@link org.alfresco.repo.search.Indexer indexer}.
 * <p>
 * The use of a delegate to perform all the <b>node</b> manipulation means that
 * implementations of the stores can be swapped in and out but still get indexed.
 * 
 * @author Derek Hulley
 */
public class IndexingNodeServiceImpl extends AbstractNodeServiceImpl
{
    private NodeService nodeServiceDelegate;
    private Indexer indexer;

    public IndexingNodeServiceImpl(
			PolicyComponent policyComponent,
			NodeService nodeServiceDelegate, 
			Indexer indexer)
    {
		super(policyComponent);
		
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
            QName assocTypeQName,
            QName assocQName,
            QName nodeTypeQName) throws InvalidNodeRefException
    {
        return this.createNode(parentRef, null, assocQName, nodeTypeQName, null);
    }

    /**
     * Delegates to the assigned {@link #nodeServiceDelegate} before using the
     * {@link #indexer} to update the search index.
     * 
     * @see IndexerComponent#createNode(ChildAssocRef)
     */
    public ChildAssocRef createNode(NodeRef parentRef,
            QName assocTypeQName,
            QName assocQName,
            QName nodeTypeQName,
            Map<QName, Serializable> properties) throws InvalidNodeRefException
    {
        // call delegate
        ChildAssocRef assocRef = nodeServiceDelegate.createNode(
                parentRef,
                null,
                assocQName,
                nodeTypeQName,
                properties);
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
    public NodeAssocRef createAssociation(NodeRef sourceRef, NodeRef targetRef, QName assocTypeQName) throws InvalidNodeRefException, AssociationExistsException
    {
        return nodeServiceDelegate.createAssociation(sourceRef, targetRef, assocTypeQName);
    }

    /**
     * Direct delegation to assigned {@link #nodeServiceDelegate}
     */
    public void removeAssociation(NodeRef sourceRef, NodeRef targetRef, QName assocTypeQName) throws InvalidNodeRefException
    {
        nodeServiceDelegate.removeAssociation(sourceRef, targetRef, assocTypeQName);
    }

    /**
     * Direct delegation to assigned {@link #nodeServiceDelegate}
     */
    public List<NodeAssocRef> getSourceAssocs(NodeRef targetRef, QNamePattern qnamePattern) throws InvalidNodeRefException
    {
        return nodeServiceDelegate.getSourceAssocs(targetRef, qnamePattern);
    }

    /**
     * Direct delegation to assigned {@link #nodeServiceDelegate}
     */
    public List<NodeAssocRef> getTargetAssocs(NodeRef sourceRef, QNamePattern qnamePattern) throws InvalidNodeRefException
    {
        return nodeServiceDelegate.getTargetAssocs(sourceRef, qnamePattern);
    }

    /**
     * Direct delegation to assigned {@link #nodeServiceDelegate}
     */
    public List<ChildAssocRef> getChildAssocs(NodeRef nodeRef, QNamePattern qnamePattern) throws InvalidNodeRefException
    {
        return nodeServiceDelegate.getChildAssocs(nodeRef, qnamePattern);
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
    public ChildAssocRef getPrimaryParent(NodeRef nodeRef) throws InvalidNodeRefException
    {
        return nodeServiceDelegate.getPrimaryParent(nodeRef);
    }

    /**
     * Direct delegation to assigned {@link #nodeServiceDelegate}
     */
    public List<ChildAssocRef> getParentAssocs(NodeRef nodeRef, QNamePattern qnamePattern) throws InvalidNodeRefException
    {
        return nodeServiceDelegate.getParentAssocs(nodeRef, qnamePattern);
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
