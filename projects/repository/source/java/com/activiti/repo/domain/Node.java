package com.activiti.repo.domain;

import java.util.Map;
import java.util.Set;

import com.activiti.repo.ref.NodeRef;

/**
 * Specific instances of nodes are unique, but may share GUIDs across stores.
 * 
 * @author Derek Hulley
 */
public interface Node
{
    public static final String QUERY_GET_CHILD_ASSOCS = "node.GetChildAssocs";
    
    // TODO: Remove this in favour of the Data Dictionary
    public static final String TYPE_REFERENCE = "reference";
    public static final String TYPE_REAL = "real";
    public static final String TYPE_CONTAINER = "container";
    public static final String TYPE_CONTENT = "content";
    
    /**
     * @return Returns the unique key for this node
     */
    public NodeKey getKey();

    /**
     * @param key the unique node key
     */
    public void setKey(NodeKey key);
    
    public Store getStore();
    
    public void setStore(Store store);
    
    public String getType();
    
    public void setType(String type);

    /**
     * @return Returns all the regular associations for which this node is a target 
     */
    public Set<NodeAssoc> getSourceNodeAssocs();

    public Set<ChildAssoc> getParentAssocs();

    public Map<String, String> getProperties();

//    public void setProperties(Map<String, String> properties);
    
    /**
     * Convenience method to get the reference to the node
     * 
     * @return Returns the reference to this node
     */
    public NodeRef getNodeRef();
}
