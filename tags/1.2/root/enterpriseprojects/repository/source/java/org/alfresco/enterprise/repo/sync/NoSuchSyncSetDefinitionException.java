/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.sync;

/**
 * This exception is thrown when a {@link SyncSetDefinition} unexpectedly does not exist.
 * 
 * @author Neil Mc Erlean
 * @since TODO
 */
public class NoSuchSyncSetDefinitionException extends SyncAdminServiceException
{
    private static final long serialVersionUID = 1L;
    
    private final String unrecognisedSsdId;
    
    public NoSuchSyncSetDefinitionException(String message, String unrecognisedSsdId) 
    {
        super(message);
        this.unrecognisedSsdId = unrecognisedSsdId;
    }
    
    public NoSuchSyncSetDefinitionException(String message, String unrecognisedSsdId, Throwable source) 
    {
        super(message, source);
        this.unrecognisedSsdId = unrecognisedSsdId;
    }
    
    /**
     * Gets the id for the non-existent {@link SyncSetDefinition}, if available.
     * @return
     */
    public String getUnrecognisedSsdId()
    {
        return this.unrecognisedSsdId;
    }
}
