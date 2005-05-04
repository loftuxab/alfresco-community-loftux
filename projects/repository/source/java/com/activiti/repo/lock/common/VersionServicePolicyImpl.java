package com.activiti.repo.lock.common;

import com.activiti.repo.lock.LockService;
import com.activiti.repo.ref.NodeRef;
import com.activiti.repo.version.policy.OnBeforeCreateVersionPolicy;

/**
 * Version service policy implementations for the lock service
 * 
 * @author Roy Wetherall
 */
public class VersionServicePolicyImpl extends AbstractPolicyImpl implements OnBeforeCreateVersionPolicy
{
    /**
     * Constructor 
     * 
     * @param lockService  the lock service
     */
    public VersionServicePolicyImpl(LockService lockService)
    {
        super(lockService);
    }

    /**
     * onBeforeCreateVersion policy implementation
     * 
     * @param versionableNode  the node being versioned
     */
    public void OnBeforeCreateVersion(NodeRef versionableNode)
    {
        // Check the lock
        checkForLock(versionableNode);
    }
}