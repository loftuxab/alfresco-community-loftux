/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.sync;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.extensions.surf.util.ParameterCheck;

/**
 * This class holds the basic, top-level configuration information about CloudSync on the current Alfresco instance.
 * 
 * @author Neil Mc Erlean
 */
public class CloudSyncAdmin implements InitializingBean
{
    /**
     * This enum represents the two roles that an Alfresco server instance can play in CloudSync.
     */
    public enum ServerRole { SOURCE, TARGET}
    
    private boolean    enabled;
    private ServerRole serverRole;
    
    public void setEnabledString(String enabledString)
    {
        this.enabled = Boolean.parseBoolean(enabledString.trim());
    }
    
    /**
     * Is the Cloud-Sync feature enabled on this server?
     */
    public boolean isEnabled()
    {
        return this.enabled;
    }
    
    public void setServerRoleString(String serverRoleString)
    {
        this.serverRole = ServerRole.valueOf(serverRoleString.toUpperCase());
    }
    
    /** Gets the role that this Alfresco repository instance plays in CloudSync. */
    public ServerRole getServerRole()
    {
        return this.serverRole;
    }
    
    @Override public void afterPropertiesSet() throws Exception
    {
        ParameterCheck.mandatory("serverRole", serverRole);
    }
}

