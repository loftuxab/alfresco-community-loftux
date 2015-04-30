/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
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
package org.alfresco.module.org_alfresco_module_cloud.emailaddress.webscripts;

import java.util.HashMap;
import java.util.Map;

import org.alfresco.module.org_alfresco_module_cloud.emailaddress.domain.InvalidDomainException;
import org.alfresco.util.ParameterCheck;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 * This is the webscript controller for the invalid-domain.delete webscript.
 * 
 * @author Neil Mc Erlean
 * @since Thor Phase 2 Sprint 1
 */
public class InvalidDomainDelete extends AbstractInvalidDomainWebscript
{
    @Override protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache)
    {
        final String domain = getDomainFromURL(req);
        
        ParameterCheck.mandatoryString(PARAM_DOMAIN, domain);
        
        try
        {
            emailAddressService.deleteInvalidDomain(domain);
        } catch (InvalidDomainException ide)
        {
            throw new WebScriptException(Status.STATUS_NOT_FOUND, "Cannot delete invalid domain entry. Entry exists.", ide);
        }
        
        return new HashMap<String, Object>();
    }
}
