/**
 * Copyright (C) 2005-2013 Alfresco Software Limited.
 *
 * This file is part of the Spring Surf Extension project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.alfresco.module.org_alfresco_module_cloud.networkadmin;

import org.alfresco.module.org_alfresco_module_cloud.CloudModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.PersonService;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;

/**
 * Only allow Network Admins access to this kind of Web Script 
 */
public class NetworkAdmin
{
    private PersonService personService;
    private NodeService nodeService;

    public void setPersonService(PersonService service)
    {
        this.personService = service;
    }

    public void setNodeService(NodeService service)
    {
        this.nodeService = service;
    }
    
    /**
     * Checks current authentication is a Network Admin or an Alfresco System Admin.
     * 
     * @throws NetworkAdminException 
     */
    public void checkNetworkAdmin()
    {
        final String user = AuthenticationUtil.getRunAsUser();
        
        if ( !user.equals(AuthenticationUtil.getAdminUserName()))
        {
            NodeRef person = personService.getPerson(user, false);
            
            if (person == null || !nodeService.hasAspect(person, CloudModel.ASPECT_NETWORK_ADMIN))
            {
                throw new WebScriptException(Status.STATUS_UNAUTHORIZED, "Failed to execute Network Admin Web Script");
            }
        }
    }
    
    /**
     * Push Network Admin
     */
    public void pushNetworkAdmin()
    {
        checkNetworkAdmin();
        AuthenticationUtil.pushAuthentication();
        AuthenticationUtil.setRunAsUser(AuthenticationUtil.getAdminUserName());
    }
    
    /**
     * Pop Network Admin
     */
    public void popNetworkAdmin()
    {
        AuthenticationUtil.popAuthentication();
    }
    
    /**
     * Execute a unit of work as Network Admin
     * 
     * @param runAsWork    the unit of work to do
     * @return Returns     the work's return value
     */
    public <R> R runAs(final NetworkAdminRunAsWork<R> runAsWork)
    {
        checkNetworkAdmin();

        return AuthenticationUtil.runAs(new AuthenticationUtil.RunAsWork<R>()
        {
            public R doWork()
            {
                try
                {
                    return runAsWork.doWork();
                }
                catch (Throwable exception)
                {
                    // Re-throw the exception
                    if (exception instanceof RuntimeException)
                    {
                        throw (RuntimeException) exception;
                    }
                    throw new RuntimeException("Error during run as.", exception);
                }
            }
        }, AuthenticationUtil.getAdminUserName());
    }
    
    public interface NetworkAdminRunAsWork<Result>
    {
        /**
         * Method containing the work to be done
         * 
         * @return Return the result of the operation
         */
        Result doWork() throws Exception;
    }

}
