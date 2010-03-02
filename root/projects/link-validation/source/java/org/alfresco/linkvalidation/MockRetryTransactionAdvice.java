/*-----------------------------------------------------------------------------
*  Copyright 2007-2010 Alfresco Software Limited.
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
*  
*  
*  Author  Jon Cox  <jcox@alfresco.com>
*  File    MockRetryTransactionAdvice.java
*----------------------------------------------------------------------------*/

package  org.alfresco.linkvalidation;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.exception.LockAcquisitionException;
import org.springframework.aop.framework.ReflectiveMethodInvocation;

/**
*   A no-op transaction advice class for testing purposes.
*/
public class MockRetryTransactionAdvice implements MethodInterceptor 
{
    private static Log    log = LogFactory.getLog(MockRetryTransactionAdvice.class);
    
    public MockRetryTransactionAdvice()
    {
    }
    
    public Object invoke(MethodInvocation methodInvocation) throws Throwable 
    {
        try
        {
            MethodInvocation clone = ((ReflectiveMethodInvocation)methodInvocation).invocableClone();
            Object result = clone.proceed();
            return result;
        }
        catch (Throwable t)
        {
            log.error("Mock Txn Failed");
            throw t;
        }
    }
}
