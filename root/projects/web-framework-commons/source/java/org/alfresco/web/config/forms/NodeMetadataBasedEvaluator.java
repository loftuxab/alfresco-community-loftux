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
package org.alfresco.web.config.forms;

import java.io.Serializable;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
 * This class provides common behaviour for the evaluators which use node-based
 * metadata from a web repo web script as part of their implementation.
 * 
 * @author Neil McErlean
 */
public abstract class NodeMetadataBasedEvaluator implements Evaluator
{
    protected static final String ENDPOINT_ID = "alfresco";
    protected static final Pattern nodeRefPattern = Pattern.compile(".+://.+/.+");

    protected abstract Log getLogger();

    /**
     * This method checks if the specified condition is matched by the specified
     * jsonResponse String.
     * 
     * @return true if there is a match, else false.
     */
    protected abstract boolean checkJsonAgainstCondition(String condition, String jsonResponseString);

    /**
     * Determines whether the given node type matches the path of the given
     * object
     * 
     * @see org.springframework.extensions.config.evaluator.Evaluator#applies(java.lang.Object,
     *      java.lang.String)
     */
    public boolean applies(Object obj, String condition)
    {
        boolean result = false;

        if (obj instanceof String)
        {
            String objAsString = (String) obj;
            Matcher m = nodeRefPattern.matcher(objAsString);
            if (m.matches())
            {
                try
                {
                    String jsonResponseString = callMetadataService(objAsString);

                    result = checkJsonAgainstCondition(condition, jsonResponseString);
                }
                catch (NotAuthenticatedException ne)
                {
                   // ignore the fact that the lookup failed, the form UI component
                   // will handle this and return the appropriate status code.
                }
                catch (ConnectorServiceException e)
                {
                    if (getLogger().isWarnEnabled())
                    {
                        getLogger().warn("Failed to connect to metadata service.", e);
                    }
                }
            }
        }

        return result;
    }

    private String callMetadataService(String nodeString) throws ConnectorServiceException
    {
        // Before making the remote call, we'll check the request-scoped cache in
        // ThreadLocalRequestContext.
        StringBuilder builder = new StringBuilder().append("forms.cache.").append(nodeString);
        String keyForCachedJson = builder.toString();
        
        Map<String, Serializable> valuesMap = ThreadLocalRequestContext.getRequestContext().getValuesMap();
        Serializable cachedResult = valuesMap.get(keyForCachedJson);
        if (cachedResult != null & cachedResult instanceof String)
        {
            if (getLogger().isDebugEnabled())
            {
                getLogger().debug("Retrieved cached metadata for " + nodeString);
            }
            return (String)cachedResult;
        }

        ConnectorService connService = FrameworkUtil.getConnectorService();
        
        RequestContext requestContext = ThreadLocalRequestContext.getRequestContext();
        String currentUserId = requestContext.getUserId();
        HttpSession currentSession = ServletUtil.getSession(true);
        Connector connector = connService.getConnector(ENDPOINT_ID, currentUserId, currentSession);

        Response r = connector.call("/api/metadata?nodeRef=" + nodeString + "&shortQNames=true");

        // check that the service call did not return unauthorized
        if (r.getStatus().getCode() == ResponseStatus.STATUS_UNAUTHORIZED)
        {
           throw new NotAuthenticatedException();
        }
        
        String jsonResponseString = r.getResponse();
        
        // Cache the jsonResponseString in the RequestContext
        if (getLogger().isDebugEnabled())
        {
            getLogger().debug("Caching metadata for " + nodeString);
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
      private static final long serialVersionUID = 9136927085864150503L;
    }
}