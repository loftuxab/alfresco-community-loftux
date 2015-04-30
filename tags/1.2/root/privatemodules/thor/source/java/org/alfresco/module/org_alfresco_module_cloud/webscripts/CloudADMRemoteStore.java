/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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
 */
package org.alfresco.module.org_alfresco_module_cloud.webscripts;

import org.alfresco.module.org_alfresco_module_cloud.CloudModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.web.scripts.bean.ADMRemoteStore;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.security.PersonService;

/**
 * Cloud specific override of ADMRemoteStore. To deal with the genius idea of a Network
 * Administrator user who isn't really an admin at all from a permissions perspective and
 * requires manual hand-holding through content writes/delete in the remote config store.
 * 
 * @author Kevin Roast
 * @since 4.2
 */
public class CloudADMRemoteStore extends ADMRemoteStore
{
    private PersonService personService;
    
    /**
     * @param personService     the PersonService to set
     */
    public void setPersonService(PersonService personService)
    {
        this.personService = personService;
    }
    
    @Override
    protected String getPathRunAsUser(String path)
    {
        String user = super.getPathRunAsUser(path);
        if (!user.equals(AuthenticationUtil.getAdminUserName()))
        {
            NodeRef person = personService.getPersonOrNull(user);
            if (person != null && unprotNodeService.hasAspect(person, CloudModel.ASPECT_NETWORK_ADMIN))
            {
                // found a user who is a "network admin" - allow them permissions to perform a write
                user = AuthenticationUtil.getSystemUserName();
            }
        }
        return user;
    }
}