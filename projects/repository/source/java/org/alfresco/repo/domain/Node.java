package org.alfresco.repo.domain;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

/**
 * Specific instances of nodes are unique, but may share GUIDs across stores.
 * 
 * @author Derek Hulley
 */
public interface Node
{
    public static final String QUERY_GET_CHILD_ASSOCS = "node.GetChildAssocs";
    
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
    
    public QName getTypeQName();
    
    public void setTypeQName(QName qname);
    
    public Set<QName> getAspects();
    
    /**
     * @return Returns all the regular associations for which this node is a target 
     */
    public Set<NodeAssoc> getSourceNodeAssocs();

    /**
     * @return Returns all the regular associations for which this node is a source 
     */
    public Set<NodeAssoc> getTargetNodeAssocs();

    public Set<ChildAssoc> getChildAssocs();

    public Set<ChildAssoc> getParentAssocs();

    public Map<String, Serializable> getProperties();

    /**
     * Convenience method to get the reference to the node
     * 
     * @return Returns the reference to this node
     */
    public NodeRef getNodeRef();
}
