/*
 * Copyright (C) 2005-2007 Alfresco Software Limited.
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 * 
 * As a special exception to the terms and conditions of version 2.0 of the GPL,
 * you may redistribute this Program in connection with Free/Libre and Open
 * Source Software ("FLOSS") applications as described in Alfresco's FLOSS
 * exception. You should have recieved a copy of the text describing the FLOSS
 * exception, and it is also available here:
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.web.scripts;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.alfresco.web.site.RequestContext;
import org.alfresco.web.site.RequestUtil;

/**
 * @author muzquiano
 */
public class DeclarativeSiteWebScript extends DeclarativeJSONWebScript
{
    protected Store store;

    public DeclarativeSiteWebScript()
    {
        super();
    }

    public DeclarativeSiteWebScript(Store store)
    {
        super();
        this.store = store;
    }

    public void setStore(Store store)
    {
        this.store = store;
    }

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
            params.put("site", scriptSite);
        }
        return params;
    }

    protected RequestContext getRequestContext(WebScriptRequest req)
    {
        // avm store id
        String avmStoreId = req.getParameter("avmStoreId");

        // make the request available
        HttpServletRequest request = null;
        if (req instanceof org.alfresco.web.scripts.servlet.WebScriptServletRequest)
        {
            request = ((org.alfresco.web.scripts.servlet.WebScriptServletRequest) req).getHttpServletRequest();

            RequestContext context = RequestUtil.getRequestContext(request);
            return context;
        }
        return null;

        /*
        // construct a local request context
        ScriptRequestContextFactory factory = new ScriptRequestContextFactory();
        RequestContext context = factory.newInstance(getStore(), avmStoreId,
                request);
        return context;
        */
    }

}
