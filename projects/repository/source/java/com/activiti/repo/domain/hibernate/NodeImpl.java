package com.activiti.repo.domain.hibernate;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.activiti.repo.domain.Node;
import com.activiti.repo.domain.Store;
import com.activiti.repo.ref.NodeRef;

/**
 * Simple named node to test out various features
 * 
 * @author derekh
 * 
 */
public class NodeImpl implements Node
{
    private Long id;
    private String guid;
    private String type;
    private Store store;
    private Set parentAssocs;
    private Map properties;
    private NodeRef nodeRef;

    public NodeImpl()
    {
        parentAssocs = new HashSet(3, 0.75F);
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getGuid()
    {
        return guid;
    }

    public synchronized void setGuid(String id)
    {
        this.guid = id;
        this.nodeRef = null;
    }
    
    public String getType()
    {
        return type;
    }

    public synchronized void setType(String type)
    {
        this.type = type;
        this.nodeRef = null;
    }

    public Store getStore()
    {
        return store;
    }

    public synchronized void setStore(Store store)
    {
        this.store = store;
        this.nodeRef = null;
    }

    public Set getParentAssocs()
    {
        return parentAssocs;
    }

    public void setParentAssocs(Set parentAssocs)
    {
        this.parentAssocs = parentAssocs;
    }

    public Map getProperties()
    {
        return properties;
    }

    public void setProperties(Map properties)
    {
        this.properties = properties;
    }

    /**
     * Thread-safe caching of the reference is provided
     */
    public synchronized NodeRef getNodeRef()
    {
        if (nodeRef == null)
        {
            nodeRef = new NodeRef(getStore().getStoreRef(), getGuid(), getId());
        }
        return nodeRef;
    }
    
    /**
     * @see #getNodeRef()
     */
    public String toString()
    {
        return getNodeRef().toString();
    }
}
