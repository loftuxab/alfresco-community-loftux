package com.activiti.repo.domain.hibernate;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.activiti.repo.domain.ChildAssoc;
import com.activiti.repo.domain.Node;
import com.activiti.repo.domain.NodeAssoc;
import com.activiti.repo.domain.NodeKey;
import com.activiti.repo.domain.Store;
import com.activiti.repo.ref.NodeRef;

/**
 * Simple named node to test out various features
 * 
 * @author Derek Hulley
 * 
 */
public class NodeImpl implements Node
{
    private NodeKey key;
    private String type;
    private Store store;
    private Set<NodeAssoc> sourceNodeAssocs;
    private Set<ChildAssoc> parentAssocs;
    private Map<String, Serializable> properties;
    private NodeRef nodeRef;

    public NodeImpl()
    {
        sourceNodeAssocs = new HashSet<NodeAssoc>(3);
        parentAssocs = new HashSet<ChildAssoc>(3);
        properties = new HashMap<String, Serializable>(5);
    }
    
    public boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        else if (obj == this)
        {
            return true;
        }
        else if (!(obj instanceof Node))
        {
            return false;
        }
        Node that = (Node) obj;
        return (this.getKey().equals(that.getKey()));
    }
    
    public int hashCode()
    {
        return getKey().hashCode();
    }

    public NodeKey getKey() {
		return key;
	}

	public void setKey(NodeKey key) {
		this.key = key;
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

    public Set<NodeAssoc> getSourceNodeAssocs()
    {
        return sourceNodeAssocs;
    }

    /**
     * For Hibernate use
     */
    private void setSourceNodeAssocs(Set<NodeAssoc> sourceNodeAssocs)
    {
        this.sourceNodeAssocs = sourceNodeAssocs;
    }

    public Set<ChildAssoc> getParentAssocs()
    {
        return parentAssocs;
    }

    /**
     * For Hibernate use
     */
    private void setParentAssocs(Set<ChildAssoc> parentAssocs)
    {
        this.parentAssocs = parentAssocs;
    }

    public Map<String, Serializable> getProperties()
    {
        return properties;
    }

    /**
     * For Hibernate use
     */
    private void setProperties(Map<String, Serializable> properties)
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
