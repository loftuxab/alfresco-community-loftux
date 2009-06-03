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
import java.util.List;
import java.util.Set;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.repo.security.permissions.AccessDeniedException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * RM SearchService method interceptor to veto 'access' based on RM caveat config
 * 
 * @author janv
 */
public class RMSearchMethodInterceptor extends RMAbstractMethodInterceptor
{
    private static Log logger = LogFactory.getLog(RMSearchMethodInterceptor.class);
    
    @SuppressWarnings("unchecked")
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
            
            if (methodName.equals("query"))
            {
                // filter after invocation
            }
            else if (methodName.equals("selectNodes"))
            {
                // filter after invocation
            }
            else if (methodName.equals("contains") || methodName.equals("selectProperties") || methodName.equals("like"))
            {
                Object[] args = invocation.getArguments();
                
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
            else
            {
                // belts-and-braces
                throw new AlfrescoRuntimeException("RMSearchMethodInterceptor: unexpected SearchService method: "+methodName);
            }
            
            // Make the call
            Object ret = invocation.proceed();
            
            if (methodName.equals("query"))
            {
                // Filter access (equivalent to AFTER_ACL_NODE)
                ResultSet rawResultSet = (ResultSet)ret;
                return getValue(rawResultSet);
            }
            else if (methodName.equals("selectNodes"))
            {
                // Filter access (equivalent to AFTER_ACL_NODE)
                List<NodeRef> rawNodeRefs = (List<NodeRef>)ret;
                List<NodeRef> outboundNodeRefs = new ArrayList<NodeRef>(rawNodeRefs.size());
                
                for (NodeRef rawNodeRef : rawNodeRefs)
                {
                    NodeRef outbound = getValue(rawNodeRef);
                    if (outbound != null)
                    {
                        outboundNodeRefs.add(outbound);
                    }
                }
                return outboundNodeRefs;
            }
        }
        
        return invocation.proceed();
    }
    
    private NodeRef getValue(NodeRef nodeRef)
    {
        if (nodeRef == null || (! caveatConfigImpl.hasAccess(nodeRef)))
        {
            return null;
        }
        
        return nodeRef;
    }
    
    private ResultSet getValue(ResultSet resultSet)
    {
        if (resultSet == null)
        {
            return null;
        }
        
        return new RMFilteringResultSet(resultSet, caveatConfigImpl);
    }
}
