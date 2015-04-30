/*
 * Copyright 2005-2010 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.management;

import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.service.cmr.security.AuthorityService;

/**
 * An implementation of the {@link AuthorityMBean} interface exposing basic information about users and groups.
 * 
 * @author dward
 */
public class Authority extends MBeanSupport implements AuthorityMBean
{
    private AuthorityService authorityService;

    public void setAuthorityService(AuthorityService authorityService)
    {
        this.authorityService = authorityService;
    }

    @Override
    public int getNumberOfGroups()
    {
        return doWork(new RetryingTransactionCallback<Integer>()
        {
            public Integer execute() throws Throwable
            {
                return (int) authorityService.countGroups();
            }
        }, true);
    }

    @Override
    public int getNumberOfUsers()
    {
        return doWork(new RetryingTransactionCallback<Integer>()
        {
            public Integer execute() throws Throwable
            {
                return (int) authorityService.countUsers();
            }
        }, true);
    }
}
