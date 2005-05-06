package org.alfresco.repo.lock.common;

import org.alfresco.repo.lock.LockService;
import org.alfresco.repo.ref.NodeRef;
import org.alfresco.repo.version.policy.OnBeforeCreateVersionPolicy;

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