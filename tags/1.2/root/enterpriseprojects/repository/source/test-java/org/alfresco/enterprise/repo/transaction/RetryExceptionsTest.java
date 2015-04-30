/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.transaction;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.Test;
import org.springframework.dao.ConcurrencyFailureException;

/**
 * Integration test for {@link RetryExceptions} with {@link RetryingTransactionHelper}.
 *  
 * @author Matt Ward
 */
public class RetryExceptionsTest
{
    @Test
    public void canAugmentCoreListWithEnterpriseClasses()
    {
        List<Class<?>> classes = Arrays.<Class<?>>asList(RetryingTransactionHelper.RETRY_EXCEPTIONS);
        
        // Check that an enterprise-specific exception class is present.
        //assertTrue("Expected exception class not present", classes.contains(RemoteCacheException.class));
        // (not used anymore - TODO: enterprise specific code must go)
        
        // Check that a couple of the core classes are present.
        assertTrue("Expected exception class not present", classes.contains(ConstraintViolationException.class));
        assertTrue("Expected exception class not present", classes.contains(ConcurrencyFailureException.class));
    }
}
