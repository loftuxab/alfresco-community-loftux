/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.sync;

import org.alfresco.error.AlfrescoRuntimeException;

/**
 * @author Neil Mc Erlean
 * @since TODO
 */
public class SyncAdminServiceException extends AlfrescoRuntimeException
{
    private static final long serialVersionUID = 1L;
    
    public SyncAdminServiceException(String message) 
    {
        super(message);
    }
    
    public SyncAdminServiceException(String message, Throwable source) 
    {
        super(message, source);
    }
}
