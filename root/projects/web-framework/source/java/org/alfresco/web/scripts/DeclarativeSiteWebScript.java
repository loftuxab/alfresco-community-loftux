/*
 * Copyright (C) 2005-2008 Alfresco Software Limited.
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
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.web.scripts;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.alfresco.web.site.RequestContext;
import org.alfresco.web.site.RequestUtil;
import org.alfresco.web.site.exception.RequestContextException;

/**
 * Declarative web script implementation that automatically self-provisions
 * with the Web Framework "site" root objects.
 * 
 * @author muzquiano
 */
public class DeclarativeSiteWebScript extends DeclarativeJSONWebScript
{
    
    private static final String ROOT_SCOPE_SITE = "site";
    /** The store. */
    protected Store store;

    /**
     * Instantiates a new declarative site web script.
     */
    public DeclarativeSiteWebScript()
    {
    }

    /**
     * Instantiates a new declarative site web script.
     * 
     * @param store the store
     */
    public DeclarativeSiteWebScript(Store store)
    {
        this.store = store;
    }

    /**
     * Sets the store.
     * 
     * @param store the new store
     */
    public void setStore(Store store)
    {
        this.store = store;
    }

    /**
     * Gets the store.
     * 
     * @return the store
     */
    public Store getStore()
    {
        return this.store;
    }

    /**
     * This is overridden so that all web script implementation classes that
     * inherit from this one automatically have a "site" root object.
     * 
     * This also makes sure that the "site" root object is set up with an
     * appropriate request context implementation that uses the correct store.
     * 
     * @param req the req
     * @param res the res
     * @param customParams the custom params
     * 
     * @return the map< string, object>
     */
    protected Map<String, Object> createScriptParameters(WebScriptRequest req,
            WebScriptResponse res, Map<String, Object> customParams)
    {
        Map<String, Object> params = super.createScriptParameters(req, res,
                customParams);
        RequestContext context = getRequestContext(req);
        if (context != null)
        {
            ScriptSite scriptSite = new ScriptSite(context);
            params.put(ROOT_SCOPE_SITE, scriptSite);
        }
        return params;
    }

    /**
     * Gets the request context.
     * 
     * @param req the req
     * 
     * @return the request context
     */
    protected RequestContext getRequestContext(WebScriptRequest req)
    {
        RequestContext context = null;
        try
        {
            if (req instanceof org.alfresco.web.scripts.servlet.WebScriptServletRequest)
            {
                HttpServletRequest request = ((org.alfresco.web.scripts.servlet.WebScriptServletRequest) req).getHttpServletRequest();
                context = RequestUtil.getRequestContext(request);
            }
        }
        catch(RequestContextException rce)
        {
            /**
             * If we cannot acquire a request context for the current
             * request, then we cannot supply the "site" object
             */
            logger.debug("Unable to place 'site' root scope object into web script");
            logger.debug(rce);
        }
        return context;
    }
}
