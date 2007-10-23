/*
 * Copyright (C) 2005-2007 Alfresco Software Limited.
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
 * http://www.alfresco.com/legal/licensing
 */
package org.alfresco.module.knowledgeBase;

import java.util.Set;

import org.alfresco.repo.processor.BaseProcessorExtension;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.security.AuthorityService;

/**
 * Knowledge base script utility methods 
 * 
 * @author Roy Wetherall
 */
public class KbScriptUtil extends BaseProcessorExtension implements KbModel
{
    private AuthorityService authorityService;
    
    public void setAuthorityService(AuthorityService authorityService)
    {
        this.authorityService = authorityService;
    }
    
    public String getUserVisibility(String userName)
    {
        String result = VISIBILITY_TIER_3.getId();
        String currentUser = AuthenticationUtil.getCurrentUserName();
        AuthenticationUtil.setSystemUserAsCurrentUser();
        try
        {        
            Set<String> authroities = this.authorityService.getAuthoritiesForUser(userName);
            if (this.authorityService.isAdminAuthority(currentUser) == true || authroities.contains(GROUP_INTERNAL) == true)
            {
                result = VISIBILITY_INTERNAL.getId();
            }
            else if (authroities.contains(GROUP_TIER_1) == true)
            {
                result = VISIBILITY_TIER_1.getId();
            }
            else if (authroities.contains(GROUP_TIER_2) == true)
            {
                result = VISIBILITY_TIER_2.getId();
            }            
        }
        finally
        {
            AuthenticationUtil.setCurrentUser(currentUser);
        }
        
        return result;
    }
}
