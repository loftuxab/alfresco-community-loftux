package org.alfresco.repo.node;

import org.alfresco.repo.ref.StoreRef;

/**
 * Thrown when an operation cannot be performed because the <b>store</b> reference
 * no longer exists.
 * 
 * @author Derek Hulley
 */
public class InvalidStoreRefException extends AbstractStoreException
{
    private static final long serialVersionUID = 3258126938479409463L;

    public InvalidStoreRefException(StoreRef storeRef)
    {
        super(storeRef);
    }

    public InvalidStoreRefException(String msg, StoreRef storeRef)
    {
        super(msg, storeRef);
    }
}
