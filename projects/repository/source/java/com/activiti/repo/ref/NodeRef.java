package com.activiti.repo.ref;

import java.io.Serializable;

/**
 * Reference to a node store
 * 
 * @author Derek Hulley
 */
public class NodeRef implements Serializable
{
    private static final long serialVersionUID = 3834308453517833270L;

    private static final String URI_FILLER = "/";

    private StoreRef storeRef;
    private String id;

    /**
     * @param storeRef
     * @see StoreRef
     * @param id
     *      the manually assigned identifier of the node
     */
    public NodeRef(StoreRef storeRef, String id)
    {
        if (storeRef == null)
        {
            throw new IllegalArgumentException(
                    "Store reference may not be null");
        }
        if (id == null)
        {
            throw new IllegalArgumentException("Node id may not be null");
        }

        this.storeRef = storeRef;
        this.id = id;
    }

    public String toString()
    {
        return storeRef.toString() + URI_FILLER + id;
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
            return (this.id.equals(that.id)
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
        return id.hashCode();
    }

    public StoreRef getStoreRef()
    {
        return storeRef;
    }

    public String getId()
    {
        return id;
    }
}