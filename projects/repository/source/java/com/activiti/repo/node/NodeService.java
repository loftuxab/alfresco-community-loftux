package com.activiti.repo.node;

import java.util.Map;

import com.activiti.repo.ref.NodeRef;

/**
 * Interface for public and internal <b>node</b> operations
 * 
 * @author derekh
 */
public interface NodeService
{
    /**
     * @see #createNode(NodeRef, String, String, Map)
     */
    NodeRef createNode(NodeRef parentRef, String name, String nodeType);
    
    /**
     * Creates a new, non-abstract, real node as a primary child of the given parent node.
     * 
     * @param parentRef the parent node
     * @param name the name of the child association between the parent and the new child
     * @param nodeType a predefined node type
     * @param properties optional map of properties to assign to the node
     * @return Returns a reference to the newly created node
     */
    NodeRef createNode(NodeRef parentRef, String name, String nodeType, Map properties);
    
    /**
     * Deletes the given node.
     * 
     * @param nodeRef reference to a node within a store
     */
    void deleteNode(NodeRef nodeRef);
}
