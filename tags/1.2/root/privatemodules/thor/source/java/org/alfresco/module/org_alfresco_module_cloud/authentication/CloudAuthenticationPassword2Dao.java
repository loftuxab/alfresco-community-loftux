/*
 * Copyright (C) 2005-2011 Alfresco Software Limited.
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
package org.alfresco.module.org_alfresco_module_cloud.authentication;

import java.io.Serializable;
import java.util.Map;

import net.sf.acegisecurity.GrantedAuthority;
import net.sf.acegisecurity.GrantedAuthorityImpl;
import net.sf.acegisecurity.UserDetails;
import net.sf.acegisecurity.providers.dao.User;
import net.sf.acegisecurity.providers.dao.UsernameNotFoundException;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.datatype.DefaultTypeConverter;
import org.alfresco.service.namespace.QName;
import org.springframework.dao.DataAccessException;

/**
 * Component to provide authentication using native Alfresco authentication
 */
public class CloudAuthenticationPassword2Dao extends CloudAuthenticationDao
{
    public CloudAuthenticationPassword2Dao()
    {
        super();
    }
    
    @Override
    public Object getSalt(UserDetails userDetails)
    {
        if (userDetails == null)
        {
            return null;
        }
        
        NodeRef userNodeRef = getUserOrNull(userDetails.getUsername());
        if (userNodeRef == null)
        {
            return null;
        }
        return nodeService.getProperty(userNodeRef, ContentModel.PROP_SALT);
    }
    
    @Override
    public UserDetails loadUserByUsername(String incomingUserName) throws UsernameNotFoundException, DataAccessException
    {
        NodeRef userRef = getUserOrNull(incomingUserName);
        if (userRef == null)
        {
            throw new UsernameNotFoundException("Could not find user by userName: " + incomingUserName);
        }
        
        Map<QName, Serializable> properties = nodeService.getProperties(userRef);
        String password = DefaultTypeConverter.INSTANCE.convert(String.class, properties.get(ContentModel.PROP_PASSWORD_SHA256));
        
        // Report back the user name as stored on the user
        String userName = DefaultTypeConverter.INSTANCE.convert(String.class, properties.get(ContentModel.PROP_USER_USERNAME));
        
        GrantedAuthority[] gas = new GrantedAuthority[1];
        gas[0] = new GrantedAuthorityImpl("ROLE_AUTHENTICATED");
        
        boolean isAdminAuthority = authorityService.isAdminAuthority(userName);
        
        UserDetails ud = new User(
                userName,
                password,
                getEnabled(userName, properties, isAdminAuthority),
                !getHasExpired(userName, properties, isAdminAuthority),
                !getCredentialsHaveExpired(userName, properties, isAdminAuthority),
                !getLocked(userName, properties, isAdminAuthority),
                gas);
        return ud;
    }
}
