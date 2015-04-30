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
package org.alfresco.module.org_alfresco_module_cloud.users.webscripts;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_cloud.CloudModel;
import org.alfresco.module.org_alfresco_module_cloud.accounts.Account;
import org.alfresco.module.org_alfresco_module_cloud.accounts.AccountService;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.web.scripts.TenantWebScriptServletRequest;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.PersonService;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 * This class is the controller for the user-network.get web script.
 * 
 * @author Neil Mc Erlean
 * @since Alfresco Cloud Module (Thor)
 */
public class UserNetworkGet extends DeclarativeWebScript
{
    private NodeService nodeService;
    private PersonService personService;
    private AccountService accountService;
    
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }
    
    public void setPersonService(PersonService service)
    {
        this.personService = service;
    }
    
    public void setAccountService(AccountService service)
    {
        this.accountService = service;
    }
    
    
    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache)
    {
        if (!(req instanceof TenantWebScriptServletRequest))
        {
            throw new WebScriptException("Request is not a tenant aware request");
        }

        Map<String, Object> model = new HashMap<String, Object>();

        // extract person info
        String fullyAuthenticatedUser = AuthenticationUtil.getFullyAuthenticatedUser();
        NodeRef personNode = personService.getPerson(fullyAuthenticatedUser);
        model.put("userName", fullyAuthenticatedUser);
        model.put("serviceContext", req.getServiceContextPath());
        model.put("isExternal", nodeService.hasAspect(personNode, CloudModel.ASPECT_EXTERNAL_PERSON));
        model.put("isNetworkAdmin", nodeService.hasAspect(personNode, CloudModel.ASPECT_NETWORK_ADMIN));

        // get avatar information
        List<AssociationRef> avatorAssocs = nodeService.getTargetAssocs(personNode, ContentModel.ASSOC_AVATAR);
        if(avatorAssocs.size() > 0)
        {
        	AssociationRef ref = avatorAssocs.get(0);
        	model.put("avatar", "api/node/" + ref.getTargetRef().toString().replace("://","/") + "/content/thumbnails/avatar");
        }

        // extract account info
        String tenant = ((TenantWebScriptServletRequest)req).getTenant();
        Account account = accountService.getAccountByDomain(tenant);
        if (account != null)
        {
            model.put("accountType", account.getType());
        }
        
        return model;
    }
}
