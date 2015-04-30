/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.enterprise.repo.cluster.core;

import org.alfresco.util.LogUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hazelcast.core.MembershipEvent;
import com.hazelcast.core.MembershipListener;

/**
 * Clears or "drops" all of the invalidating caches
 * upon a change in cluster membership.
 * <p>
 * This is required to alleviate problems such as caches drifting
 * out of sync during a split-brain scenario. For example, if a member
 * drops out of the cluster temporarily, the caches may receive updates,
 * but other members are not notified of the changes with
 * invalidation messages - as they should be. Upon leaving, the members
 * will drop their caches to reduce sync problems and again on the
 * member rejoining. 
 * 
 * @author Matt Ward
 */
public class MembershipChangeCacheDropper implements MembershipListener
{
    private static final Log log = LogFactory.getLog(MembershipChangeCacheDropper.class);
    private static final String MSG_DROP_CACHE_JOINED = "system.cluster.drop_invalidating_caches.member_joined";
    private static final String MSG_DROP_CACHE_LEFT = "system.cluster.drop_invalidating_caches.member_left";
    private static final String MSG_DROP_CACHE_DISABLED = "system.cluster.drop_invalidating_caches.disabled";
    
    private boolean enabled;
    
    @Override
    public void memberAdded(MembershipEvent event)
    {
        if (!enabled)
        {
            if (log.isDebugEnabled())
            {
                LogUtil.debug(log, MSG_DROP_CACHE_DISABLED);
            }
            return;
        }
        
        if (log.isDebugEnabled())
        {
            LogUtil.debug(log, MSG_DROP_CACHE_JOINED);
        }
        dropCaches();
    }

    @Override
    public void memberRemoved(MembershipEvent event)
    {
        if (!enabled)
        {
            if (log.isDebugEnabled())
            {
                LogUtil.debug(log, MSG_DROP_CACHE_DISABLED);
            }
            return;
        }
        
        if (log.isDebugEnabled())
        {
            LogUtil.debug(log, MSG_DROP_CACHE_LEFT);
        }
        dropCaches();
    }

    private void dropCaches()
    {
        ClusteredObjectProxyFactory.dropInvalidatingCaches();
    }

    /**
     * Sets whether cache dropping upon membership change is enabled or disabled.
     * 
     * @param enabled   true to enable cache dropping, false otherwise.
     */
    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }
}
