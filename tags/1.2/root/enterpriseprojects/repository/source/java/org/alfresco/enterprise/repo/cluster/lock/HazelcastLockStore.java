/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.cluster.lock;

import java.util.Collections;
import java.util.Set;

import org.alfresco.repo.lock.mem.AbstractLockStore;
import org.alfresco.repo.lock.mem.LockState;
import org.alfresco.repo.lock.mem.LockStore;
import org.alfresco.service.cmr.repository.NodeRef;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hazelcast.core.IMap;

/**
 * Hazelcast {@link IMap} based {@link LockStore} that therefore supports clustering.
 * 
 * @author Matt Ward
 */
public class HazelcastLockStore extends AbstractLockStore<IMap<NodeRef, LockState>>
{
    private static final Log logger = LogFactory.getLog(HazelcastLockStore.class);
    
    /**
     * Constructor.
     * 
     * @param map
     */
    public HazelcastLockStore(IMap<NodeRef, LockState> map)
    {
        super(map);
    }

    @Override
    public Set<NodeRef> getNodes()
    {
        try
        {
            return super.getNodes();
        }
        catch (IllegalStateException e)
        {
            logger.warn("Cluster inactive, but called getNodes()");
            return Collections.emptySet();
        }
    }
    
    @Override
    public LockState get(NodeRef nodeRef)
    {
        try
        {
            return super.get(nodeRef);
        }
        catch (IllegalStateException e)
        {
            logger.warn("Cluster inactive, but called get(NodeRef) with node-ref: " + nodeRef);
            return null;
        }
    }

    
    @Override
    public void set(NodeRef nodeRef, LockState lockState)
    {
        try
        {
            super.set(nodeRef, lockState);
        }
        catch(IllegalStateException e)
        {
            logger.warn("Cluster inactive, but called set("+nodeRef+", "+lockState+")");
        }
    }


    @Override
    public void clear()
    {
        try
        {
            super.clear();
        }
        catch(IllegalStateException e)
        {
            logger.warn("Cluster inactive, but called clear()");
        }
    }
}
