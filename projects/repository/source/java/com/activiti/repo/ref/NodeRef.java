package com.activiti.repo.ref;

import java.io.Serializable;

/**
 * Reference to a node store
 * 
 * @author derekh
 */
public class NodeRef implements Serializable
{
    private static final long serialVersionUID = 3834308453517833270L;

    private static final String URI_FILLER = "/";

    private StoreRef storeRef;
    private String guid;
    private Long id;

    /**
     * @param storeRef
     * @see StoreRef
     * @param guid
     *      the manually assigned unique identifier of the node
     * @param id
     *      the generated, unique id of the node
     */
    public NodeRef(StoreRef storeRef, String guid, Long id)
    {
        if (storeRef == null)
        {
            throw new IllegalArgumentException(
                    "Store reference may not be null");
        }
        if (guid == null)
        {
            throw new IllegalArgumentException("Node id may not be null");
        }

        this.storeRef = storeRef;
        this.guid = guid;
        this.id = id;
    }

    public String toString()
    {
        return storeRef.toString() + URI_FILLER + guid;
    }

    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj instanceof NodeRef)
        {
            NodeRef that = (NodeRef) obj;
            return (this.guid.equals(that.guid)
                    && this.storeRef.equals(that.storeRef));
        }
        else
        {
            return false;
        }
    }
    
    /**
     * Hashes on ID alone.  As the number of copies of a particular node will be minimal, this is acceptable
     */
    public int hashCode()
    {
        return guid.hashCode();
    }

    public StoreRef getStoreRef()
    {
        return storeRef;
    }

    public String getGuid()
    {
        return guid;
    }
    
    public Long getId()
    {
        return id;
    }
}