/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.sync.transport;

import org.alfresco.service.cmr.remoteticket.RemoteSystemUnavailableException;

/**
 * Exception thrown when the Cloud declines to accept a sync push.
 * 
 * @author Nick Burch
 * @since TODO
 */
public class CloudSyncDeclinedException extends RemoteSystemUnavailableException
{
    private static final long serialVersionUID = -4351133492423721556L;

    public CloudSyncDeclinedException(String message)
    {
        super(message);
    }
}