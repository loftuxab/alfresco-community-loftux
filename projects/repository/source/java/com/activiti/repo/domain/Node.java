package com.activiti.repo.domain;

import java.util.Map;
import java.util.Set;

import com.activiti.repo.ref.NodeRef;

/**
 * Specific instances of nodes are unique, but may share GUIDs across workspaces.
 * 
 * @author derekh
 */
public interface Node
{
    public static final String QUERY_FIND_NODE_IN_WORKSPACE = "node.FindNodeInWorkspace";
    
    // TODO: Remove this in favour of the Data Dictionary
    public static final String TYPE_REFERENCE = "reference";
    public static final String TYPE_REAL = "real";
    public static final String TYPE_CONTAINER = "container";
    public static final String TYPE_CONTENT = "content";
    
    /**
     * @return Returns the persistence-assigned ID
     */
    public Long getId();

    /**
     * @param id automatically assigned ID
     */
    public void setId(Long id);
    
    /**
     * @return Returns the manually assigned GUID
     */
    public String getGuid();
    
    /**
     * @param manually assigned GUID
     */
    public void setGuid(String id);
    
    public Store getWorkspace();
    
    public void setWorkspace(Store workspace);
    
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
