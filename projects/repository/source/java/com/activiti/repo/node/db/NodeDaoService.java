package com.activiti.repo.node.db;

import java.util.Collection;

import com.activiti.repo.domain.ChildAssoc;
import com.activiti.repo.domain.ContainerNode;
import com.activiti.repo.domain.Node;
import com.activiti.repo.domain.NodeAssoc;
import com.activiti.repo.domain.RealNode;
import com.activiti.repo.domain.ReferenceNode;
import com.activiti.repo.domain.Store;

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
     * @param store the store to which the node must belong
     * @param referencedPath the path to another, possibly non-existent, node in the
     *      same store
     * @return Returns a new reference node for the given reference and store
     */
    public ReferenceNode newReferenceNode(Store store, String referencedPath);
    
    /**
     * @param store the store to which the node must belong
     * @param type the type of the node
     * @return Returns a new real node of the given type and attached to the store
     */
    public RealNode newRealNode(Store store, String type);
    
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
     * @param assocNamespaceUri TODO
     * @return Returns the persisted and filled association
     * @see ChildAssoc
     */
    public ChildAssoc newChildAssoc(ContainerNode parentNode,
            Node childNode,
            boolean isPrimary,
            String assocNamespaceUri,
            String assocName);
    
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
    public NodeAssoc newNodeAssoc(RealNode sourceNode,
            Node targetNode,
            String assocName);
    
    /**
     * @return Returns the node association or null if not found
     */
    public NodeAssoc getNodeAssoc(RealNode sourceNode,
            Node targetNode,
            String assocName);
    
    /**
     * @return Returns the target nodes for the association
     */
    public Collection<Node> getNodeAssocTargets(RealNode sourceNode, String assocName);
    
    /**
     * @return Returns the source nodes for the association
     */
    public Collection<RealNode> getNodeAssocSources(Node targetNode, String assocName);
    
    /**
     * @param assoc the node association to remove
     */
    public void deleteNodeAssoc(NodeAssoc assoc);
}
