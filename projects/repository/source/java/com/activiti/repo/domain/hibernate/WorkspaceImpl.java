package com.activiti.repo.domain.hibernate;

import com.activiti.repo.domain.RealNode;
import com.activiti.repo.domain.Workspace;
import com.activiti.repo.ref.StoreRef;

/**
 * @author derekh
 */
public class WorkspaceImpl implements Workspace
{
    private Long id;
    private String protocol;
    private String identifier;
    private RealNode rootNode;
    private transient StoreRef storeRef;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getProtocol()
    {
        return protocol;
    }

    public void setProtocol(String protocol)
    {
        this.protocol = protocol;
    }

    public String getIdentifier()
    {
        return identifier;
    }

    public synchronized void setIdentifier(String identifier)
    {
        this.identifier = identifier;
        this.storeRef = null;
    }

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
        if (storeRef == null)
        {
            storeRef = new StoreRef(protocol, identifier);
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