package org.alfresco.repo.node;

import org.alfresco.repo.ref.StoreRef;

/**
 * Store-related exception that keeps a handle to the store reference
 * 
 * @author Derek Hulley
 */
public abstract class AbstractStoreException extends RuntimeException
{
    private StoreRef storeRef;
    
    public AbstractStoreException(StoreRef storeRef)
    {
        this(null, storeRef);
    }

    public AbstractStoreException(String msg, StoreRef storeRef)
    {
        super(msg);
        this.storeRef = storeRef;
    }

    /**
     * @return Returns the offending store reference
     */
    public StoreRef getStoreRef()
    {
        return storeRef;
    }
}
