/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.cluster.core;

import java.util.Date;

/**
 * Represents a registered server instance.
 * 
 * @author Matt Ward
 */
public interface RegisteredServerInfo extends ServerInfo
{
    /**
     * When did the server last register itself in the database?
     * 
     * @return Date
     */
    Date getLastRegistered();
}
