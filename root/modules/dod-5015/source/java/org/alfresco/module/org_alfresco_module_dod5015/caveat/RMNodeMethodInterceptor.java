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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.alfresco.repo.security.permissions.AccessDeniedException;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * RM NodeService method interceptor to veto 'access' based on RM caveat config
 * 
 * @author janv
 */
public class RMNodeMethodInterceptor extends RMAbstractMethodInterceptor
{
    private static Log logger = LogFactory.getLog(RMNodeMethodInterceptor.class);
    
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
            
            if (methodName.equals("exists") || methodName.equals("getType"))
            {
                // consistent with public-services-security-context.xml
                return invocation.proceed();
            }
            
            Object[] args = invocation.getArguments();
            // Convert each of the arguments to the underlying (full) reference.
            for (int i = 0; i < args.length; i++)
            {
                Object arg = args[i];
                
                if (arg == null)
                {
                    // No check possible
                }
                else if (checkValue(arg) == null)
                {
                    // Check access (equivalent to ACL_NODE, ACL_PARENT, ...)
                    throw new AccessDeniedException(MSG_ACCESS_DENIED);
                }
            }
            
            // Make the call
            Object ret = invocation.proceed();
            
            // Filter access
            return checkValue(ret);
        }
        
        return invocation.proceed();
    }
    
    @SuppressWarnings("unchecked")
    private Object checkValue(Object value)
    {
        if (value == null)
        {
            return null;
        }
        else if (value instanceof StoreRef)
        {
            return value;
        }
        else if (value instanceof Collection)
        {
            return checkValues((Collection<Object>)value);
        }
        else if (value instanceof NodeRef)
        {
            NodeRef ref = (NodeRef) value;
            
            // Check access
            if (ref == null || (! caveatConfigImpl.hasAccess(ref)))
            {
                return null;
            }
            
        }
        else if (value instanceof ChildAssociationRef)
        {
            ChildAssociationRef ref = (ChildAssociationRef)value;
            
            // Check access
            if (ref == null || (! caveatConfigImpl.hasAccess(ref.getParentRef())) || (! caveatConfigImpl.hasAccess(ref.getChildRef())))
            {
                return null;
            }
        }
        else if (value instanceof AssociationRef)
        {
            AssociationRef ref = (AssociationRef)value;
            
            // Check access
            if (ref == null || (! caveatConfigImpl.hasAccess(ref.getSourceRef())) || (! caveatConfigImpl.hasAccess(ref.getTargetRef())))
            {
                return null;
            }
        }
        
        return value;
    }
    
    private Collection<Object> checkValues(Collection<Object> rawValues)
    {
        /*
         * Return types can be Lists or Sets, so cater for both.
         */
        final Collection<Object> convertedValues;
        if (rawValues instanceof List)
        {
            convertedValues = new ArrayList<Object>(rawValues.size());
        }
        else if (rawValues instanceof Set)
        {
            convertedValues = new HashSet<Object>(rawValues.size(), 1.0F);
        }
        else
        {
            throw new IllegalArgumentException("Interceptor can only handle List and Set return types.");
        }
        
        for (Object rawValue : rawValues)
        {
            Object convertedValue = checkValue(rawValue);
            
            if ((convertedValue != null) || (rawValue == null))
            {
                convertedValues.add(convertedValue);
            }
        }
        
        return convertedValues;
    }
}
