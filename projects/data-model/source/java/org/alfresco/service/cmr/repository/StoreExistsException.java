package org.alfresco.service.cmr.repository;

import org.alfresco.api.AlfrescoPublicApi;

/**
 * Thrown when an operation cannot be performed because the <b>store</b> reference
 * no longer exists.
 * 
 * @author Derek Hulley
 */
@AlfrescoPublicApi
public class StoreExistsException extends AbstractStoreException
{
    private static final long serialVersionUID = 3906369320370975030L;

    public StoreExistsException(StoreRef storeRef, Throwable e)
    {
        super(storeRef, e);
    }
}
