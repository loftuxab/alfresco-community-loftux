/*
 * Copyright (C) 2005-2008 Alfresco Software Limited.
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
package org.alfresco.module.org_alfresco_module_dod5015;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.InvalidNodeRefException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;

/**
 * @author Roy Wetherall
 */
public class RecordsManagementPoliciesUtil
{
    /**
     * Get all aspect and node type qualified names
     * 
     * @param nodeRef
     *            the node we are interested in
     * @return Returns a set of qualified names containing the node type and all
     *         the node aspects, or null if the node no longer exists
     */
    public static Set<QName> getTypeAndAspectQNames(final NodeService nodeService, final NodeRef nodeRef)
    {
        return AuthenticationUtil.runAs(new AuthenticationUtil.RunAsWork<Set<QName>>()
        {
            public Set<QName> doWork() throws Exception
            {
                Set<QName> qnames = null;
                try
                {            
                    Set<QName> aspectQNames = nodeService.getAspects(nodeRef);
                    
                    QName typeQName = nodeService.getType(nodeRef);
                    
                    qnames = new HashSet<QName>(aspectQNames.size() + 1);
                    qnames.addAll(aspectQNames);
                    qnames.add(typeQName);
                }
                catch (InvalidNodeRefException e)
                {
                    qnames = Collections.emptySet();
                }
                // done
                return qnames;
            }
        }, AuthenticationUtil.getAdminUserName());   
    }

}
