package org.alfresco.repo.domain.hibernate;

import org.alfresco.repo.domain.RealNode;
import org.alfresco.repo.domain.Store;
import org.alfresco.repo.domain.StoreKey;
import org.alfresco.service.cmr.repository.StoreRef;

/**
 * @author Derek Hulley
 */
public class StoreImpl implements Store
{
	private StoreKey key;
//    private String protocol;
//    private String identifier;
    private RealNode rootNode;
    private transient StoreRef storeRef;

    public StoreKey getKey() {
		return key;
	}

	public synchronized void setKey(StoreKey key) {
		this.key = key;
        this.storeRef = null;
	}

//	public String getProtocol()
//    {
//        return protocol;
//    }
//
//    public void setProtocol(String protocol)
//    {
//        this.protocol = protocol;
//    }
//
//    public String getIdentifier()
//    {
//        return identifier;
//    }
//
//    public synchronized void setIdentifier(String identifier)
//    {
//        this.identifier = identifier;
//        this.storeRef = null;
//    }
//
    public RealNode getRootNode()
    {
        return rootNode;
    }

    public void setRootNode(RealNode rootNode)
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