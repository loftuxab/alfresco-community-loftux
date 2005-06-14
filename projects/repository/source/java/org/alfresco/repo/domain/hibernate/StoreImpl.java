package org.alfresco.repo.domain.hibernate;

import org.alfresco.repo.domain.Node;
import org.alfresco.repo.domain.Store;
import org.alfresco.repo.domain.StoreKey;
import org.alfresco.service.cmr.repository.StoreRef;

/**
 * Hibernate-specific implementation of the domain entity <b>store</b>.
 * 
 * @author Derek Hulley
 */
public class StoreImpl implements Store
{
	private StoreKey key;
    private Node rootNode;
    private transient StoreRef storeRef;

    public StoreKey getKey() {
		return key;
	}

	public synchronized void setKey(StoreKey key) {
		this.key = key;
        this.storeRef = null;
	}

    public Node getRootNode()
    {
        return rootNode;
    }

    public void setRootNode(Node rootNode)
    {
        this.rootNode = rootNode;
    }
    
    /**
     * Lazily constructs <code>StoreRef</code> instance referencing this entity
     */
    public synchronized StoreRef getStoreRef()
    {
        if (storeRef == null && key != null)
        {
            storeRef = new StoreRef(key.getProtocol(), key.getIdentifier());
        }
        return storeRef;
    }
    
    /**
     * @see #getStoreRef()()
     */
    public String toString()
    {
        return getStoreRef().toString();
    }
}