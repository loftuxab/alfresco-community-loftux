package com.activiti.repo.node.db;

import com.activiti.repo.domain.ChildAssoc;
import com.activiti.repo.domain.ContainerNode;
import com.activiti.repo.domain.Node;
import com.activiti.repo.domain.RealNode;
import com.activiti.repo.domain.ReferenceNode;
import com.activiti.repo.domain.Store;

/**
 * Service layer accessing persistent <b>node</b> entities directly
 * 
 * @author derekh
 */
public interface NodeDaoService
{
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
     * @return Returns the persisted and filled association
     * @see ChildAssoc
     */
    public ChildAssoc newChildAssoc(ContainerNode parentNode,
            Node childNode,
            boolean isPrimary,
            String name);
    
    /**
     * @param store the store to search in
     * @param guid the store-unique, manually assigned GUID
     * @return Returns the node found in the store, or null if not found
     */
    public Node findNodeInStore(Store store, String guid);
    
    /**
     * @param id the unique id of the node
     * @return Returns the <b>node</b> entity
     */
    public Node getNode(Long id);
    
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
}
