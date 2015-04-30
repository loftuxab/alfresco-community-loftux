/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.sync;

/**
 * This exception is thrown when an operation is performed against a node with the assumption that
 * the node is related to the Cloud Sync feature in some way - and yet it is not.
 * 
 * @author Neil Mc Erlean
 * @since TODO
 */
public class NodeNotSyncRelatedException extends SyncAdminServiceException
{
    private static final long serialVersionUID = 1L;
    
    public NodeNotSyncRelatedException(String message) 
    {
        super(message);
    }
    
    public NodeNotSyncRelatedException(String message, Throwable source) 
    {
        super(message, source);
    }
}
