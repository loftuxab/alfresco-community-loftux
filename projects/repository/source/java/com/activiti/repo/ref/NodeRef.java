package com.activiti.repo.ref;

/**
 * Reference to a node store
 * 
 * @author derekh
 */
public class NodeRef
{
    private static final String URI_FILLER = "/";

    private StoreRef storeRef;

    private String id;

    /**
     * @param storeRef
     * @see StoreRef
     * @param id
     *            the unique identifier of the node
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
            return (this.storeRef.equals(that.storeRef) && this.id
                    .equals(that.id));
        } else
        {
            return false;
        }
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