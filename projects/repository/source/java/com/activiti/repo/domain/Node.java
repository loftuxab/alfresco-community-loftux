package com.activiti.repo.domain;

import java.util.Map;
import java.util.Set;

import com.activiti.repo.ref.NodeRef;

/**
 * Specific instances of nodes are unique, but may share GUIDs across stores.
 * 
 * @author derekh
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

    public Set getParentAssocs();

    public void setParentAssocs(Set parentAssocs);

    public Map getProperties();

    public void setProperties(Map properties);
    
    /**
     * Convenience method to get the reference to the node
     * 
     * @return Returns the reference to this node
     */
    public NodeRef getNodeRef();
}
