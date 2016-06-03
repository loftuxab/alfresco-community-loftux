package org.alfresco.service.cmr.repository;

import org.alfresco.api.AlfrescoPublicApi;

/**
 * Store-related exception that keeps a handle to the store reference
 * 
 * @author Derek Hulley
 */
@AlfrescoPublicApi
public abstract class AbstractStoreException extends RuntimeException
{
    private static final long serialVersionUID = 1315634811903555316L;

    private StoreRef storeRef;
    
    public AbstractStoreException(StoreRef storeRef)
    {
        this(null, storeRef, null);
    }

    public AbstractStoreException(String msg, StoreRef storeRef)
    {
        this(msg, storeRef, null);
    }

    public AbstractStoreException(StoreRef storeRef, Throwable e)
    {
        this(null, storeRef, e);
    }

    public AbstractStoreException(String msg, StoreRef storeRef, Throwable e)
    {
        super(msg, e);
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
