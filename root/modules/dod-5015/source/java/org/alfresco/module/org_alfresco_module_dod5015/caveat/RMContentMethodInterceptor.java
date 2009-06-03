/*
 * Copyright (C) 2005-2009 Alfresco Software Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of 
 * the GPL, you may redistribute this Program in connection with Free/Libre 
 * and Open Source Software ("FLOSS") applications as described in Alfresco's 
 * FLOSS exception.  You should have recieved a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.module.org_alfresco_module_dod5015.caveat;

import java.util.Set;

import org.alfresco.repo.security.permissions.AccessDeniedException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * RM Content Service method interceptor to veto 'access' based on RM caveat config
 * 
 * @author janv
 */
public class RMContentMethodInterceptor extends RMAbstractMethodInterceptor
{
    private static Log logger = LogFactory.getLog(RMContentMethodInterceptor.class);
    
    public Object invoke(MethodInvocation invocation) throws Throwable
    {
        Set<String> constraintNames = caveatConfigImpl.getRMConstraintNames();
        if (constraintNames.size() > 0)
        {
            String methodName = invocation.getMethod().getName();
            
            if (logger.isTraceEnabled())
            {
                logger.trace("Intercepting method " + methodName);
            }
            
            Object[] args = invocation.getArguments();
            
            if (methodName.equals("getReader") || methodName.equals("getWriter"))
            {
                for (int i = 0; i < args.length; i++)
                {
                    Object arg = args[i];
                    
                    if (arg == null)
                    {
                        // No check possible
                    }
                    else if (arg instanceof NodeRef)
                    {
                        NodeRef ref = (NodeRef) arg;
                        
                        // Veto access (equivalent to ACL_NODE)
                        if (ref == null || (! caveatConfigImpl.hasAccess(ref)))
                        {
                            throw new AccessDeniedException(MSG_ACCESS_DENIED);
                        }
                    }
                }
            }
        }
        
        return invocation.proceed();
    }
}
