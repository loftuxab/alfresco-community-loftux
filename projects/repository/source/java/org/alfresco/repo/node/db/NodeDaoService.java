package org.alfresco.repo.node.db;

import java.util.Collection;

import org.alfresco.repo.domain.ChildAssoc;
import org.alfresco.repo.domain.Node;
import org.alfresco.repo.domain.NodeAssoc;
import org.alfresco.repo.domain.Store;
import org.alfresco.service.cmr.dictionary.InvalidTypeException;
import org.alfresco.service.namespace.QName;

/**
 * Service layer accessing persistent <b>node</b> entities directly
 * 
 * @author Derek Hulley
 */
public interface NodeDaoService
{
    /**
     * Evicts the persistent entity from the first level cache. 
     * <p>
     * Use this method to ensure that the cache size doesn't grow too much
     * 
     * @param node an entity no longer required
     */
    public void evict(Node node);
    
    /**
     * Evicts the persistent entity from the first level cache.
     * <p>
     * Use this method to ensure that the cache size doesn't grow too much
     * 
     * @param assoc an entity no longer required
     */
    public void evict(ChildAssoc assoc);
    
    /**
     * Creates a unique store for the given protocol and identifier combination
     * 
     * @param protocol a protocol, e.g. {@link org.alfresco.service.cmr.repository.StoreRef#PROTOCOL_WORKSPACE}
     * @param identifier a protocol-specific identifier
     * @return Returns the new persistent entity
     */
    public Store createStore(String protocol, String identifier);
    
    /**
     * @param protocol the protocol that the store serves
     * @param identifier the protocol-specific identifer
     * @return Returns a store with the given values or null if one doesn't exist
     */
    public Store getStore(String protocol, String identifier);

    /**
     * @param store the store to which the node must belong
     * @param nodeTypeQName the type of the node
     * @return Returns a new node of the given type and attached to the store
     * @throws InvalidTypeException if the node type is invalid or if the node type
     *      is not a valid real node
     */
    public Node newNode(Store store, QName nodeTypeQName) throws InvalidTypeException;
    
    /**
     * @param protocol the store protocol
     * @param identifier the store identifier for the given protocol
     * @param id the store-specific node identifier
     * @return Returns the <b>node</b> entity
     */
    public Node getNode(String protocol, String identifier, String id);
    
    /**
     * Deletes the node instance, taking care of any cascades that are required over
     * and above those provided by the persistence mechanism.
     * <p>
     * A caller must able to delete the node using this method and not have to follow
     * up with any other ancillary deletes
     * 
     * @param node the entity to delete
     */
    public void deleteNode(Node node);
    
    /**
     * @return Returns the persisted and filled association
     * @see ChildAssoc
     */
    public ChildAssoc newChildAssoc(
            Node parentNode,
            Node childNode,
            boolean isPrimary,
            QName assocTypeQName,
            QName qname);
    
    /**
     * @param assoc the child association to remove
     */
    public void deleteChildAssoc(ChildAssoc assoc);
    
    /**
     * Finds the association between the node's primary parent and the node itself
     * 
     * @param node the child node
     * @return Returns the primary <code>ChildAssoc</code> instance where the given node is the child.
     *      The return value could be null for a root node - but ONLY a root node
     */
    public ChildAssoc getPrimaryParentAssoc(Node node);
    
    /**
     * @return Returns the persisted and filled association
     * @see NodeAssoc
     */
    public NodeAssoc newNodeAssoc(
            Node sourceNode,
            Node targetNode,
            QName assocTypeQName);
    
    /**
     * @return Returns the node association or null if not found
     */
    public NodeAssoc getNodeAssoc(
            Node sourceNode,
            Node targetNode,
            QName assocTypeQName);
    
    /**
     * @return Returns the target nodes for the association
     */
    public Collection<Node> getNodeAssocTargets(Node sourceNode, QName assocTypeQName);
    
    /**
     * @return Returns the source nodes for the association
     */
    public Collection<Node> getNodeAssocSources(Node targetNode, QName assocTypeQName);
    
    /**
     * @param assoc the node association to remove
     */
    public void deleteNodeAssoc(NodeAssoc assoc);
}
