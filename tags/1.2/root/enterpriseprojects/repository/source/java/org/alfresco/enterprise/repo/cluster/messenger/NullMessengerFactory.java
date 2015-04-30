/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.cluster.messenger;

import java.io.Serializable;

import org.alfresco.enterprise.repo.cluster.core.ClusterMembershipListener;

/**
 * Factory to produce no-op {@link Messenger messengers}
 * 
 * @author Matt Ward
 * @since 4.2
 */
public class NullMessengerFactory implements MessengerFactory
{
    @Override
    public <T extends Serializable> Messenger<T> createMessenger(String appRegion)
    {
        return new NullMessenger<T>();
    }

    @Override
    public <T extends Serializable> Messenger<T> createMessenger(String appRegion, boolean acceptLocalMessages)
    {
        return new NullMessenger<T>();
    }

    @Override
    public boolean isClusterActive()
    {
        return false;
    }

    @Override
    public void addMembershipListener(ClusterMembershipListener membershipListener)
    {
    }
}
