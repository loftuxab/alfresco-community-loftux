package org.alfresco.service.cmr.repository;

import org.alfresco.api.AlfrescoPublicApi;

/**
 * Thrown when an operation cannot be performed because the <b>store</b> reference
 * no longer exists.
 * 
 * @author Derek Hulley
 */
@AlfrescoPublicApi
public class InvalidStoreRefException extends AbstractStoreException
{
    private static final long serialVersionUID = 3258126938479409463L;

    public InvalidStoreRefException(StoreRef storeRef)
    {
        this("Invalid store: " + storeRef, storeRef);
    }

    public InvalidStoreRefException(String msg, StoreRef storeRef)
    {
        super(msg, storeRef);
    }
}
