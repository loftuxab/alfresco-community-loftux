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
 * FLOSS exception.  You should have received a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.web.config.forms;

import java.io.Serializable;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.springframework.extensions.config.evaluator.Evaluator;
import org.springframework.extensions.surf.FrameworkUtil;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.ServletUtil;
import org.springframework.extensions.surf.exception.ConnectorServiceException;
import org.springframework.extensions.surf.support.ThreadLocalRequestContext;
import org.springframework.extensions.webscripts.connector.Connector;
import org.springframework.extensions.webscripts.connector.ConnectorService;
import org.springframework.extensions.webscripts.connector.Response;
import org.springframework.extensions.webscripts.connector.ResponseStatus;

/**
 * This class provides common behaviour for the evaluators which use 
 * a repo web script as part of their implementation.
 * 
 * @author Neil McErlean
 * @author Gavin Cornwell
 */
public abstract class ServiceBasedEvaluator implements Evaluator
{
    protected static final String ENDPOINT_ID = "alfresco";

    protected abstract Log getLogger();

    /**
     * Calls the given service
     * 
     * @param serviceUrl The service to call
     * @return The service response as a JSON string
     * @throws ConnectorServiceException
     */
    protected String callService(String serviceUrl) throws ConnectorServiceException
    {
        // Before making the remote call, we'll check the request-scoped cache in
        // ThreadLocalRequestContext.
        StringBuilder builder = new StringBuilder().append("forms.cache.").append(serviceUrl);
        String keyForCachedJson = builder.toString();
        
        Map<String, Serializable> valuesMap = ThreadLocalRequestContext.getRequestContext().getValuesMap();
        Serializable cachedResult = valuesMap.get(keyForCachedJson);
        if (cachedResult != null & cachedResult instanceof String)
        {
            if (getLogger().isDebugEnabled())
            {
                getLogger().debug("Retrieved cached response for " + serviceUrl);
            }
            return (String)cachedResult;
        }

        ConnectorService connService = FrameworkUtil.getConnectorService();
        
        RequestContext requestContext = ThreadLocalRequestContext.getRequestContext();
        String currentUserId = requestContext.getUserId();
        HttpSession currentSession = ServletUtil.getSession(true);
        Connector connector = connService.getConnector(ENDPOINT_ID, currentUserId, currentSession);

        Response r = connector.call(serviceUrl);
        
        // check that the service call did not return unauthorized
        if (r.getStatus().getCode() == ResponseStatus.STATUS_UNAUTHORIZED)
        {
           throw new NotAuthenticatedException();
        }

        String jsonResponseString = r.getResponse();
        
        // Cache the jsonResponseString in the RequestContext
        if (getLogger().isDebugEnabled())
        {
            getLogger().debug("Caching response for " + serviceUrl + ":\n" + jsonResponseString);
        }
        
        ThreadLocalRequestContext.getRequestContext().setValue(keyForCachedJson, jsonResponseString);
        
        return jsonResponseString;
    }
    
    /**
     * Marker exception to indicate that authentication failed
     *
     * @author Gavin Cornwell
     */
    class NotAuthenticatedException extends RuntimeException 
    {
        private static final long serialVersionUID = -4906852539344031273L;
    }
}