package com.activiti.repo.domain.hibernate;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.activiti.repo.domain.Node;
import com.activiti.repo.domain.NodeKey;
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
    private NodeKey key;
    private String type;
    private Store store;
    private Set parentAssocs;
    private Map properties;
    private NodeRef nodeRef;

    public NodeKey getKey() {
		return key;
	}

	public void setKey(NodeKey key) {
		this.key = key;
	}

	public NodeImpl()
    {
        parentAssocs = new HashSet(3, 0.75F);
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
        if (nodeRef == null && key != null)
        {
            nodeRef = new NodeRef(getStore().getStoreRef(), getKey().getGuid());
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
