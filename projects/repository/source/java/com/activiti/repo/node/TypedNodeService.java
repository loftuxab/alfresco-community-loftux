package com.activiti.repo.node;

import com.activiti.repo.domain.ChildAssoc;
import com.activiti.repo.domain.ContainerNode;
import com.activiti.repo.domain.Node;
import com.activiti.repo.domain.RealNode;
import com.activiti.repo.domain.ReferenceNode;
import com.activiti.repo.domain.Store;

/**
 * Service layer interface for entity-aware operations
 * 
 * @author derekh
 */
public interface TypedNodeService
{
    /**
     * @param workspace the workspace to which the node must belong
     * @param referencedPath the path to another, possibly non-existent, node in the
     *      same workspace
     * @return Returns a new reference node for the given reference and workspace
     */
    public ReferenceNode newReferenceNode(Store workspace, String referencedPath);
    
    /**
     * @param workspace the workspace to which the node must belong
     * @param type the type of the node
     * @return Returns a new real node of the given type and attached to the workspace
     */
    public RealNode newRealNode(Store workspace, String type);
    
    /**
     * @return Returns the persisted and filled association
     * @see ChildAssoc
     */
    public ChildAssoc newChildAssoc(ContainerNode parentNode,
            Node childNode,
            boolean isPrimary,
            String name);
    
    /**
     * 
     * @param workspace the workspace to search in
     * @param id the unique id of the node
     * @return
     */
    public Node findNodeInWorkspace(Store workspace, String id);
}
