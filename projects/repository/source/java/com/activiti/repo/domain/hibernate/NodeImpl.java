package com.activiti.repo.domain.hibernate;

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
    private Map<String, String> properties;
    private NodeRef nodeRef;

    public NodeImpl()
    {
        sourceNodeAssocs = new HashSet<NodeAssoc>(3);
        parentAssocs = new HashSet<ChildAssoc>(3);
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

    public void setSourceNodeAssocs(Set<NodeAssoc> sourceNodeAssocs)
    {
        this.sourceNodeAssocs = sourceNodeAssocs;
    }

    public Set<ChildAssoc> getParentAssocs()
    {
        return parentAssocs;
    }

    public void setParentAssocs(Set<ChildAssoc> parentAssocs)
    {
        this.parentAssocs = parentAssocs;
    }

    public Map<String, String> getProperties()
    {
        return properties;
    }

    public void setProperties(Map<String, String> properties)
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
