/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.cluster.messenger;

import java.io.Serializable;

import org.alfresco.enterprise.repo.cluster.core.ClusterMembershipListener;

/**
 * Factory class responsible for creating instances of {@link Messenger} class.
 * 
 * @author Matt Ward
 */
public interface MessengerFactory
{
    /** A catch-all for unknown application regions. */
    public static final String APP_REGION_DEFAULT = "DEFAULT";
    
    <T extends Serializable> Messenger<T> createMessenger(String appRegion);
    
    <T extends Serializable> Messenger<T> createMessenger(String appRegion, boolean acceptLocalMessages);
    
    boolean isClusterActive();
    
    void addMembershipListener(ClusterMembershipListener membershipListener);
}
