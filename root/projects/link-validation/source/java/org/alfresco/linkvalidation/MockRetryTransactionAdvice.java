/*-----------------------------------------------------------------------------
*  Copyright 2007 Alfresco Inc.
*  
*  This program is free software; you can redistribute it and/or modify
*  it under the terms of the GNU General Public License as published by
*  the Free Software Foundation; either version 2 of the License, or
*  (at your option) any later version.
*  
*  This program is distributed in the hope that it will be useful, but
*  WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
*  or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
*  for more details.
*  
*  You should have received a copy of the GNU General Public License along
*  with this program; if not, write to the Free Software Foundation, Inc.,
*  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  As a special
*  exception to the terms and conditions of version 2.0 of the GPL, you may
*  redistribute this Program in connection with Free/Libre and Open Source
*  Software ("FLOSS") applications as described in Alfresco's FLOSS exception.
*  You should have received a copy of the text describing the FLOSS exception,
*  and it is also available here:   http://www.alfresco.com/legal/licensing
*  
*  
*  Author  Jon Cox  <jcox@alfresco.com>
*  File    MockRetryTransactionAdvice.java
*----------------------------------------------------------------------------*/

package  org.alfresco.linkvalidation;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.log4j.Logger;
import org.hibernate.exception.LockAcquisitionException;
import org.springframework.aop.framework.ReflectiveMethodInvocation;

/**
*   A no-op transaction advice class for testing purposes.
*/
public class MockRetryTransactionAdvice implements MethodInterceptor 
{
    private static Logger log = Logger.getLogger(MockRetryTransactionAdvice.class);
    
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
