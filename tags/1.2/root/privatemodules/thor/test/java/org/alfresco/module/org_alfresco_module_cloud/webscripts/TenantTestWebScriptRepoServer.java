/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
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

import javax.servlet.ServletContext;

import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.tenant.TenantContextHolder;
import org.alfresco.repo.web.scripts.TestWebScriptRepoServer;
import org.springframework.extensions.webscripts.Authenticator;
import org.springframework.extensions.webscripts.Description.RequiredAuthentication;
import org.springframework.extensions.webscripts.servlet.ServletAuthenticatorFactory;
import org.springframework.extensions.webscripts.servlet.WebScriptServletRequest;
import org.springframework.extensions.webscripts.servlet.WebScriptServletResponse;
import org.springframework.web.context.ServletContextAware;


/**
 * Stand-alone Web Script Test Server
 * 
 * @author davidc
 */
public class TenantTestWebScriptRepoServer extends TestWebScriptRepoServer
{
    @Override
    public void setServletAuthenticatorFactory(ServletAuthenticatorFactory factory)
    {
        super.setServletAuthenticatorFactory(new LocalTestRunAsAuthenticatorFactory());
    }
    
    
    public static class LocalTestRunAsAuthenticatorFactory implements ServletAuthenticatorFactory, ServletContextAware
    {
        private String tenant;

        public LocalTestRunAsAuthenticatorFactory()
        {
            this.tenant = TenantContextHolder.getTenantDomain(); 
        }
        
        @Override
        public void setServletContext(ServletContext context)
        {
        }
        
        @Override
        public Authenticator create(WebScriptServletRequest req, WebScriptServletResponse res)
        {
            String runAsUser = AuthenticationUtil.getRunAsUser();
            if (runAsUser == null)
            {
                runAsUser = AuthenticationUtil.getSystemUserName();
            }
            return new LocalTestRunAsAuthenticator(runAsUser);
        }
        
        public class LocalTestRunAsAuthenticator implements Authenticator
        {
            private String userName;
            
            public LocalTestRunAsAuthenticator(String userName)
            {
                this.userName = userName;
            }
            
            @Override
            public boolean authenticate(RequiredAuthentication required, boolean isGuest)
            {
                if (! emptyCredentials())
                {
                    AuthenticationUtil.setRunAsUser(userName);
                    if (tenant != null)
                    {
                        TenantContextHolder.setTenantDomain(tenant);
                    }
                    return true;
                }
                
                return false;
            }
            
            @Override
            public boolean emptyCredentials()
            {
                return (userName == null || userName.length() == 0);
            }
        }
    }    

}
