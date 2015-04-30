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

import org.alfresco.module.org_alfresco_module_cloud.emailaddress.DomainValidityCheck.FailureReason;
import org.alfresco.module.org_alfresco_module_cloud.emailaddress.domain.InvalidDomainException;
import org.alfresco.util.ParameterCheck;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 * This is the webscript controller for the invalid-domain.put webscript.
 * 
 * @author Neil Mc Erlean
 * @since Thor Phase 2 Sprint 1
 */
public class InvalidDomainPut extends AbstractInvalidDomainWebscript
{
    @Override protected Map<String, Object> executeImpl(WebScriptRequest req,
            Status status, Cache cache)
    {
        Map<String, String> postedData = extractInvalidDomainDataFromReqBody(req);
        
        ParameterCheck.mandatoryString(PARAM_DOMAIN, postedData.get(PARAM_DOMAIN));
        ParameterCheck.mandatoryString(PARAM_TYPE, postedData.get(PARAM_TYPE));
        ParameterCheck.mandatory(PARAM_NOTES, postedData.get(PARAM_NOTES)); // Must not be null, but "" is ok.
        
        FailureReason type;
        try
        {
            type = FailureReason.valueOf(postedData.get(PARAM_TYPE));
        } catch (IllegalArgumentException iae)
        {
            throw new WebScriptException(Status.STATUS_BAD_REQUEST, "Did not recognise invalid domain type.", iae);
        }
        
        try
        {
            emailAddressService.updateInvalidDomain(postedData.get(PARAM_DOMAIN), type, postedData.get(PARAM_NOTES));
        } catch (InvalidDomainException ide)
        {
            throw new WebScriptException(Status.STATUS_NOT_FOUND, "Cannot update invalid domain entry. Entry doesn't exist.", ide);
        }
        
        return new HashMap<String, Object>();
    }
}
