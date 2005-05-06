package org.alfresco.repo.node;

import org.alfresco.repo.ref.StoreRef;

/**
 * Thrown when an operation cannot be performed because the <b>store</b> reference
 * no longer exists.
 * 
 * @author Derek Hulley
 */
public class StoreExistsException extends AbstractStoreException
{
    private static final long serialVersionUID = 3906369320370975030L;

    public StoreExistsException(StoreRef storeRef)
    {
        super(storeRef);
    }

    public StoreExistsException(String msg, StoreRef storeRef)
    {
        super(msg, storeRef);
    }
}
